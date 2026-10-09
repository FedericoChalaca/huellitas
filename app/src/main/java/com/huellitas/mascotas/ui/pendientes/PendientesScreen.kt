@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui.pendientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
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
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.PendientesViewModel
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Pendiente
import com.huellitas.mascotas.data.VaccineStatus
import com.huellitas.mascotas.data.rutaDeMotivo
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.data.vaccineStatus
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.StatusChip
import com.huellitas.mascotas.ui.TabScaffold

// Pestaña Pendientes: vacunas y desparasitaciones con fecha de próxima dosis, de la más urgente a la
// más lejana. Cada una trae el botón para pedir la cita con la mascota y el motivo ya elegidos.

private val FILTROS = listOf(null to "Todo", "Vacunación" to "Vacunas", "Desparasitación" to "Desparasitaciones")

@Composable
fun PendientesScreen(nav: NavController, vm: MainViewModel, pendientesVm: PendientesViewModel = viewModel()) {
    val todos by pendientesVm.pendientes.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }
    var filtro by rememberSaveable { mutableStateOf<String?>(null) }
    val visibles = todos.filter { filtro == null || it.tipo == filtro }

    TabScaffold(nav, "pendientes", "Pendientes") { padding ->
        if (todos.isEmpty()) {
            EmptyState(
                "💉", "Nada pendiente",
                "Cuando registres una vacuna o una desparasitación con fecha de próxima dosis, aparece aquí.",
                Modifier.padding(padding),
            )
        } else {
            val vencidos = visibles.count { vaccineStatus(it.dueDay, today, settings.warnDays) == VaccineStatus.OVERDUE }
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FILTROS.forEach { (tipo, etiqueta) ->
                            FilterChip(selected = filtro == tipo, onClick = { filtro = tipo }, label = { Text(etiqueta) })
                        }
                    }
                }
                item {
                    Text(
                        when {
                            visibles.isEmpty() -> "No hay nada en este filtro."
                            vencidos == 0 -> "Todo al día por ahora."
                            vencidos == 1 -> "1 pendiente vencido."
                            else -> "$vencidos pendientes vencidos."
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (vencidos == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    )
                }
                items(visibles, key = { it.clave }) { p ->
                    PendienteCard(
                        p, vaccineStatus(p.dueDay, today, settings.warnDays),
                        onOpen = { nav.navigate("pet/${p.petId}") },
                        onAgendar = { nav.navigate("cita/pedir/${p.petId}/${rutaDeMotivo(p.tipo)}") },
                    )
                }
            }
        }
    }
}

@Composable
private fun PendienteCard(p: Pendiente, status: VaccineStatus, onOpen: () -> Unit, onAgendar: () -> Unit) {
    Card(onClick = onOpen, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SpeciesBadge(speciesEmoji(p.petSpecies), size = 48)
                Column(Modifier.weight(1f)) {
                    Text(p.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${p.tipo} · ${p.petName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(Dates.format(p.dueDay), style = MaterialTheme.typography.bodyMedium)
                }
                StatusChip(status)
            }
            FilledTonalButton(onClick = onAgendar) { Text("Agendar cita") }
        }
    }
}
