package com.huellitas.mascotas.ui.urgencias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.CitasViewModel
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.data.CLINICAS
import com.huellitas.mascotas.data.clinicaDeUrgencias
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.activityViewModel
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
    var citaCreada by remember { mutableStateOf<Long?>(null) }

    fun pedir(petId: Long) {
        eligiendo = false
        scope.launch { citaCreada = vm.pedirUrgencia(petId) }
    }

    BackScaffold("Urgencias 24 h", onBack = { nav.popBackStack() }) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Button(
                    // Con una sola mascota no hace falta preguntar para cuál es.
                    onClick = { if (pets.size == 1) pedir(pets.first().id) else eligiendo = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error, contentColor = MaterialTheme.colorScheme.onError),
                    contentPadding = PaddingValues(vertical = 16.dp),
                ) { Text("🚨 Pedir atención de urgencia ahora") }
            }
            item {
                Text(
                    "Clínicas aliadas abiertas ahora (datos de ejemplo)",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            items(clinicas, key = { it.id }) { ClinicaCard(it) }
            item {
                OutlinedButton(onClick = { nav.navigate("urgencias/auxilios") }, modifier = Modifier.fillMaxWidth()) {
                    Text("🩹 Primeros auxilios mientras llegas")
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
        val clinica = remember { clinicaDeUrgencias() }
        AlertDialog(
            onDismissRequest = { citaCreada = null },
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
