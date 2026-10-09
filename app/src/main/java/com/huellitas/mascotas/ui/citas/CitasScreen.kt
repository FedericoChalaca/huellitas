@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.citas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.huellitas.mascotas.data.AppointmentWithPet
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.clinicaPorId
import com.huellitas.mascotas.data.esCitaProxima
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.TabScaffold

/** Pestaña Citas: las que vienen y, aparte, el historial (pasadas, canceladas y completadas). */
@Composable
fun CitasScreen(nav: NavController, vm: CitasViewModel = viewModel()) {
    val citas by vm.citas.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }
    var verHistorial by rememberSaveable { mutableStateOf(false) }
    val (proximas, historial) = citas.partition { esCitaProxima(it.appointment.day, it.appointment.status, today) }
    // El historial se lee al revés: lo más reciente arriba.
    val visibles = if (verHistorial) historial.reversed() else proximas

    TabScaffold(
        nav, "citas", "Mis citas",
        fab = {
            FloatingActionButton(onClick = { nav.navigate("cita/pedir/0/-") }) {
                Icon(Icons.Filled.Add, contentDescription = "Pedir una cita")
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = !verHistorial, onClick = { verHistorial = false }, label = { Text("Próximas (${proximas.size})") })
                FilterChip(selected = verHistorial, onClick = { verHistorial = true }, label = { Text("Historial (${historial.size})") })
            }
            if (visibles.isEmpty()) {
                if (verHistorial) EmptyState("🗂️", "Sin historial", "Aquí quedan las citas pasadas y las canceladas.")
                else EmptyState("📅", "No tienes citas próximas", "Toca + para pedir una.")
            } else {
                LazyColumn(
                    // El espacio de abajo evita que el botón + tape la última cita.
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visibles, key = { it.appointment.id }) { cita ->
                        CitaCard(cita) { nav.navigate("cita/${cita.appointment.id}") }
                    }
                }
            }
        }
    }
}

@Composable
private fun CitaCard(cita: AppointmentWithPet, onClick: () -> Unit) {
    val a = cita.appointment
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SpeciesBadge(if (a.urgent) "🚨" else speciesEmoji(cita.petSpecies), size = 48)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("${a.reason} · ${cita.petName}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    clinicaPorId(a.clinicId)?.nombre ?: "Clínica",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text("${Dates.format(a.day)} · ${a.slot}", style = MaterialTheme.typography.bodyMedium)
            }
            EstadoCitaChip(a.status)
        }
    }
}

/** Etiqueta con el estado de una cita (Solicitada, Confirmada, Cancelada o Completada). */
@Composable
fun EstadoCitaChip(status: String) {
    val scheme = MaterialTheme.colorScheme
    val (bg, fg) = when (status) {
        AppointmentStatus.CONFIRMED -> scheme.primaryContainer to scheme.onPrimaryContainer
        AppointmentStatus.REQUESTED -> scheme.secondaryContainer to scheme.onSecondaryContainer
        AppointmentStatus.CANCELLED -> scheme.errorContainer to scheme.onErrorContainer
        else -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(50)) {
        Text(status, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium)
    }
}
