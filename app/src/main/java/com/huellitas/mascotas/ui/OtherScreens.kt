@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.Session
import com.huellitas.mascotas.data.ThemeMode

// ---------------------------------------------------------------- Configuración

@Composable
private fun InfoCard(title: String, emoji: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Tarjeta(modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(emoji, fontSize = 20.sp)
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            content()
        }
    }
}

/** FilterChip verde cuando está elegido, igual en toda la app. */
@Composable
private fun ChipOpcion(texto: String, selected: Boolean, onClick: () -> Unit) {
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

@Composable
fun SettingsScreen(nav: NavController, vm: MainViewModel) {
    val session by vm.session.collectAsStateWithLifecycle()
    val settings by vm.settings.collectAsStateWithLifecycle()
    val user = (session as? Session.LoggedIn)?.user
    var askDelete by remember { mutableStateOf(false) }

    BackScaffold("Ajustes", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            InfoCard("Cuenta", "👤", Modifier.aparecer()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Círculo con la inicial del nombre.
                    Box(
                        Modifier.size(52.dp).background(encabezadoBrush(), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            user?.name?.trim()?.firstOrNull()?.uppercase() ?: "🐾",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colorSobreEncabezado(),
                        )
                    }
                    Column {
                        Text(user?.name ?: "", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(user?.email ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Text(
                    "Tus mascotas y vacunas se guardan solo en este celular.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            InfoCard("Apariencia", "🎨", Modifier.aparecer(1)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        ChipOpcion(mode.label, settings.theme == mode) { vm.setTheme(mode) }
                    }
                }
            }
            InfoCard("Aviso de vacunas", "🔔", Modifier.aparecer(2)) {
                Text("Marcar una vacuna como «Pronto» cuando falten:")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(3, 7, 14).forEach { days ->
                        ChipOpcion("$days días", settings.warnDays == days) { vm.setWarnDays(days) }
                    }
                }
            }
            OutlinedButton(
                onClick = { vm.logout() },
                modifier = Modifier.fillMaxWidth().height(50.dp).aparecer(3),
                shape = RoundedCornerShape(16.dp),
            ) { Text("Cerrar sesión") }
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

    BackScaffold("Créditos", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(Modifier.fillMaxWidth().aparecer().background(encabezadoBrush(), RoundedCornerShape(24.dp))) {
                HuellasDeAdorno(Modifier.matchParentSize())
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        Modifier.size(88.dp).flotar(4f).background(colorSobreEncabezado().copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Text("🐾", fontSize = 44.sp) }
                    Text("Huellitas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colorSobreEncabezado())
                    Text("Versión $version", color = colorSobreEncabezado().copy(alpha = 0.85f))
                    Text("Mascotas, citas y seguro de demostración", style = MaterialTheme.typography.bodySmall, color = colorSobreEncabezado().copy(alpha = 0.85f))
                }
            }
            InfoCard("Trabajo académico", "🎓", Modifier.aparecer(1)) {
                CreditRow("Estudiantes", "Federico Martínez López y Nicolás Castrillón")
                CreditRow("Programa", "Ingeniería de Sistemas")
                CreditRow("Universidad", "Universidad Pontificia Bolivariana")
                CreditRow("Materia", "Aplicaciones Móviles")
                CreditRow("Docente", "Andrés Bedoya Tobón")
                CreditRow("Periodo", "2026-2")
            }
            InfoCard("Con qué está hecha", "🛠️", Modifier.aparecer(2)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Kotlin", "Jetpack Compose", "Room", "DataStore", "Navigation Compose").forEach {
                        EtiquetaEstado(it, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
                Text("Room guarda la base de datos en el celular; DataStore, la sesión y los ajustes.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
