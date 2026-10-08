package com.huellitas.mascotas.ui.seguro

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/** Póliza de una mascota. Tareas de Nicolás: N-22 (datos) y N-23 (coberturas y barra de uso). */
@Composable
fun PolizaScreen(nav: NavController, petId: Long) {
    PantallaPendiente(nav, "Póliza", "Tarea N-22 (Nicolás). Mascota número $petId.")
}
