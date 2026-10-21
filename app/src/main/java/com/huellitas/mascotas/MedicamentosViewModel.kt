package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.LineaCotizacion
import com.huellitas.mascotas.data.Medicamento
import com.huellitas.mascotas.data.Quote
import com.huellitas.mascotas.data.QuoteItem
import com.huellitas.mascotas.data.Totales
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * El carrito de la cotización y las cotizaciones guardadas. El carrito se comparte entre las pantallas
 * Medicamentos y Cotización: se pide con activityViewModel() (ver ui/Placeholder.kt) para que las dos vean el mismo.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MedicamentosViewModel(app: Application) : OwnerViewModel(app) {
    private val _carrito = MutableStateFlow<List<LineaCotizacion>>(emptyList())
    val carrito: StateFlow<List<LineaCotizacion>> = _carrito.asStateFlow()

    /** Suma una unidad; si el medicamento ya estaba, solo sube la cantidad. */
    fun agregar(m: Medicamento) = _carrito.update { lista ->
        if (lista.any { it.medicamento.id == m.id }) lista.map { if (it.medicamento.id == m.id) it.copy(cantidad = it.cantidad + 1) else it }
        else lista + LineaCotizacion(m, 1)
    }

    /** Con una cantidad de 0 o menos, el renglón se quita. */
    fun cambiarCantidad(m: Medicamento, cantidad: Int) = _carrito.update { lista ->
        if (cantidad <= 0) lista.filterNot { it.medicamento.id == m.id }
        else lista.map { if (it.medicamento.id == m.id) it.copy(cantidad = cantidad) else it }
    }

    fun quitar(m: Medicamento) = cambiarCantidad(m, 0)

    fun vaciar() = _carrito.update { emptyList() }

    /** Guarda la cotización con sus renglones y vacía el carrito. Devuelve false si no hay nada que guardar. */
    suspend fun guardarCotizacion(petId: Long?, totales: Totales): Boolean {
        val owner = uid() ?: return false
        val lineas = _carrito.value
        if (lineas.isEmpty()) return false
        if (petId != null && db.pets().get(petId, owner) == null) return false
        val quote = Quote(ownerId = owner, petId = petId, createdDay = Dates.today(), totalCop = totales.subtotalCop, coveredCop = totales.coberturaCop)
        val items = lineas.map {
            QuoteItem(quoteId = 0, medicineId = it.medicamento.id, name = it.medicamento.nombre, unitPriceCop = it.medicamento.precioCop, quantity = it.cantidad)
        }
        db.quotes().saveWithItems(quote, items)
        vaciar()
        return true
    }

    val cotizaciones: StateFlow<List<Quote>> = ownerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else db.quotes().observeAll(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun renglones(quoteId: Long): Flow<List<QuoteItem>> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(emptyList()) else db.quotes().observeItems(quoteId, owner) }

    fun borrarCotizacion(id: Long) {
        viewModelScope.launch {
            val owner = uid() ?: return@launch
            db.quotes().delete(id, owner)
        }
    }
}
