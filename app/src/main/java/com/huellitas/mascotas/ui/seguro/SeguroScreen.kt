package com.huellitas.mascotas.ui.seguro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huellitas.mascotas.data.ASEGURADORA
import com.huellitas.mascotas.ui.MenuCard
import com.huellitas.mascotas.ui.TabScaffold

/**
 * Pestaña Seguro. Hoy es solo un menú para llegar a cada pantalla.
 * Tarea F-07 (Federico): arriba, una tarjeta por mascota con el estado de su póliza.
 */
@Composable
fun SeguroScreen(nav: NavController) {
    TabScaffold(nav, "seguro", ASEGURADORA) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // TODO(F-07): tarjetas de pólizas por mascota (SeguroViewModel.polizas)
            MenuCard("🚨", "Urgencias 24 h", "Clínicas abiertas ahora y atención inmediata") { nav.navigate("urgencias") }
            MenuCard("💊", "Cotizar medicamentos", "Arma una cotización con el descuento de tu plan") { nav.navigate("medicamentos") }
            MenuCard("🧾", "Mis cotizaciones", "Lo que ya cotizaste") { nav.navigate("cotizaciones") }
            MenuCard("🛡️", "Planes del seguro", "Compara Básico, Plus y Premium") { nav.navigate("seguro/planes") }
            MenuCard("🩹", "Primeros auxilios", "Qué hacer mientras llegas a la clínica") { nav.navigate("urgencias/auxilios") }
        }
    }
}
