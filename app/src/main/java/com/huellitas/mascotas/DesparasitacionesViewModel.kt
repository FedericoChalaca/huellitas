package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.Deworming
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/** Desparasitaciones de las mascotas: el mismo patrón que las vacunas en MainViewModel. */
@OptIn(ExperimentalCoroutinesApi::class)
class DesparasitacionesViewModel(app: Application) : OwnerViewModel(app) {
    fun desparasitaciones(petId: Long): Flow<List<Deworming>> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(emptyList()) else db.dewormings().observeForPet(petId, owner) }

    suspend fun get(id: Long): Deworming? = uid()?.let { db.dewormings().get(id, it) }

    /** Devuelve false si la mascota no es de esta cuenta o si la desparasitación que se edita no existe. */
    suspend fun guardar(d: Deworming): Boolean {
        val owner = uid() ?: return false
        if (db.pets().get(d.petId, owner) == null) return false
        if (d.id != 0L) {
            if (db.dewormings().get(d.id, owner) == null) return false
        } else {
            // Una dosis nueva del mismo producto reemplaza la fecha "próxima" de las anteriores.
            db.dewormings().clearNextDue(d.petId, d.product)
        }
        db.dewormings().upsert(d)
        return true
    }

    fun borrar(id: Long) {
        viewModelScope.launch {
            val owner = uid() ?: return@launch
            db.dewormings().delete(id, owner)
        }
    }
}
