package com.huellitas.mascotas.ui.desparasitacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.huellitas.mascotas.ui.SeccionTitulo
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.StatusChip
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.VacioEnLinea
import com.huellitas.mascotas.ui.activityViewModel
import com.huellitas.mascotas.ui.aparecer
import com.huellitas.mascotas.ui.colorDeEstado
import com.huellitas.mascotas.ui.textoDias

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

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 8.dp)) {
        SeccionTitulo("Desparasitaciones") {
            FilledTonalButton(onClick = { nav.navigate("deworming/form/$petId/0") }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Registrar")
            }
        }
        if (lista.isEmpty()) VacioEnLinea("🪱", "Todavía no hay desparasitaciones registradas.")
        lista.forEachIndexed { i, d ->
            val status = vaccineStatus(d.nextDueDay, today, settings.warnDays)
            Tarjeta(onClick = { nav.navigate("deworming/form/$petId/${d.id}") }, modifier = Modifier.fillMaxWidth().aparecer(i)) {
                Row(Modifier.height(IntrinsicSize.Min)) {
                    Box(Modifier.width(6.dp).fillMaxHeight().background(colorDeEstado(status)))
                    Row(
                        Modifier.weight(1f).padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        SpeciesBadge("🛡️", size = 44)
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text("${d.product} · ${d.kind}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text("Aplicada: ${Dates.format(d.appliedDay)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            d.nextDueDay?.let {
                                Text("Próxima: ${Dates.format(it)} · ${textoDias(it, today)}", style = MaterialTheme.typography.bodyMedium)
                                StatusChip(status)
                            }
                            if (d.notes.isNotBlank()) Text(d.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { aBorrar = d }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Borrar la desparasitación", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
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
