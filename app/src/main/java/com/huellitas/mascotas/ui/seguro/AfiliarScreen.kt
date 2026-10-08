package com.huellitas.mascotas.ui.seguro

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/** Afiliar una mascota: elegir plan y crear su póliza. Tarea F-08 (Federico). */
@Composable
fun AfiliarScreen(nav: NavController, petId: Long) {
    PantallaPendiente(nav, "Afiliar mascota", "Tarea F-08 (Federico). Mascota número $petId.")
}
