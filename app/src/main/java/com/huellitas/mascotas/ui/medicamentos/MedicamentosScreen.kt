@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui.medicamentos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MedicamentosViewModel
import com.huellitas.mascotas.data.CATEGORIAS_MEDICAMENTO
import com.huellitas.mascotas.data.MEDICAMENTOS
import com.huellitas.mascotas.data.Medicamento
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.activityViewModel

/** Catálogo de medicamentos para cotizar: buscador, categorías, ficha y botón para agregar al carrito. */
@Composable
fun MedicamentosScreen(nav: NavController) {
    val vm = activityViewModel<MedicamentosViewModel>()
    val carrito by vm.carrito.collectAsStateWithLifecycle()
    var consulta by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf<String?>(null) }
    var elegido by remember { mutableStateOf<Medicamento?>(null) }
    val visibles = MEDICAMENTOS.filter {
        it.nombre.contains(consulta.trim(), ignoreCase = true) && (categoria == null || it.categoria == categoria)
    }
    val unidades = carrito.sumOf { it.cantidad }

    BackScaffold(
        "Cotizar medicamentos",
        onBack = { nav.popBackStack() },
        actions = {
            IconButton(onClick = { nav.navigate("cotizacion") }) {
                BadgedBox(badge = { if (unidades > 0) Badge { Text("$unidades") } }) { Text("🛒") }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                consulta, { consulta = it },
                placeholder = { Text("Buscar medicamento") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            )
            FlowRow(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = categoria == null, onClick = { categoria = null }, label = { Text("Todos") })
                CATEGORIAS_MEDICAMENTO.forEach { c ->
                    FilterChip(selected = categoria == c, onClick = { categoria = c }, label = { Text(c) })
                }
            }
            if (visibles.isEmpty()) {
                EmptyState("🔍", "Sin resultados", "No encontramos «$consulta».")
            } else {
                LazyColumn(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(visibles, key = { it.id }) { m ->
                        Card(onClick = { elegido = m }, modifier = Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(m.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(m.presentacion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(pesos(m.precioCop), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                }
                                FilledTonalButton(onClick = { vm.agregar(m) }) { Text("+ Agregar") }
                            }
                        }
                    }
                    item {
                        Text(
                            "Demostración académica: precios inventados; no se vende nada.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    elegido?.let { m ->
        AlertDialog(
            onDismissRequest = { elegido = null },
            title = { Text(m.nombre) },
            text = { Text("${m.categoria}\n${m.presentacion}\n${pesos(m.precioCop)}") },
            confirmButton = { TextButton(onClick = { vm.agregar(m); elegido = null }) { Text("Agregar") } },
            dismissButton = { TextButton(onClick = { elegido = null }) { Text("Cerrar") } },
        )
    }
}
