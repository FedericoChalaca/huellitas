package com.huellitas.mascotas.ui.urgencias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huellitas.mascotas.data.CLINICAS
import com.huellitas.mascotas.ui.BackScaffold

/**
 * Urgencias: las clínicas abiertas las 24 horas, de la más cercana a la más lejana.
 * Tarea F-09 (Federico): botón "Pedir atención de urgencia ahora" que crea una cita urgente.
 */
@Composable
fun UrgenciasScreen(nav: NavController) {
    val clinicas = CLINICAS.filter { it.abierta24h }.sortedBy { it.distanciaKm }
    BackScaffold("Urgencias 24 h", onBack = { nav.popBackStack() }) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // TODO(F-09): botón "Pedir atención de urgencia ahora"
            item {
                Text(
                    "Clínicas aliadas abiertas ahora (datos de ejemplo)",
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            items(clinicas, key = { it.id }) { ClinicaCard(it) }
        }
    }
}
