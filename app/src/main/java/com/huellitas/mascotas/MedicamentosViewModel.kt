package com.huellitas.mascotas

import android.app.Application

/**
 * Catálogo, carrito y cotizaciones de medicamentos. Archivo de Nicolás: tarjetas N-13, N-18, N-19 y N-20.
 * El carrito se comparte entre las pantallas Medicamentos y Cotización: pídelo con activityViewModel()
 * (ver ui/Placeholder.kt) para que las dos vean el mismo.
 */
class MedicamentosViewModel(app: Application) : OwnerViewModel(app) {
    // TODO(N-13): carrito: val carrito: StateFlow<List<LineaCotizacion>>, agregar(m), quitar(m), cambiarCantidad(m, n), vaciar()
    // TODO(N-18): suspend fun guardarCotizacion(petId: Long?, totales: Totales): Boolean
    // TODO(N-19): val cotizaciones: StateFlow<List<Quote>>
    // TODO(N-20): fun renglones(quoteId: Long): Flow<List<QuoteItem>>
}
