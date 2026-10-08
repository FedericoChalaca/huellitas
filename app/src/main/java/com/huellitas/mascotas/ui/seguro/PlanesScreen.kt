package com.huellitas.mascotas.ui.seguro

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/** Comparación de los tres planes (datos en data/Insurer.kt, PLANES). Tarea N-21 (Nicolás). */
@Composable
fun PlanesScreen(nav: NavController) {
    PantallaPendiente(nav, "Planes del seguro", "Tarea N-21 (Nicolás).")
}
