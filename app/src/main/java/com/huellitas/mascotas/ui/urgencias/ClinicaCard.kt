@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.urgencias

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import com.huellitas.mascotas.ui.DatoConIcono
import com.huellitas.mascotas.ui.EtiquetaEstado
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.Tarjeta

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
    Tarjeta(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SpeciesBadge(if (clinica.abierta24h) "🚨" else "🏥", size = 48)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(clinica.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    DatoConIcono(Icons.Filled.Place, "${clinica.direccion} · ${clinica.ciudad}")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (clinica.abierta24h) EtiquetaEstado("Abierta 24 h", MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer, latir = true)
                EtiquetaEstado(clinica.distancia, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // ACTION_DIAL solo abre el marcador con el número escrito: no necesita permiso de llamadas.
                FilledTonalButton(
                    onClick = {
                        val numero = clinica.telefono.filter { it.isDigit() }
                        abrir(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero")), "No hay una app para llamar")
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Llamar")
                }
                OutlinedButton(
                    onClick = {
                        val consulta = "${clinica.nombre}, ${clinica.ciudad}"
                        abrir(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(consulta))), "No hay una app de mapas")
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Filled.Place, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Cómo llegar")
                }
            }
        }
    }
}
