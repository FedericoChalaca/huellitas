@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.Session
import com.huellitas.mascotas.data.ThemeMode

// ---------------------------------------------------------------- Configuración

@Composable
private fun InfoCard(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
fun SettingsScreen(nav: NavController, vm: MainViewModel) {
    val session by vm.session.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val user = (session as? Session.LoggedIn)?.user
    var askDelete by remember { mutableStateOf(false) }

    TabScaffold(nav, "mas", "Ajustes") { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            InfoCard("Cuenta") {
                Text(user?.name ?: "", style = MaterialTheme.typography.titleLarge)
                Text(user?.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Tus mascotas y vacunas se guardan solo en este celular.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            InfoCard("Apariencia") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(selected = settings.theme == mode, onClick = { vm.setTheme(mode) }, label = { Text(mode.label) })
                    }
                }
            }
            InfoCard("Aviso de vacunas") {
                Text("Marcar una vacuna como «Pronto» cuando falten:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3, 7, 14).forEach { days ->
                        FilterChip(selected = settings.warnDays == days, onClick = { vm.setWarnDays(days) }, label = { Text("$days días") })
                    }
                }
            }
            OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text("Cerrar sesión") }
            TextButton(onClick = { askDelete = true }, modifier = Modifier.fillMaxWidth()) {
                Text("Borrar mi cuenta y mis datos", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (askDelete) {
        ConfirmDialog(
            title = "¿Borrar tu cuenta?",
            text = "Se borran tu cuenta, tus mascotas y sus vacunas de este celular. No se puede deshacer.",
            confirmLabel = "Borrar todo",
            onConfirm = {
                askDelete = false
                vm.deleteAccount()
            },
            onDismiss = { askDelete = false },
        )
    }
}

// ---------------------------------------------------------------- Créditos

@Composable
private fun CreditRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.4f))
        Text(value, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.6f))
    }
}

@Composable
fun CreditsScreen(nav: NavController) {
    val context = LocalContext.current
    val version = remember {
        runCatching { context.packageManager.getPackageInfo(context.packageName, 0).versionName }.getOrNull() ?: "1.0.0"
    }

    TabScaffold(nav, "mas", "Créditos") { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Card(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    SpeciesBadge("🐾", size = 64)
                    Column {
                        Text("Huellitas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Versión $version", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Control de mascotas y vacunación", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            InfoCard("Trabajo académico") {
                CreditRow("Estudiante", "Federico Martínez López")
                CreditRow("Programa", "Ingeniería de Sistemas")
                CreditRow("Universidad", "Universidad Pontificia Bolivariana")
                CreditRow("Materia", "Aplicaciones Móviles")
                CreditRow("Docente", "Andrés Bedoya Tobón")
                CreditRow("Periodo", "2026-2")
            }
            InfoCard("Con qué está hecha") {
                Text("Kotlin y Jetpack Compose")
                Text("Room: base de datos en el celular")
                Text("DataStore: sesión y ajustes")
                Text("Navigation Compose: las pantallas")
            }
        }
    }
}
