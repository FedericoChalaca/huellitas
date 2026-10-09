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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.SeguroViewModel
import com.huellitas.mascotas.data.Clinica
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.ESPECIALIDADES
import com.huellitas.mascotas.data.MEDICINA_GENERAL
import com.huellitas.mascotas.data.MOTIVOS
import com.huellitas.mascotas.data.clinicasPara
import com.huellitas.mascotas.data.errorDeCita
import com.huellitas.mascotas.data.hora
import com.huellitas.mascotas.data.horariosDisponibles
import com.huellitas.mascotas.data.motivoDeRuta
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.DateField
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.activityViewModel
import java.time.LocalTime
import kotlinx.coroutines.launch

/**
 * Pedir una cita: mascota, motivo, especialidad (solo si es con especialista), clínica, día y hora.
 * [petId] es 0 si no viene una mascota elegida; [motivo] es la palabra de la ruta ("vacunacion"…) o "-".
 */
@Composable
fun PedirCitaScreen(
    nav: NavController,
    petId: Long,
    motivo: String,
    vm: CitasViewModel = viewModel(),
    seguro: SeguroViewModel = viewModel(),
) {
    val main = activityViewModel<MainViewModel>()
    val pets by main.pets.collectAsStateWithLifecycle()
    val polizas by seguro.polizas.collectAsStateWithLifecycle()
    val today = remember { Dates.today() }
    val scope = rememberCoroutineScope()

    var mascota by rememberSaveable { mutableStateOf(petId.takeIf { it != 0L }) }
    var elMotivo by rememberSaveable { mutableStateOf(motivoDeRuta(motivo)) }
    var especialidadElegida by rememberSaveable { mutableStateOf(MEDICINA_GENERAL) }
    var clinicaElegida by rememberSaveable { mutableStateOf<String?>(null) }
    var dia by rememberSaveable { mutableStateOf<Long?>(null) }
    var horaElegida by rememberSaveable { mutableStateOf<String?>(null) }
    var notas by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var guardando by remember { mutableStateOf(false) }

    // Con una sola mascota no hay nada que elegir.
    LaunchedEffect(pets) {
        if (mascota == null && pets.size == 1) mascota = pets.first().id
    }

    // Lo que depende de otra elección se recalcula aquí: si cambia el motivo y la clínica elegida
    // ya no sirve, deja de contar sin tener que "limpiarla" en cada botón.
    val especialidad = if (elMotivo == "Especialista") especialidadElegida else MEDICINA_GENERAL
    val clinicas = clinicasPara(especialidad).filter { elMotivo != "Urgencia" || it.abierta24h }.sortedBy { it.distanciaKm }
    val clinicaId = clinicaElegida?.takeIf { id -> clinicas.any { it.id == id } }
    val ahora = remember { LocalTime.now().let { hora(it.hour, it.minute) } }
    val elDia = dia
    // Si la cita es para hoy, las horas que ya pasaron no se ofrecen.
    val horarios = if (clinicaId == null || elDia == null) emptyList()
    else horariosDisponibles(clinicaId, elDia).filter { elDia != today || it > ahora }
    val laHora = horaElegida?.takeIf { it in horarios }
    val plan = polizas.firstOrNull { it.policy.petId == mascota }?.let { planPorId(it.policy.planId) }

    fun pedir() {
        val p = mascota ?: return
        val m = elMotivo ?: return
        val c = clinicaId ?: return
        val d = elDia ?: return
        val h = laHora ?: return
        errorDeCita(m, especialidad, c, d, h, today)?.let {
            error = it
            return
        }
        guardando = true
        scope.launch {
            val id = vm.pedirCita(p, m, especialidad, c, d, h, notas)
            guardando = false
            if (id == null) {
                error = "No se pudo pedir la cita. Revisa los datos."
            } else {
                // Al volver atrás desde el detalle no debe reaparecer el formulario ya enviado.
                nav.navigate("cita/$id") { popUpTo("cita/pedir/{petId}/{motivo}") { inclusive = true } }
            }
        }
    }

    BackScaffold("Pedir cita", onBack = { nav.popBackStack() }) { padding ->
        if (pets.isEmpty()) {
            Column(Modifier.padding(padding)) {
                EmptyState("🐾", "Primero agrega una mascota", "Las citas se piden para una mascota.", Modifier.weight(1f))
                Button(onClick = { nav.navigate("pet/form/0") }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Agregar mascota") }
            }
        } else Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Titulo("Mascota")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pets.forEach { pet ->
                    FilterChip(
                        selected = mascota == pet.id,
                        onClick = { mascota = pet.id; error = null },
                        label = { Text("${speciesEmoji(pet.species)} ${pet.name}") },
                    )
                }
            }
            if (mascota != null) {
                Text(
                    if (plan != null) "Su plan ${plan.nombre} cubre el ${plan.coberturaPct} % (demostración)."
                    else "Esta mascota no tiene póliza: la cita sería particular.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Titulo("Motivo")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MOTIVOS.forEach { m ->
                    FilterChip(selected = elMotivo == m, onClick = { elMotivo = m; error = null }, label = { Text(m) })
                }
            }

            if (elMotivo == "Especialista") {
                Titulo("Especialidad")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ESPECIALIDADES.forEach { e ->
                        FilterChip(selected = especialidadElegida == e, onClick = { especialidadElegida = e; error = null }, label = { Text(e) })
                    }
                }
            }

            Titulo("Clínica")
            clinicas.forEach { c ->
                ClinicaOpcion(c, selected = clinicaId == c.id) { clinicaElegida = c.id; error = null }
            }

            DateField("Fecha", dia, { dia = it; error = null })

            if (clinicaId != null && elDia != null) {
                Titulo("Hora")
                when {
                    elDia < today -> Text("Esa fecha ya pasó: elige otra.", color = MaterialTheme.colorScheme.error)
                    horarios.isEmpty() -> Text("Esa clínica no tiene horas libres ese día. Prueba con otra fecha.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    else -> FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        horarios.forEach { h ->
                            FilterChip(selected = laHora == h, onClick = { horaElegida = h; error = null }, label = { Text(h) })
                        }
                    }
                }
            }

            OutlinedTextField(notas, { notas = it }, label = { Text("Notas para la clínica (opcional)") }, minLines = 2, modifier = Modifier.fillMaxWidth())
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(
                onClick = { pedir() },
                enabled = !guardando && mascota != null && elMotivo != null && clinicaId != null && elDia != null && laHora != null,
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Pedir cita") }
            Text(
                "Demostración académica: no se le avisa a ninguna clínica real.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Titulo(texto: String) {
    Text(texto, style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun ClinicaOpcion(clinica: Clinica, selected: Boolean, onClick: () -> Unit) {
    val colors = if (selected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors()
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = colors) {
        Row(Modifier.padding(start = 4.dp, end = 16.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            // onClick = null: el toque lo maneja la tarjeta completa, no solo el circulito.
            RadioButton(selected = selected, onClick = null, modifier = Modifier.padding(12.dp))
            Column(Modifier.weight(1f)) {
                Text(clinica.nombre, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "${clinica.ciudad} · ${clinica.distancia}" + if (clinica.abierta24h) " · 24 h" else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
