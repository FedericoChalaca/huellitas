@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.urgencias

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huellitas.mascotas.data.Clinica
import com.huellitas.mascotas.ui.SpeciesBadge

/**
 * Tarjeta de una clínica (se usa en Urgencias), con los botones para llamar y para ver cómo llegar.
 * Los dos abren otra app del celular (el marcador y el mapa): por eso la app no necesita permisos.
 */
@Composable
fun ClinicaCard(clinica: Clinica, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    fun abrir(intent: Intent, sinApp: String) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, sinApp, Toast.LENGTH_SHORT).show()
        }
    }
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // ACTION_DIAL solo abre el marcador con el número escrito: no necesita permiso de llamadas.
                OutlinedButton(
                    onClick = {
                        val numero = clinica.telefono.filter { it.isDigit() }
                        abrir(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")), "No hay una app para llamar")
                    },
                ) { Text("📞 Llamar") }
                OutlinedButton(
                    onClick = {
                        val consulta = "${clinica.nombre}, ${clinica.ciudad}"
                        abrir(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(consulta))), "No hay una app de mapas")
                    },
                ) { Text("🧭 Cómo llegar") }
            }
        }
    }
}
