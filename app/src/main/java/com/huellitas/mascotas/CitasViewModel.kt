package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.Appointment
import com.huellitas.mascotas.data.AppointmentStatus
import com.huellitas.mascotas.data.AppointmentWithPet
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.MEDICINA_GENERAL
import com.huellitas.mascotas.data.clinicaDeUrgencias
import com.huellitas.mascotas.data.errorDeCita
import com.huellitas.mascotas.data.hora
import java.time.LocalTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Citas con las clínicas aliadas. Pedir una cita ya está hecho (Federico);
 * y también cancelar y reprogramar.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CitasViewModel(app: Application) : OwnerViewModel(app) {
    /** Todas las citas, ordenadas por día y hora. */
    val citas: StateFlow<List<AppointmentWithPet>> = ownerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else db.appointments().observeAll(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun cita(id: Long): Flow<AppointmentWithPet?> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(null) else db.appointments().observe(id, owner) }

    /**
     * Guarda la cita y devuelve su id. Devuelve null si la mascota no es de esta cuenta o si la cita
     * no cumple las reglas de [errorDeCita] (la pantalla ya le mostró el motivo a la persona).
     */
    suspend fun pedirCita(petId: Long, motivo: String, especialidad: String, clinicaId: String, day: Long, slot: String, notas: String): Long? {
        val owner = uid() ?: return null
        if (db.pets().get(petId, owner) == null) return null
        if (errorDeCita(motivo, especialidad, clinicaId, day, slot, Dates.today()) != null) return null
        return db.appointments().upsert(
            Appointment(
                petId = petId, reason = motivo, specialty = especialidad, clinicId = clinicaId,
                day = day, slot = slot, urgent = motivo == "Urgencia", notes = notas.trim(),
            ),
        )
    }

    /** Urgencia: atención para ya mismo en la clínica 24 horas más cercana. Queda confirmada de una vez. */
    suspend fun pedirUrgencia(petId: Long): Long? {
        val owner = uid() ?: return null
        if (db.pets().get(petId, owner) == null) return null
        val ahora = LocalTime.now()
        return db.appointments().upsert(
            Appointment(
                petId = petId, reason = "Urgencia", specialty = MEDICINA_GENERAL, clinicId = clinicaDeUrgencias().id,
                day = Dates.today(), slot = hora(ahora.hour, ahora.minute),
                status = AppointmentStatus.CONFIRMED, urgent = true,
            ),
        )
    }

    /** La cita no se borra: queda con estado «Cancelada» y pasa al historial. */
    fun cancelar(id: Long) {
        viewModelScope.launch {
            val owner = uid() ?: return@launch
            db.appointments().setStatus(id, AppointmentStatus.CANCELLED, owner)
        }
    }

    /** Cambia el día y la hora de una cita vigente; vuelve a quedar «Solicitada». Devuelve false si no se puede. */
    suspend fun reprogramar(id: Long, day: Long, slot: String): Boolean {
        val owner = uid() ?: return false
        val actual = db.appointments().get(id, owner) ?: return false
        if (actual.status != AppointmentStatus.REQUESTED && actual.status != AppointmentStatus.CONFIRMED) return false
        if (errorDeCita(actual.reason, actual.specialty, actual.clinicId, day, slot, Dates.today()) != null) return false
        db.appointments().upsert(actual.copy(day = day, slot = slot, status = AppointmentStatus.REQUESTED))
        return true
    }
}
