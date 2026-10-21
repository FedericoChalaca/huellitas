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

/** Pestaña Más: accesos directos, ajustes, créditos y la pantalla que explica qué es de mentira. */
@Composable
fun MasScreen(nav: NavController) {
    TabScaffold(nav, "mas", "Más") { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MenuCard("🚨", "Urgencias 24 h", "Clínicas abiertas ahora") { nav.navigate("urgencias") }
            MenuCard("💊", "Medicamentos", "Cotiza con el descuento de tu plan") { nav.navigate("medicamentos") }
            MenuCard("⚙️", "Ajustes", "Tema, avisos y tu cuenta") { nav.navigate("settings") }
            MenuCard("ℹ️", "Créditos", "Quién hizo esta app") { nav.navigate("credits") }
            MenuCard("🎓", "Acerca de la demo", "Qué es real y qué no") { nav.navigate("acerca") }
        }
    }
}
