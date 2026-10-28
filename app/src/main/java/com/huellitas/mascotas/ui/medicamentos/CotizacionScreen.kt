@file:OptIn(ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui.medicamentos

import androidx.compose.animation.AnimatedContent
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.MainViewModel
import com.huellitas.mascotas.MedicamentosViewModel
import com.huellitas.mascotas.SeguroViewModel
import com.huellitas.mascotas.data.calcularTotales
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.data.speciesEmoji
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.BotonPrincipal
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.NotaDemo
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.activityViewModel
import com.huellitas.mascotas.ui.aparecer
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/** La cotización en curso (el carrito): cantidades, el descuento del plan de la mascota elegida y los totales. */
@Composable
fun CotizacionScreen(nav: NavController, seguro: SeguroViewModel = viewModel()) {
    val vm = activityViewModel<MedicamentosViewModel>()
    val main = activityViewModel<MainViewModel>()
    val carrito by vm.carrito.collectAsStateWithLifecycle()
    val pets by main.pets.collectAsStateWithLifecycle()
    var petId by remember { mutableStateOf<Long?>(null) }
    val poliza by remember(petId) { petId?.let { seguro.poliza(it) } ?: flowOf(null) }.collectAsStateWithLifecycle(initialValue = null)
    val plan = poliza?.let { planPorId(it.planId) }
    val totales = calcularTotales(carrito, plan?.coberturaPct ?: 0)
    val scope = rememberCoroutineScope()

    BackScaffold("Mi cotización", onBack = { nav.popBackStack() }) { padding ->
        if (carrito.isEmpty()) {
            EmptyState(
                "🧾", "Tu cotización está vacía", "Agrega medicamentos desde el catálogo.", Modifier.padding(padding),
                accion = { OutlinedButton(onClick = { nav.popBackStack() }) { Text("Ver catálogo") } },
            )
            return@BackScaffold
        }
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("¿Para qué mascota?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = petId == null, onClick = { petId = null }, label = { Text("Sin mascota") })
                    pets.forEach { p ->
                        FilterChip(selected = petId == p.id, onClick = { petId = p.id }, label = { Text("${speciesEmoji(p.species)} ${p.name}") })
                    }
                }
            }
            itemsIndexed(carrito, key = { _, l -> l.medicamento.id }) { i, linea ->
                Tarjeta(Modifier.fillMaxWidth().animateItem().aparecer(i)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SpeciesBadge("💊", size = 40)
                        Column(Modifier.weight(1f)) {
                            Text(linea.medicamento.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(pesos(linea.medicamento.precioCop), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        // Selector de cantidad en una pastilla: − número +
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(50)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TextButton(onClick = { vm.cambiarCantidad(linea.medicamento, linea.cantidad - 1) }) { Text("−", fontWeight = FontWeight.Bold) }
                                AnimatedContent(linea.cantidad, transitionSpec = { (fadeIn() + scaleIn()) togetherWith fadeOut() }, label = "cantidad") { n ->
                                    Text("$n", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                TextButton(onClick = { vm.cambiarCantidad(linea.medicamento, linea.cantidad + 1) }) { Text("+", fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
            }
            item {
                Tarjeta(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Fila("Subtotal", pesos(totales.subtotalCop))
                        Fila("Seguro paga (${plan?.coberturaPct ?: 0} %)", "- ${pesos(totales.coberturaCop)}")
                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                        Fila("Tú pagas", pesos(totales.copagoCop), fuerte = true)
                        if (petId != null && plan == null) {
                            Text("Esta mascota no tiene póliza: pagas el 100 %.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                BotonPrincipal(
                    "Guardar cotización",
                    onClick = {
                        scope.launch {
                            if (vm.guardarCotizacion(petId, totales)) nav.navigate("cotizaciones") { popUpTo("seguro") }
                        }
                    },
                )
                NotaDemo("Demostración académica: no se compra ni se cobra nada.", Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String, fuerte: Boolean = false) {
    val estilo = if (fuerte) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(etiqueta, style = estilo, fontWeight = if (fuerte) FontWeight.Bold else FontWeight.Normal)
        // Los totales cambian con un fundido al subir o bajar cantidades.
        AnimatedContent(valor, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "valorFila") { v ->
            Text(v, style = estilo, fontWeight = if (fuerte) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
