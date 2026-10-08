package com.huellitas.mascotas.ui.desparasitacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.PantallaPendiente

/**
 * Formulario para registrar o editar una desparasitación. Tareas N-04 y N-05 (Nicolás).
 * [id] es 0 si es una nueva. Copia el patrón de VaccineFormScreen en ui/PetScreens.kt.
 */
@Composable
fun DesparasitacionFormScreen(nav: NavController, petId: Long, id: Long) {
    PantallaPendiente(nav, "Registrar desparasitación", "Tarea N-04 (Nicolás). Mascota $petId, desparasitación $id.")
}
