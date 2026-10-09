package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Policy
import com.huellitas.mascotas.data.PolicyWithPet
import com.huellitas.mascotas.data.numeroDePoliza
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.data.usoDeEjemplo
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Pólizas de las mascotas. Afiliar ya está hecho (Federico); mostrar el detalle es de Nicolás (N-22 y N-23). */
@OptIn(ExperimentalCoroutinesApi::class)
class SeguroViewModel(app: Application) : OwnerViewModel(app) {
    /** Las pólizas de todas las mascotas que tienen una. */
    val polizas: StateFlow<List<PolicyWithPet>> = ownerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else db.policies().observeAll(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** La póliza de una mascota, o null si no la tiene. */
    fun poliza(petId: Long): Flow<Policy?> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(null) else db.policies().observeForPet(petId, owner) }

    /**
     * Afilia la mascota al plan. Si ya tenía póliza solo cambia el plan: el número, la vigencia y
     * lo usado se conservan. Devuelve false si la mascota no es de esta cuenta o el plan no existe.
     */
    suspend fun afiliar(petId: Long, planId: String): Boolean {
        val owner = uid() ?: return false
        val plan = planPorId(planId) ?: return false
        if (db.pets().get(petId, owner) == null) return false
        val hoy = Dates.today()
        val actual = db.policies().forPet(petId, owner)
        db.policies().upsert(
            actual?.copy(planId = plan.id) ?: Policy(
                petId = petId,
                planId = plan.id,
                number = numeroDePoliza(LocalDate.ofEpochDay(hoy).year, petId),
                startDay = hoy,
                endDay = Dates.addMonths(hoy, 12),
                usedCop = usoDeEjemplo(plan, petId),
            ),
        )
        return true
    }
}
