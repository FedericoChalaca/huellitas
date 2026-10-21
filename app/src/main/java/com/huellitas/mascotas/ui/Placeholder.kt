package com.huellitas.mascotas.ui

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

/**
 * Un ViewModel que vive mientras viva la actividad entera, no una sola pantalla: sirve para compartir estado
 * entre dos pantallas (por ejemplo el carrito de medicamentos entre Medicamentos y Cotización).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
inline fun <reified T : ViewModel> activityViewModel(): T {
    val activity = LocalActivity.current as? ComponentActivity ?: error("Esta pantalla necesita una actividad compatible.")
    return viewModel<T>(activity)
}

/** Tarjeta grande y tocable de un menú (Seguro, Más). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuCard(emoji: String, titulo: String, subtitulo: String, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SpeciesBadge(emoji, size = 48)
            Column(Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
