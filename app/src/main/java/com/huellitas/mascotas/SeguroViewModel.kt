package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.Policy
import com.huellitas.mascotas.data.PolicyWithPet
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Pólizas de las mascotas. Afiliar es de Federico (F-08); mostrar el detalle es de Nicolás (N-22 y N-23). */
@OptIn(ExperimentalCoroutinesApi::class)
class SeguroViewModel(app: Application) : OwnerViewModel(app) {
    /** Las pólizas de todas las mascotas que tienen una. */
    val polizas: StateFlow<List<PolicyWithPet>> = ownerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else db.policies().observeAll(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** La póliza de una mascota, o null si no la tiene. */
    fun poliza(petId: Long): Flow<Policy?> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(null) else db.policies().observeForPet(petId, owner) }

    // TODO(F-08): suspend fun afiliar(petId: Long, planId: String): Boolean
}
