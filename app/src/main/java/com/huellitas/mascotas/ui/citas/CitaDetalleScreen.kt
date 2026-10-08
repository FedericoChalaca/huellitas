package com.huellitas.mascotas.ui.citas

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/** Detalle de una cita. Tareas de Nicolás: N-24 (mostrar), N-25 (cancelar) y N-26 (reprogramar). */
@Composable
fun CitaDetalleScreen(nav: NavController, citaId: Long) {
    PantallaPendiente(nav, "Detalle de la cita", "Tarea N-24 (Nicolás). Cita número $citaId.")
}
