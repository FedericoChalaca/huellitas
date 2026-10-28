@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui.medicamentos

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.huellitas.mascotas.MedicamentosViewModel
import com.huellitas.mascotas.data.CATEGORIAS_MEDICAMENTO
import com.huellitas.mascotas.data.MEDICAMENTOS
import com.huellitas.mascotas.data.Medicamento
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.NotaDemo
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.activityViewModel
import com.huellitas.mascotas.ui.aparecer
import kotlinx.coroutines.delay

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
            // El carrito da un saltito cada vez que se agrega algo.
            val salto = remember { Animatable(1f) }
            LaunchedEffect(unidades) {
                if (unidades > 0) {
                    salto.snapTo(1.35f)
                    salto.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
            }
            IconButton(onClick = { nav.navigate("cotizacion") }) {
                BadgedBox(
                    badge = { if (unidades > 0) Badge(containerColor = MaterialTheme.colorScheme.secondary) { Text("$unidades") } },
                    modifier = Modifier.scale(salto.value),
                ) { Icon(Icons.Filled.ShoppingCart, contentDescription = "Ver mi cotización") }
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                consulta, { consulta = it },
                placeholder = { Text("Buscar medicamento") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(50),
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
            )
            // Categorías en una sola fila que se desliza de lado.
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item { ChipCategoria("Todos", categoria == null) { categoria = null } }
                items(CATEGORIAS_MEDICAMENTO) { c -> ChipCategoria(c, categoria == c) { categoria = c } }
            }
            if (visibles.isEmpty()) {
                EmptyState("🔍", "Sin resultados", if (consulta.isBlank()) "No hay medicamentos en esta categoría." else "No encontramos «$consulta».")
            } else {
                LazyColumn(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    itemsIndexed(visibles, key = { _, m -> m.id }) { i, m ->
                        Tarjeta(onClick = { elegido = m }, modifier = Modifier.fillMaxWidth().animateItem().aparecer(i)) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                SpeciesBadge("💊", size = 44)
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(m.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(m.presentacion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(pesos(m.precioCop), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                BotonAgregar { vm.agregar(m) }
                            }
                        }
                    }
                    item {
                        NotaDemo("Demostración académica: precios inventados; no se vende nada.")
                    }
                }
            }
        }
    }

    elegido?.let { m ->
        AlertDialog(
            onDismissRequest = { elegido = null },
            icon = { Text("💊", fontSize = 32.sp) },
            title = { Text(m.nombre) },
            text = { Text("${m.categoria}\n${m.presentacion}\n${pesos(m.precioCop)}") },
            confirmButton = { TextButton(onClick = { vm.agregar(m); elegido = null }) { Text("Agregar") } },
            dismissButton = { TextButton(onClick = { elegido = null }) { Text("Cerrar") } },
        )
    }
}

@Composable
private fun ChipCategoria(texto: String, selected: Boolean, onClick: () -> Unit) {
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

/** «+ Agregar» que confirma con «✓ Listo» un momento después de tocarlo. */
@Composable
private fun BotonAgregar(onClick: () -> Unit) {
    var listo by remember { mutableStateOf(false) }
    LaunchedEffect(listo) {
        if (listo) {
            delay(1200)
            listo = false
        }
    }
    FilledTonalButton(onClick = { onClick(); listo = true }) {
        AnimatedContent(listo, transitionSpec = { (fadeIn() + scaleIn()) togetherWith fadeOut() }, label = "agregar") { hecho ->
            Text(if (hecho) "✓ Listo" else "+ Agregar")
        }
    }
}
