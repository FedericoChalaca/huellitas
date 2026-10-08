package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.AppointmentWithPet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/**
 * Citas con las clínicas aliadas. Lo de crear una cita es de Federico (F-03);
 * cancelar y reprogramar son de Nicolás (N-25 y N-26).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CitasViewModel(app: Application) : OwnerViewModel(app) {
    /** Todas las citas, ordenadas por día y hora. */
    val citas: StateFlow<List<AppointmentWithPet>> = ownerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else db.appointments().observeAll(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cita(id: Long): Flow<AppointmentWithPet?> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(null) else db.appointments().observe(id, owner) }

    // TODO(F-03): suspend fun pedirCita(...): Boolean
    // TODO(N-25): fun cancelar(id: Long)
    // TODO(N-26): suspend fun reprogramar(id: Long, day: Long, slot: String): Boolean
}
