@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.seguro

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
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
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.BotonPrincipal
import com.huellitas.mascotas.ui.CabeceraFormulario
import com.huellitas.mascotas.ui.MensajeError
import com.huellitas.mascotas.ui.NotaDemo
import com.huellitas.mascotas.ui.SeccionTitulo
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.activityViewModel
import com.huellitas.mascotas.ui.aparecer
import com.huellitas.mascotas.ui.tarjetaColores
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
            pet?.let { CabeceraFormulario(speciesEmoji(it.species), "Elige cómo quieres proteger a $nombre.") }
            SeccionTitulo("Elige un plan")
            PLANES.forEachIndexed { i, p ->
                PlanOpcion(p, selected = planId == p.id, modifier = Modifier.aparecer(i)) { planElegido = p.id; error = null }
            }
            Tarjeta(Modifier.fillMaxWidth().aparecer(PLANES.size)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Resumen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Mascota: $nombre")
                    // El nombre del plan cambia con un fundido al elegir otro.
                    AnimatedContent(plan?.nombre ?: "sin elegir", transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "planResumen") { n ->
                        Text("Plan: $n", fontWeight = FontWeight.Bold)
                    }
                    Text(if (yaAfiliada) "Vigencia: se conserva la que ya tiene" else "Vigencia: 1 año desde hoy")
                }
            }
            NotaDemo("Demostración académica: no es un seguro real y no se cobra nada.")
            MensajeError(error)
            BotonPrincipal(
                if (yaAfiliada) "Cambiar de plan" else "Afiliar",
                onClick = {
                    val elegido = planId ?: return@BotonPrincipal
                    guardando = true
                    scope.launch {
                        val ok = vm.afiliar(petId, elegido)
                        guardando = false
                        if (ok) nav.popBackStack() else error = "No se pudo guardar. Vuelve a intentarlo."
                    }
                },
                // Con póliza, el botón solo sirve si de verdad se elige un plan distinto.
                enabled = !guardando && pet != null && planId != null && planId != actual?.planId,
                cargando = guardando,
                textoCargando = "Guardando…",
            )
        }
    }
}

@Composable
private fun PlanOpcion(plan: Plan, selected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    // El plan elegido se tiñe de verde y gana un borde, con transición suave.
    val fondo by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primaryContainer else tarjetaColores().containerColor,
        tween(250),
        label = "fondoPlan",
    )
    val borde by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        tween(250),
        label = "bordePlan",
    )
    Tarjeta(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = fondo),
        border = BorderStroke(2.dp, borde),
    ) {
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
