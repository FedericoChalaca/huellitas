@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.pendientes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.VaccineStatus
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.data.vaccineStatus
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.StatusChip
import com.huellitas.mascotas.ui.TabScaffold

// Pestaña Pendientes. Por ahora solo trae las vacunas; Federico (F-01 y F-02) le suma las
// desparasitaciones y el botón "Agendar" de cada una.

@Composable
fun PendientesScreen(nav: NavController, vm: MainViewModel) {
    val items by vm.upcoming.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }

    TabScaffold(nav, "pendientes", "Pendientes") { padding ->
        if (items.isEmpty()) {
            EmptyState(
                "💉", "Nada pendiente",
                "Cuando registres una vacuna con fecha de próxima dosis, aparece aquí.",
                Modifier.padding(padding),
            )
        } else {
            val overdue = items.count { vaccineStatus(it.vaccine.nextDueDay, today, settings.warnDays) == VaccineStatus.OVERDUE }
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Text(
                        if (overdue == 0) "Todo al día por ahora." else if (overdue == 1) "1 vacuna vencida." else "$overdue vacunas vencidas.",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (overdue == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    )
                }
                items(items, key = { it.vaccine.id }) { row ->
                    val due = row.vaccine.nextDueDay
                    Card(onClick = { nav.navigate("pet/${row.vaccine.petId}") }, modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            SpeciesBadge(speciesEmoji(row.petSpecies), size = 48)
                            Column(Modifier.weight(1f)) {
                                Text(row.vaccine.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(
                                    "${row.petName} · ${due?.let { Dates.format(it) } ?: ""}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            StatusChip(vaccineStatus(due, today, settings.warnDays))
                        }
                    }
                }
            }
        }
    }
}
