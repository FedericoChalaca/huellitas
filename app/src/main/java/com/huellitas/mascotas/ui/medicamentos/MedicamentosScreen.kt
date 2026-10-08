package com.huellitas.mascotas.ui.medicamentos

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/**
 * Catálogo de medicamentos para cotizar. Tareas de Nicolás: N-09 (lista), N-10 (buscador),
 * N-11 (categorías), N-12 (detalle) y N-14 (botón del carrito).
 */
@Composable
fun MedicamentosScreen(nav: NavController) {
    PantallaPendiente(nav, "Cotizar medicamentos", "Tarea N-09 (Nicolás).")
}
