@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.citas

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.TabScaffold

/** Pestaña Citas. Tarea F-10 (Federico): la lista de próximas citas y el historial. */
@Composable
fun CitasScreen(nav: NavController) {
    TabScaffold(
        nav, "citas", "Mis citas",
        fab = {
            FloatingActionButton(onClick = { nav.navigate("cita/pedir/0/-") }) {
                Icon(Icons.Filled.Add, contentDescription = "Pedir una cita")
            }
        },
    ) { padding ->
        EmptyState("🚧", "Mis citas", "Tarea F-10 (Federico): la lista de citas.", Modifier.padding(padding))
    }
}
