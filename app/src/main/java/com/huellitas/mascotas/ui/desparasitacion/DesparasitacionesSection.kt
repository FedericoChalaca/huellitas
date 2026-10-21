package com.huellitas.mascotas.ui.desparasitacion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.DesparasitacionesViewModel
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Deworming
import com.huellitas.mascotas.data.vaccineStatus
import com.huellitas.mascotas.ui.ConfirmDialog
import com.huellitas.mascotas.ui.StatusChip
import com.huellitas.mascotas.ui.activityViewModel

/**
 * Sección «Desparasitaciones» del detalle de la mascota: el título, el botón «Registrar» y la lista.
 * Se llama desde PetDetailScreen, dentro de un item de su LazyColumn.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesparasitacionesSection(nav: NavController, petId: Long, vm: DesparasitacionesViewModel = viewModel()) {
    val main = activityViewModel<MainViewModel>()
    val lista by remember(petId) { vm.desparasitaciones(petId) }.collectAsStateWithLifecycle(initialValue = emptyList())
    val settings by main.settings.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }
    var aBorrar by remember { mutableStateOf<Deworming?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Desparasitaciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            FilledTonalButton(onClick = { nav.navigate("deworming/form/$petId/0") }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Registrar")
            }
        }
        if (lista.isEmpty()) Text("Todavía no hay desparasitaciones registradas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        lista.forEach { d ->
            Card(onClick = { nav.navigate("deworming/form/$petId/${d.id}") }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${d.product} · ${d.kind}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("Aplicada: ${Dates.format(d.appliedDay)}", style = MaterialTheme.typography.bodyMedium)
                        d.nextDueDay?.let {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Próxima: ${Dates.format(it)}", style = MaterialTheme.typography.bodyMedium)
                                StatusChip(vaccineStatus(it, today, settings.warnDays))
                            }
                        }
                        if (d.notes.isNotBlank()) Text(d.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { aBorrar = d }) { Icon(Icons.Filled.Delete, contentDescription = "Borrar la desparasitación") }
                }
            }
        }
    }

    aBorrar?.let { d ->
        ConfirmDialog(
            title = "¿Borrar ${d.product}?",
            text = "No se puede deshacer.",
            confirmLabel = "Borrar",
            onConfirm = {
                vm.borrar(d.id)
                aBorrar = null
            },
            onDismiss = { aBorrar = null },
        )
    }
}
