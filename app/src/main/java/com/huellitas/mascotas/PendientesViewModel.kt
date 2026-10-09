package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.Pendiente
import com.huellitas.mascotas.data.unirPendientes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

/** Lo que está por aplicarse: vacunas y desparasitaciones con fecha de próxima dosis, en una sola lista. */
@OptIn(ExperimentalCoroutinesApi::class)
class PendientesViewModel(app: Application) : OwnerViewModel(app) {
    val pendientes: StateFlow<List<Pendiente>> = ownerId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList())
            else combine(db.vaccines().observeUpcoming(id), db.dewormings().observeUpcoming(id), ::unirPendientes)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
