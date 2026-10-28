@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui.pendientes

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.sp
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
import com.huellitas.mascotas.ui.DatoConIcono
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.StatusChip
import com.huellitas.mascotas.ui.TabScaffold
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.aparecer
import com.huellitas.mascotas.ui.colorDeEstado
import com.huellitas.mascotas.ui.flotar
import com.huellitas.mascotas.ui.textoDias

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

    val vencidosTotal = todos.count { vaccineStatus(it.dueDay, today, settings.warnDays) == VaccineStatus.OVERDUE }
    TabScaffold(
        nav, "pendientes", "Pendientes",
        subtitle = when {
            todos.isEmpty() -> "Nada por ahora"
            vencidosTotal > 0 -> "Hay cosas que atender"
            else -> "Vacunas y desparasitaciones por venir"
        },
    ) { padding ->
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
                item(key = "filtros") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.aparecer()) {
                        FILTROS.forEach { (tipo, etiqueta) ->
                            FilterChip(
                                selected = filtro == tipo,
                                onClick = { filtro = tipo },
                                label = { Text(etiqueta) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                            )
                        }
                    }
                }
                item(key = "resumen") { ResumenPendientes(visibles.isEmpty(), vencidos, Modifier.aparecer(1)) }
                itemsIndexed(visibles, key = { _, p -> p.clave }) { i, p ->
                    PendienteCard(
                        p, vaccineStatus(p.dueDay, today, settings.warnDays), today,
                        modifier = Modifier.animateItem().aparecer(i + 2),
                        onOpen = { nav.navigate("pet/${p.petId}") },
                        onAgendar = { nav.navigate("cita/pedir/${p.petId}/${rutaDeMotivo(p.tipo)}") },
                    )
                }
            }
        }
    }
}

/** Franja de arriba: verde si todo está al día, roja si hay vencidos; el color se funde al cambiar el filtro. */
@Composable
private fun ResumenPendientes(vacio: Boolean, vencidos: Int, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val hayVencidos = vencidos > 0
    val fondo by animateColorAsState(if (hayVencidos) scheme.errorContainer else scheme.primaryContainer, tween(400), label = "fondoResumen")
    val letra by animateColorAsState(if (hayVencidos) scheme.onErrorContainer else scheme.onPrimaryContainer, tween(400), label = "letraResumen")
    Surface(modifier.fillMaxWidth(), color = fondo, contentColor = letra, shape = RoundedCornerShape(18.dp)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(if (vacio) "🔍" else if (hayVencidos) "⏰" else "🎉", fontSize = 30.sp, modifier = Modifier.flotar(3f))
            AnimatedContent(
                targetState = when {
                    vacio -> "No hay nada en este filtro."
                    vencidos == 0 -> "Todo al día por ahora."
                    vencidos == 1 -> "1 pendiente vencido."
                    else -> "$vencidos pendientes vencidos."
                },
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "textoResumen",
            ) { texto -> Text(texto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun PendienteCard(p: Pendiente, status: VaccineStatus, today: Long, modifier: Modifier = Modifier, onOpen: () -> Unit, onAgendar: () -> Unit) {
    Tarjeta(onClick = onOpen, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            Box(Modifier.width(6.dp).fillMaxHeight().background(colorDeEstado(status)))
            Column(Modifier.weight(1f).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    SpeciesBadge(speciesEmoji(p.petSpecies), size = 48)
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(p.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "${if (p.tipo == "Vacunación") "💉" else "🛡️"} ${p.tipo} · ${p.petName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        DatoConIcono(Icons.Filled.DateRange, "${Dates.format(p.dueDay)} · ${textoDias(p.dueDay, today)}", MaterialTheme.colorScheme.onSurface)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(status)
                    Spacer(Modifier.weight(1f))
                    FilledTonalButton(onClick = onAgendar) {
                        Icon(Icons.Filled.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Agendar cita")
                    }
                }
            }
        }
    }
}
