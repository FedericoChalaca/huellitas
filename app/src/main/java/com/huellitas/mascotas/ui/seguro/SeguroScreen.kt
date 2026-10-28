@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.seguro

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.huellitas.mascotas.ui.EtiquetaEstado
import com.huellitas.mascotas.ui.MenuCard
import com.huellitas.mascotas.ui.NotaDemo
import com.huellitas.mascotas.ui.SeccionTitulo
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.TabScaffold
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.VacioEnLinea
import com.huellitas.mascotas.ui.activityViewModel
import com.huellitas.mascotas.ui.aparecer

/** Pestaña Seguro: la póliza de cada mascota (o el botón para afiliarla) y el menú de servicios. */
@Composable
fun SeguroScreen(nav: NavController, vm: SeguroViewModel = viewModel()) {
    val main = activityViewModel<MainViewModel>()
    val pets by main.pets.collectAsStateWithLifecycle()
    val polizas by vm.polizas.collectAsStateWithLifecycle()

    val afiliadas = pets.count { pet -> polizas.any { it.policy.petId == pet.id } }
    TabScaffold(
        nav, "seguro", ASEGURADORA,
        subtitle = when {
            pets.isEmpty() -> "Protege a tus mascotas"
            afiliadas == 0 -> "Ninguna mascota afiliada todavía"
            afiliadas == pets.size -> "Todas tus mascotas están protegidas"
            else -> "$afiliadas de ${pets.size} mascotas protegidas"
        },
    ) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NotaDemo("Demostración académica: $ASEGURADORA no es una aseguradora real y aquí no se cobra nada.", Modifier.aparecer())
            SeccionTitulo("Tus mascotas", Modifier.aparecer())
            if (pets.isEmpty()) {
                VacioEnLinea("🐾", "Agrega una mascota para poder afiliarla.", Modifier.aparecer(1))
            }
            pets.forEachIndexed { i, pet ->
                PolizaCard(
                    pet, polizas.firstOrNull { it.policy.petId == pet.id }?.policy,
                    modifier = Modifier.aparecer(i + 1),
                    onVer = { nav.navigate("seguro/poliza/${pet.id}") },
                    onAfiliar = { nav.navigate("seguro/afiliar/${pet.id}") },
                )
            }
            SeccionTitulo("Servicios", Modifier.padding(top = 8.dp).aparecer(pets.size + 1))
            val base = pets.size + 2
            MenuCard("🚨", "Urgencias 24 h", "Clínicas abiertas ahora y atención inmediata", Modifier.aparecer(base)) { nav.navigate("urgencias") }
            MenuCard("💊", "Cotizar medicamentos", "Arma una cotización con el descuento de tu plan", Modifier.aparecer(base + 1)) { nav.navigate("medicamentos") }
            MenuCard("🧾", "Mis cotizaciones", "Lo que ya cotizaste", Modifier.aparecer(base + 2)) { nav.navigate("cotizaciones") }
            MenuCard("🛡️", "Planes del seguro", "Compara Básico, Plus y Premium", Modifier.aparecer(base + 3)) { nav.navigate("seguro/planes") }
            MenuCard("🩹", "Primeros auxilios", "Qué hacer mientras llegas a la clínica", Modifier.aparecer(base + 4)) { nav.navigate("urgencias/auxilios") }
        }
    }
}

@Composable
private fun PolizaCard(pet: Pet, poliza: Policy?, modifier: Modifier = Modifier, onVer: () -> Unit, onAfiliar: () -> Unit) {
    val plan = poliza?.let { planPorId(it.planId) }
    Tarjeta(onClick = if (poliza != null) onVer else onAfiliar, modifier = modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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
                    } else {
                        Text("Sin póliza", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (poliza != null) {
                    EtiquetaEstado("Protegida", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                } else {
                    FilledTonalButton(onClick = onAfiliar) { Text("Afiliar") }
                }
            }
            if (poliza != null && plan != null) {
                // Barra del límite anual usado: se llena con animación al abrir la pestaña.
                BarraDeUso(poliza.usedCop, plan.limiteAnualCop)
                Text(
                    "Usado: ${pesos(poliza.usedCop)} de ${pesos(plan.limiteAnualCop)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Barra redondeada del límite anual que se llena desde cero. */
@Composable
fun BarraDeUso(usado: Long, limite: Long, modifier: Modifier = Modifier) {
    // El gasto de ejemplo nunca pasa del límite del plan; la barra se queda entre 0 y 1.
    val objetivo = if (limite <= 0L) 0f else (usado.toFloat() / limite).coerceIn(0f, 1f)
    val progreso = remember { Animatable(0f) }
    LaunchedEffect(objetivo) { progreso.animateTo(objetivo, tween(900)) }
    LinearProgressIndicator(
        progress = { progreso.value },
        modifier = modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(50)),
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}
