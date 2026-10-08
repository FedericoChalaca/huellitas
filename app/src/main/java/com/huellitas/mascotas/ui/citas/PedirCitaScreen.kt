package com.huellitas.mascotas.ui.citas

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.data.motivoDeRuta
import com.huellitas.mascotas.ui.PantallaPendiente

/**
 * Pedir una cita. Tarea F-04 (Federico).
 * [petId] es 0 si no viene una mascota elegida; [motivo] es la palabra de la ruta ("vacunacion"…) o "-".
 */
@Composable
fun PedirCitaScreen(nav: NavController, petId: Long, motivo: String) {
    PantallaPendiente(
        nav, "Pedir cita",
        "Tarea F-04 (Federico). Llegó: mascota $petId, motivo ${motivoDeRuta(motivo) ?: "ninguno"}.",
    )
}
