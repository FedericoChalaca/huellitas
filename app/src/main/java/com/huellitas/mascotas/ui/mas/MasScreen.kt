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
import com.huellitas.mascotas.ui.SeccionTitulo
import com.huellitas.mascotas.ui.TabScaffold
import com.huellitas.mascotas.ui.aparecer

/** Pestaña Más: accesos directos, ajustes, créditos y la pantalla que explica qué es de mentira. */
@Composable
fun MasScreen(nav: NavController) {
    TabScaffold(nav, "mas", "Más", subtitle = "Ajustes, ayuda y servicios") { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SeccionTitulo("Servicios", Modifier.aparecer())
            MenuCard("🚨", "Urgencias 24 h", "Clínicas abiertas ahora", Modifier.aparecer(1)) { nav.navigate("urgencias") }
            MenuCard("💊", "Medicamentos", "Cotiza con el descuento de tu plan", Modifier.aparecer(2)) { nav.navigate("medicamentos") }
            SeccionTitulo("La app", Modifier.padding(top = 8.dp).aparecer(3))
            MenuCard("⚙️", "Ajustes", "Tema, avisos y tu cuenta", Modifier.aparecer(4)) { nav.navigate("settings") }
            MenuCard("ℹ️", "Créditos", "Quién hizo esta app", Modifier.aparecer(5)) { nav.navigate("credits") }
            MenuCard("🎓", "Acerca de la demo", "Qué es real y qué no", Modifier.aparecer(6)) { nav.navigate("acerca") }
        }
    }
}
