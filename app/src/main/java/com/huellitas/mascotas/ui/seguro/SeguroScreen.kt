@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.seguro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.SeguroViewModel
import com.huellitas.mascotas.data.ASEGURADORA
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Pet
import com.huellitas.mascotas.data.Policy
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.MenuCard
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.TabScaffold
import com.huellitas.mascotas.ui.activityViewModel

/** Pestaña Seguro: la póliza de cada mascota (o el botón para afiliarla) y el menú de servicios. */
@Composable
fun SeguroScreen(nav: NavController, vm: SeguroViewModel = viewModel()) {
    val main = activityViewModel<MainViewModel>()
    val pets by main.pets.collectAsStateWithLifecycle()
    val polizas by vm.polizas.collectAsStateWithLifecycle()

    TabScaffold(nav, "seguro", ASEGURADORA) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Demostración académica: $ASEGURADORA no es una aseguradora real y aquí no se cobra nada.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (pets.isEmpty()) {
                Text("Agrega una mascota para poder afiliarla.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            pets.forEach { pet ->
                PolizaCard(
                    pet, polizas.firstOrNull { it.policy.petId == pet.id }?.policy,
                    onVer = { nav.navigate("seguro/poliza/${pet.id}") },
                    onAfiliar = { nav.navigate("seguro/afiliar/${pet.id}") },
                )
            }
            MenuCard("🚨", "Urgencias 24 h", "Clínicas abiertas ahora y atención inmediata") { nav.navigate("urgencias") }
            MenuCard("💊", "Cotizar medicamentos", "Arma una cotización con el descuento de tu plan") { nav.navigate("medicamentos") }
            MenuCard("🧾", "Mis cotizaciones", "Lo que ya cotizaste") { nav.navigate("cotizaciones") }
            MenuCard("🛡️", "Planes del seguro", "Compara Básico, Plus y Premium") { nav.navigate("seguro/planes") }
            MenuCard("🩹", "Primeros auxilios", "Qué hacer mientras llegas a la clínica") { nav.navigate("urgencias/auxilios") }
        }
    }
}

@Composable
private fun PolizaCard(pet: Pet, poliza: Policy?, onVer: () -> Unit, onAfiliar: () -> Unit) {
    val plan = poliza?.let { planPorId(it.planId) }
    Card(onClick = if (poliza != null) onVer else onAfiliar, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SpeciesBadge(speciesEmoji(pet.species), size = 48)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(pet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (poliza != null && plan != null) {
                    Text("Plan ${plan.nombre} · cubre el ${plan.coberturaPct} %", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "Vigente hasta el ${Dates.format(poliza.endDay)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        "Usado: ${pesos(poliza.usedCop)} de ${pesos(plan.limiteAnualCop)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text("Sin póliza", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            FilledTonalButton(onClick = if (poliza != null) onVer else onAfiliar) { Text(if (poliza != null) "Ver" else "Afiliar") }
        }
    }
}
