@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.urgencias

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huellitas.mascotas.data.Clinica
import com.huellitas.mascotas.ui.SpeciesBadge

/**
 * Tarjeta de una clínica (se usa en Urgencias).
 * Tareas de Nicolás: N-27 (botón Llamar) y N-28 (botón Cómo llegar).
 */
@Composable
fun ClinicaCard(clinica: Clinica, modifier: Modifier = Modifier) {
    Card(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SpeciesBadge(if (clinica.abierta24h) "🚨" else "🏥", size = 48)
                Column(Modifier.weight(1f)) {
                    Text(clinica.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${clinica.direccion} · ${clinica.ciudad}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                (if (clinica.abierta24h) "Abierta 24 horas · " else "") + clinica.distancia,
                style = MaterialTheme.typography.bodyMedium,
            )
            // TODO(N-27): botón "Llamar" (Intent ACTION_DIAL con clinica.telefono)
            // TODO(N-28): botón "Cómo llegar" (Intent ACTION_VIEW con un URI geo:)
        }
    }
}
