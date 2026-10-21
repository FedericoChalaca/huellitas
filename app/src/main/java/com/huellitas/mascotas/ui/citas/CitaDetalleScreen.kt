@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.CitasViewModel
import com.huellitas.mascotas.data.AppointmentStatus
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.clinicaPorId
import com.huellitas.mascotas.data.hora
import com.huellitas.mascotas.data.horariosDisponibles
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.ConfirmDialog
import com.huellitas.mascotas.ui.DateField
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.SpeciesBadge
import java.time.LocalTime
import kotlinx.coroutines.launch

/** Detalle de una cita: todos sus datos y, si sigue vigente, cancelarla o cambiarle el día y la hora. */
@Composable
fun CitaDetalleScreen(nav: NavController, citaId: Long, vm: CitasViewModel = viewModel()) {
    val item by remember(citaId) { vm.cita(citaId) }.collectAsStateWithLifecycle(initialValue = null)
    var confirmar by remember { mutableStateOf(false) }
    var reprogramando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackScaffold("Detalle de la cita", onBack = { nav.popBackStack() }) { padding ->
        val cita = item
        if (cita == null) {
            EmptyState("📅", "Cita no encontrada", "Puede que ya no exista.", Modifier.padding(padding))
            return@BackScaffold
        }
        val a = cita.appointment
        val clinica = clinicaPorId(a.clinicId)
        val vigente = a.status == AppointmentStatus.REQUESTED || a.status == AppointmentStatus.CONFIRMED
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SpeciesBadge(speciesEmoji(cita.petSpecies), size = 56)
                        Column(Modifier.weight(1f)) {
                            Text("${a.reason} · ${cita.petName}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text(a.specialty, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        EstadoCitaChip(a.status)
                    }
                    if (a.urgent) Text("🚨 Atención de urgencia", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Text("${Dates.format(a.day)} · ${a.slot}", style = MaterialTheme.typography.titleMedium)
                    if (a.notes.isNotBlank()) Text("Notas: ${a.notes}", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(clinica?.nombre ?: "Clínica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    if (clinica != null) {
                        Text("${clinica.direccion} · ${clinica.ciudad}", style = MaterialTheme.typography.bodyMedium)
                        Text("Teléfono ${clinica.telefono}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            if (vigente) {
                Button(onClick = { reprogramando = true }, modifier = Modifier.fillMaxWidth()) { Text("Reprogramar") }
                OutlinedButton(onClick = { confirmar = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancelar cita", color = MaterialTheme.colorScheme.error)
                }
            }
            Text(
                "Demostración académica: la clínica y la cita son de ejemplo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (confirmar) {
            ConfirmDialog(
                title = "¿Cancelar la cita?",
                text = "Podrás pedir otra cuando quieras.",
                confirmLabel = "Cancelar cita",
                onConfirm = {
                    vm.cancelar(citaId)
                    confirmar = false
                },
                onDismiss = { confirmar = false },
            )
        }

        if (reprogramando) {
            var dia by remember { mutableStateOf<Long?>(null) }
            var horaElegida by remember { mutableStateOf<String?>(null) }
            val hoy = remember { Dates.today() }
            val ahora = remember { LocalTime.now().let { hora(it.hour, it.minute) } }
            val horarios = dia?.let { d -> horariosDisponibles(a.clinicId, d).filter { d != hoy || it > ahora } } ?: emptyList()
            AlertDialog(
                onDismissRequest = { reprogramando = false },
                title = { Text("Nuevo día y hora") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        DateField("Nuevo día", dia, { dia = it; horaElegida = null })
                        when {
                            dia == null -> Unit
                            dia!! < hoy -> Text("Esa fecha ya pasó: elige otra.", color = MaterialTheme.colorScheme.error)
                            horarios.isEmpty() -> Text("No hay horas libres ese día. Prueba con otra fecha.")
                            else -> FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                horarios.forEach { h -> FilterChip(selected = horaElegida == h, onClick = { horaElegida = h }, label = { Text(h) }) }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = dia != null && horaElegida != null,
                        onClick = {
                            val d = dia ?: return@TextButton
                            val h = horaElegida ?: return@TextButton
                            scope.launch { if (vm.reprogramar(citaId, d, h)) reprogramando = false }
                        },
                    ) { Text("Guardar") }
                },
                dismissButton = { TextButton(onClick = { reprogramando = false }) { Text("Cancelar") } },
            )
        }
    }
}
