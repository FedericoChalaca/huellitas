@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.seguro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.SeguroViewModel
import com.huellitas.mascotas.data.PLANES
import com.huellitas.mascotas.data.Plan
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.activityViewModel
import kotlinx.coroutines.launch

/** Afiliar una mascota a un plan o, si ya tiene póliza, cambiarle el plan. */
@Composable
fun AfiliarScreen(nav: NavController, petId: Long, vm: SeguroViewModel = viewModel()) {
    val main = activityViewModel<MainViewModel>()
    val pet by remember(petId) { main.pet(petId) }.collectAsStateWithLifecycle(initialValue = null)
    val actual by remember(petId) { vm.poliza(petId) }.collectAsStateWithLifecycle(initialValue = null)
    var planElegido by rememberSaveable { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Si ya tiene póliza, arranca marcado el plan que tiene hoy.
    val planId = planElegido ?: actual?.planId
    val plan = planId?.let { planPorId(it) }
    val yaAfiliada = actual != null
    val nombre = pet?.name ?: "tu mascota"

    BackScaffold(if (yaAfiliada) "Cambiar de plan" else "Afiliar a $nombre", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Elige un plan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            PLANES.forEach { p ->
                PlanOpcion(p, selected = planId == p.id) { planElegido = p.id; error = null }
            }
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Mascota: $nombre")
                    Text("Plan: ${plan?.nombre ?: "sin elegir"}")
                    Text(if (yaAfiliada) "Vigencia: se conserva la que ya tiene" else "Vigencia: 1 año desde hoy")
                }
            }
            Text(
                "Demostración académica: no es un seguro real y no se cobra nada.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = {
                    val elegido = planId ?: return@Button
                    guardando = true
                    scope.launch {
                        val ok = vm.afiliar(petId, elegido)
                        guardando = false
                        if (ok) nav.popBackStack() else error = "No se pudo guardar. Vuelve a intentarlo."
                    }
                },
                // Con póliza, el botón solo sirve si de verdad se elige un plan distinto.
                enabled = !guardando && pet != null && planId != null && planId != actual?.planId,
                modifier = Modifier.fillMaxWidth(),
            ) { Text(if (yaAfiliada) "Cambiar de plan" else "Afiliar") }
        }
    }
}

@Composable
private fun PlanOpcion(plan: Plan, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = colors) {
        Row(Modifier.padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("${plan.nombre} · ${pesos(plan.precioMesCop)} al mes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Cubre el ${plan.coberturaPct} % de cada gasto", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "Límite ${pesos(plan.limiteAnualCop)} al año · " + if (plan.deducibleCop == 0L) "sin deducible" else "deducible ${pesos(plan.deducibleCop)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // onClick = null: el toque lo maneja la tarjeta completa.
            RadioButton(selected = selected, onClick = null, modifier = Modifier.padding(12.dp))
        }
    }
}
