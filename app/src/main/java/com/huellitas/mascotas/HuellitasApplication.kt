package com.huellitas.mascotas

import android.app.Application
import com.huellitas.mascotas.data.AppDatabase
import com.huellitas.mascotas.data.Prefs

/** Aquí viven las piezas que comparte toda la app: la base de datos y los ajustes. */
class HuellitasApplication : Application() {
    val db: AppDatabase by lazy { AppDatabase.create(this) }
    val prefs: Prefs by lazy { Prefs(this) }
}
