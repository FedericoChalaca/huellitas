@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.VaccineStatus

private const val MILLIS_PER_DAY = 86_400_000L

data class Tab(val route: String, val label: String, val icon: ImageVector)

val Tabs = listOf(
    Tab("pets", "Mascotas", Icons.Filled.Favorite),
    Tab("pendientes", "Pendientes", Icons.Filled.Notifications),
    Tab("citas", "Citas", Icons.Filled.DateRange),
    Tab("seguro", "Seguro", Icons.Filled.CheckCircle),
    Tab("mas", "Más", Icons.Filled.Menu),
)

/** Pantalla de las cuatro secciones del menú de abajo. */
@Composable
fun TabScaffold(
    nav: NavController,
    current: String,
    title: String,
    fab: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(title, fontWeight = FontWeight.Bold) }) },
        bottomBar = {
            NavigationBar {
                Tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = tab.route == current,
                        onClick = {
                            if (tab.route != current) {
                                nav.navigate(tab.route) {
                                    popUpTo("pets") { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
        floatingActionButton = fab,
        content = content,
    )
}

/** Pantalla interna (detalle o formulario): flecha para volver. */
@Composable
fun BackScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
                },
                actions = actions,
            )
        },
        content = content,
    )
}

@Composable
fun EmptyState(emoji: String, title: String, text: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(emoji, fontSize = 56.sp)
        Spacer(Modifier.height(12.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
        Spacer(Modifier.height(4.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun ConfirmDialog(title: String, text: String, confirmLabel: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel, color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } },
    )
}

@Composable
fun StatusChip(status: VaccineStatus) {
    val scheme = MaterialTheme.colorScheme
    val (bg, fg) = when (status) {
        VaccineStatus.OVERDUE -> scheme.errorContainer to scheme.onErrorContainer
        VaccineStatus.SOON -> scheme.secondaryContainer to scheme.onSecondaryContainer
        VaccineStatus.OK -> scheme.primaryContainer to scheme.onPrimaryContainer
        VaccineStatus.NONE -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
    Surface(color = bg, contentColor = fg, shape = RoundedCornerShape(50)) {
        Text(
            status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

/** Círculo con el emoji de la especie. */
@Composable
fun SpeciesBadge(emoji: String, size: Int = 56) {
    Box(
        Modifier.size(size.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center,
    ) { Text(emoji, fontSize = (size / 2).sp) }
}

/** Campo de fecha: se toca el calendario del costado y sale el selector. */
@Composable
fun DateField(label: String, day: Long?, onChange: (Long?) -> Unit, clearable: Boolean = false) {
    var open by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = day?.let { Dates.format(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Row {
                if (clearable && day != null) {
                    IconButton(onClick = { onChange(null) }) { Icon(Icons.Filled.Clear, contentDescription = "Quitar la fecha") }
                }
                IconButton(onClick = { open = true }) { Icon(Icons.Filled.DateRange, contentDescription = "Elegir la fecha") }
            }
        },
    )
    if (open) {
        val state = rememberDatePickerState(initialSelectedDateMillis = day?.let { it * MILLIS_PER_DAY })
        DatePickerDialog(
            onDismissRequest = { open = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onChange(it / MILLIS_PER_DAY) }
                    open = false
                }) { Text("Listo") }
            },
            dismissButton = { TextButton(onClick = { open = false }) { Text("Cancelar") } },
        ) { DatePicker(state = state) }
    }
}
