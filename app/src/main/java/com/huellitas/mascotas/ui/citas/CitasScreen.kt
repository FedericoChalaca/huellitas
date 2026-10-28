@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.citas

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.huellitas.mascotas.ui.BotonFlotante
import com.huellitas.mascotas.ui.DatoConIcono
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.EtiquetaEstado
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.TabScaffold
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.aparecer
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/** Pestaña Citas: las que vienen y, aparte, el historial (pasadas, canceladas y completadas). */
@Composable
fun CitasScreen(nav: NavController, vm: CitasViewModel = viewModel()) {
    val citas by vm.citas.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }
    var verHistorial by rememberSaveable { mutableStateOf(false) }
    val (proximas, historial) = citas.partition { esCitaProxima(it.appointment.day, it.appointment.status, today) }

    TabScaffold(
        nav, "citas", "Mis citas",
        subtitle = when (proximas.size) {
            0 -> "Sin citas por ahora"
            1 -> "1 cita próxima"
            else -> "${proximas.size} citas próximas"
        },
        fab = { BotonFlotante("Pedir cita", Icons.Filled.Add) { nav.navigate("cita/pedir/0/-") } },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FiltroCitas("Próximas (${proximas.size})", !verHistorial) { verHistorial = false }
                FiltroCitas("Historial (${historial.size})", verHistorial) { verHistorial = true }
            }
            // Al cambiar entre Próximas e Historial, la lista se funde en vez de saltar.
            Crossfade(targetState = verHistorial, animationSpec = tween(300), label = "listaCitas") { historialVisible ->
                // El historial se lee al revés: lo más reciente arriba.
                val lista = if (historialVisible) historial.reversed() else proximas
                if (lista.isEmpty()) {
                    if (historialVisible) EmptyState("🗂️", "Sin historial", "Aquí quedan las citas pasadas y las canceladas.")
                    else EmptyState(
                        "📅", "No tienes citas próximas", "Pide una para vacunar, desparasitar o un control.",
                        accion = {
                            FilledTonalButton(onClick = { nav.navigate("cita/pedir/0/-") }) { Text("Pedir una cita") }
                        },
                    )
                } else {
                    LazyColumn(
                        // El espacio de abajo evita que el botón tape la última cita.
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        itemsIndexed(lista, key = { _, c -> c.appointment.id }) { i, cita ->
                            CitaCard(cita, Modifier.animateItem().aparecer(i)) { nav.navigate("cita/${cita.appointment.id}") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FiltroCitas(texto: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(texto) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
        ),
    )
}

/** Cuadrito de calendario: el día grande y el mes abreviado. */
@Composable
fun HojaCalendario(day: Long, urgente: Boolean = false) {
    val fecha = LocalDate.ofEpochDay(day)
    val mes = fecha.month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es-CO")).replace(".", "").uppercase()
    val scheme = MaterialTheme.colorScheme
    val arriba = if (urgente) scheme.error else scheme.primary
    val sobreArriba = if (urgente) scheme.onError else scheme.onPrimary
    Column(
        Modifier.width(54.dp).clip(RoundedCornerShape(14.dp)).background(scheme.surfaceVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            mes,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = sobreArriba,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().background(arriba).padding(vertical = 3.dp),
        )
        Text(
            "${fecha.dayOfMonth}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

@Composable
private fun CitaCard(cita: AppointmentWithPet, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val a = cita.appointment
    Tarjeta(onClick = onClick, modifier = modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            HojaCalendario(a.day, urgente = a.urgent)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    "${if (a.urgent) "🚨" else speciesEmoji(cita.petSpecies)} ${a.reason} · ${cita.petName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                DatoConIcono(Icons.Filled.Place, clinicaPorId(a.clinicId)?.nombre ?: "Clínica")
                DatoConIcono(Icons.Filled.DateRange, "${Dates.format(a.day)} · ${a.slot}")
                EstadoCitaChip(a.status)
            }
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
    EtiquetaEstado(status, bg, fg)
}
