package com.huellitas.mascotas.ui.urgencias

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/** Primeros auxilios: 6 situaciones con consejos cortos en tarjetas. Tarea N-29 (Nicolás). */
@Composable
fun PrimerosAuxiliosScreen(nav: NavController) {
    PantallaPendiente(nav, "Primeros auxilios", "Tarea N-29 (Nicolás).")
}
