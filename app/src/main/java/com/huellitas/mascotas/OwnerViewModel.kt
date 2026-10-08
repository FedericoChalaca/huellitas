package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Base de los ViewModels de cada función (citas, seguro, medicamentos…): ya trae la base de datos,
 * los ajustes y el id de la cuenta que tiene la sesión abierta. Así cada persona trabaja en su
 * propio ViewModel y no hay que tocar MainViewModel.
 */
abstract class OwnerViewModel(app: Application) : AndroidViewModel(app) {
    protected val db = (app as HuellitasApplication).db
    protected val prefs = (app as HuellitasApplication).prefs

    /** Id de la cuenta con sesión abierta (null si no hay). Todas las consultas se filtran con él. */
    protected val ownerId: Flow<Long?> = prefs.userId

    protected suspend fun uid(): Long? = prefs.userId.first()
}
