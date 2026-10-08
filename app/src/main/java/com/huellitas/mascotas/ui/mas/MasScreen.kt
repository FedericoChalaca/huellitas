package com.huellitas.mascotas.ui.mas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.MenuCard
import com.huellitas.mascotas.ui.TabScaffold

/**
 * Pestaña Más: ajustes, créditos y lo que falte. Tareas de Nicolás: N-30 (completar el menú),
 * N-31 (que Ajustes y Créditos tengan flecha de volver) y N-32 (pantalla "Acerca de la demo").
 */
@Composable
fun MasScreen(nav: NavController) {
    TabScaffold(nav, "mas", "Más") { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MenuCard("⚙️", "Ajustes", "Tema, avisos y tu cuenta") { nav.navigate("settings") }
            MenuCard("ℹ️", "Créditos", "Quién hizo esta app") { nav.navigate("credits") }
            // TODO(N-32): MenuCard "Acerca de la demo"
        }
    }
}
