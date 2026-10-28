package com.huellitas.mascotas.ui.urgencias

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.CitasViewModel
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.data.CLINICAS
import com.huellitas.mascotas.data.clinicaDeUrgencias
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.MensajeError
import com.huellitas.mascotas.ui.MenuCard
import com.huellitas.mascotas.ui.SeccionTitulo
import com.huellitas.mascotas.ui.activityViewModel
import com.huellitas.mascotas.ui.aparecer
import kotlinx.coroutines.launch

/**
 * Urgencias: el botón para pedir atención inmediata y las clínicas abiertas las 24 horas, de la más
 * cercana a la más lejana.
 */
@Composable
fun UrgenciasScreen(nav: NavController, vm: CitasViewModel = viewModel()) {
    val main = activityViewModel<MainViewModel>()
    val pets by main.pets.collectAsStateWithLifecycle()
    val clinicas = remember { CLINICAS.filter { it.abierta24h }.sortedBy { it.distanciaKm } }
    val scope = rememberCoroutineScope()
    var eligiendo by remember { mutableStateOf(false) }
    var guardando by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var citaCreada by remember { mutableStateOf<Long?>(null) }

    fun pedir(petId: Long) {
        if (guardando) return
        eligiendo = false
        guardando = true
        error = null
        scope.launch {
            val id = vm.pedirUrgencia(petId)
            guardando = false
            if (id == null) error = "No se pudo pedir la atención. Vuelve a intentarlo."
            else citaCreada = id
        }
    }

    BackScaffold("Urgencias 24 h", onBack = { nav.popBackStack() }) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "error") { MensajeError(error) }
            item(key = "boton") { BotonUrgencia(guardando, Modifier.aparecer()) { if (pets.size == 1) pedir(pets.first().id) else eligiendo = true } }
            item(key = "titulo") {
                Column(Modifier.padding(top = 8.dp).aparecer(1)) {
                    SeccionTitulo("Clínicas abiertas ahora")
                    Text(
                        "Aliadas, de la más cercana a la más lejana (datos de ejemplo).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 14.dp, top = 2.dp),
                    )
                }
            }
            itemsIndexed(clinicas, key = { _, c -> c.id }) { i, c -> ClinicaCard(c, Modifier.aparecer(i + 2)) }
            item(key = "auxilios") {
                MenuCard("🩹", "Primeros auxilios", "Qué hacer mientras llegas a la clínica", Modifier.aparecer(clinicas.size + 2)) {
                    nav.navigate("urgencias/auxilios")
                }
            }
        }
    }

    if (eligiendo) {
        AlertDialog(
            onDismissRequest = { eligiendo = false },
            title = { Text(if (pets.isEmpty()) "Primero agrega una mascota" else "¿Para qué mascota?") },
            text = {
                Column {
                    if (pets.isEmpty()) Text("La atención se pide para una mascota registrada.")
                    pets.forEach { pet ->
                        TextButton(onClick = { pedir(pet.id) }, modifier = Modifier.fillMaxWidth()) {
                            Text("${speciesEmoji(pet.species)} ${pet.name}")
                        }
                    }
                }
            },
            confirmButton = {
                if (pets.isEmpty()) {
                    TextButton(onClick = { eligiendo = false; nav.navigate("pet/form/0") }) { Text("Agregar mascota") }
                }
            },
            dismissButton = { TextButton(onClick = { eligiendo = false }) { Text("Cancelar") } },
        )
    }

    citaCreada?.let { id ->
        // (El diálogo de confirmación lleva un ícono para que se vea que salió bien.)
        val clinica = remember { clinicaDeUrgencias() }
        AlertDialog(
            onDismissRequest = { citaCreada = null },
            icon = { Text("✅", fontSize = 32.sp) },
            title = { Text("Atención solicitada") },
            text = {
                Text(
                    "Te esperan en ${clinica.nombre}, ${clinica.direccion}. Teléfono ${clinica.telefono}.\n\n" +
                        "Demostración académica: no se le avisó a ninguna clínica real. Si es una emergencia de verdad, ve a tu veterinario.",
                )
            },
            confirmButton = {
                TextButton(onClick = { citaCreada = null; nav.navigate("cita/$id") }) { Text("Ver la cita") }
            },
            dismissButton = { TextButton(onClick = { citaCreada = null }) { Text("Cerrar") } },
        )
    }
}

/** Botón rojo grande que late despacio: es lo primero que se ve en Urgencias. */
@Composable
private fun BotonUrgencia(guardando: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val transicion = rememberInfiniteTransition(label = "latidoUrgencia")
    val escala by transicion.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "escalaUrgencia",
    )
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier.fillMaxWidth()
            .scale(if (guardando) 1f else escala)
            .background(Brush.linearGradient(listOf(Color(0xFFD64545), Color(0xFFB3261E))), RoundedCornerShape(24.dp)),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("🚨", fontSize = 36.sp)
                Column(Modifier.weight(1f)) {
                    Text("¿Es una urgencia?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Pide atención inmediata en la clínica 24 h más cercana.", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f))
                }
            }
            Button(
                onClick = onClick,
                enabled = !guardando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFB3261E)),
            ) { Text(if (guardando) "Registrando…" else "Pedir atención de urgencia ahora", fontWeight = FontWeight.Bold) }
        }
    }
}
