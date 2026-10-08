package com.huellitas.mascotas.ui.medicamentos

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/** Cotizaciones guardadas. Tareas de Nicolás: N-19 (lista) y N-20 (ver los renglones de una). */
@Composable
fun HistorialCotizacionesScreen(nav: NavController) {
    PantallaPendiente(nav, "Mis cotizaciones", "Tarea N-19 (Nicolás).")
}
