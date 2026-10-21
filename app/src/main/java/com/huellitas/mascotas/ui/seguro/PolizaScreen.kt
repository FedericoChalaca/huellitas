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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.SeguroViewModel
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.EmptyState

@Composable
private fun Dato(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.45f))
        Text(valor, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.55f))
    }
}

/** Póliza de una mascota: sus datos, lo que cubre el plan y cuánto del límite anual lleva usado. */
@Composable
fun PolizaScreen(nav: NavController, petId: Long, vm: SeguroViewModel = viewModel()) {
    val poliza by remember(petId) { vm.poliza(petId) }.collectAsStateWithLifecycle(initialValue = null)
    val plan = poliza?.let { planPorId(it.planId) }

    BackScaffold("Póliza", onBack = { nav.popBackStack() }) { padding ->
        val p = poliza
        if (p == null || plan == null) {
            Column(Modifier.padding(padding)) {
                EmptyState("🛡️", "Sin póliza", "Esta mascota todavía no está afiliada.", Modifier.weight(1f))
                Button(onClick = { nav.navigate("seguro/afiliar/$petId") }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Afiliar") }
            }
            return@BackScaffold
        }
        // El gasto de ejemplo nunca pasa del límite del plan; la barra se queda entre 0 y 1.
        val fraccion = (p.usedCop.toFloat() / plan.limiteAnualCop).coerceIn(0f, 1f)
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Plan ${plan.nombre}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Dato("Número de póliza", p.number)
                    Dato("Desde", Dates.format(p.startDay))
                    Dato("Hasta", Dates.format(p.endDay))
                    Dato("Cobertura", "${plan.coberturaPct} %")
                    Dato("Precio", "${pesos(plan.precioMesCop)} al mes")
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Qué incluye", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    plan.incluye.forEach { Text("✓ $it") }
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Límite anual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Usado: ${pesos(p.usedCop)} de ${pesos(plan.limiteAnualCop)}")
                    LinearProgressIndicator(progress = { fraccion }, modifier = Modifier.fillMaxWidth())
                    Text(
                        "Te quedan ${pesos(maxOf(0L, plan.limiteAnualCop - p.usedCop))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Button(onClick = { nav.navigate("cita/pedir/$petId/-") }, modifier = Modifier.fillMaxWidth()) { Text("Pedir cita") }
            OutlinedButton(onClick = { nav.navigate("seguro/afiliar/$petId") }, modifier = Modifier.fillMaxWidth()) { Text("Cambiar de plan") }
            Text(
                "Demostración académica: no es un seguro real y no se cobra nada.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
