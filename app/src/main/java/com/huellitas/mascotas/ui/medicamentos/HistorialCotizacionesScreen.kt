@file:OptIn(ExperimentalMaterial3Api::class)

package com.huellitas.mascotas.ui.medicamentos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MedicamentosViewModel
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Quote
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.ConfirmDialog
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.activityViewModel
import com.huellitas.mascotas.ui.aparecer

/** Cotizaciones guardadas: la lista, y al tocar una, sus renglones, con la opción de borrarla. */
@Composable
fun HistorialCotizacionesScreen(nav: NavController) {
    val vm = activityViewModel<MedicamentosViewModel>()
    val lista by vm.cotizaciones.collectAsStateWithLifecycle()
    var abierta by remember { mutableStateOf<Quote?>(null) }
    var aBorrar by remember { mutableStateOf<Quote?>(null) }

    BackScaffold("Mis cotizaciones", onBack = { nav.popBackStack() }) { padding ->
        if (lista.isEmpty()) {
            EmptyState(
                "🧾", "Aún no hay cotizaciones", "Arma una desde Cotizar medicamentos.", Modifier.padding(padding),
                accion = { FilledTonalButton(onClick = { nav.navigate("medicamentos") }) { Text("Cotizar medicamentos") } },
            )
        } else {
            LazyColumn(
                Modifier.padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                itemsIndexed(lista, key = { _, q -> q.id }) { i, q ->
                    Tarjeta(onClick = { abierta = q }, modifier = Modifier.fillMaxWidth().animateItem().aparecer(i)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            SpeciesBadge("🧾", size = 44)
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(Dates.format(q.createdDay), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Tú pagas ${pesos(q.totalCop - q.coveredCop)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                                Text(q.status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(pesos(q.totalCop), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    abierta?.let { q ->
        val renglones by remember(q.id) { vm.renglones(q.id) }.collectAsStateWithLifecycle(initialValue = emptyList())
        AlertDialog(
            onDismissRequest = { abierta = null },
            icon = { Text("🧾", fontSize = 32.sp) },
            title = { Text("Cotización del ${Dates.format(q.createdDay)}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    renglones.forEach { Text("${it.quantity} × ${it.name} — ${pesos(it.unitPriceCop * it.quantity)}") }
                    Text("Total ${pesos(q.totalCop)} · el seguro paga ${pesos(q.coveredCop)}", fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = { TextButton(onClick = { abierta = null }) { Text("Cerrar") } },
            dismissButton = {
                TextButton(onClick = { aBorrar = q; abierta = null }) { Text("Borrar", color = MaterialTheme.colorScheme.error) }
            },
        )
    }

    aBorrar?.let { q ->
        ConfirmDialog(
            title = "¿Borrar esta cotización?",
            text = "No se puede deshacer.",
            confirmLabel = "Borrar",
            onConfirm = {
                vm.borrarCotizacion(q.id)
                aBorrar = null
            },
            onDismiss = { aBorrar = null },
        )
    }
}
