package com.huellitas.mascotas.ui.medicamentos

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/** La cotización en curso (el carrito). Tareas de Nicolás: N-15 (renglones), N-17 (totales) y N-18 (guardar). */
@Composable
fun CotizacionScreen(nav: NavController) {
    PantallaPendiente(nav, "Mi cotización", "Tarea N-15 (Nicolás).")
}
