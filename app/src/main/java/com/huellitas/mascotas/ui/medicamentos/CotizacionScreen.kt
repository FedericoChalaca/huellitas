@file:OptIn(ExperimentalLayoutApi::class)

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.activityViewModel
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
            Column(Modifier.padding(padding)) {
                EmptyState("🧾", "Tu cotización está vacía", "Agrega medicamentos desde el catálogo.", Modifier.weight(1f))
                OutlinedButton(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth().padding(16.dp)) { Text("Ver catálogo") }
            }
            return@BackScaffold
        }
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text("¿Para qué mascota?", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = petId == null, onClick = { petId = null }, label = { Text("Sin mascota") })
                    pets.forEach { p ->
                        FilterChip(selected = petId == p.id, onClick = { petId = p.id }, label = { Text("${speciesEmoji(p.species)} ${p.name}") })
                    }
                }
            }
            items(carrito, key = { it.medicamento.id }) { linea ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(Modifier.weight(1f)) {
                            Text(linea.medicamento.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(pesos(linea.medicamento.precioCop), style = MaterialTheme.typography.bodyMedium)
                        }
                        OutlinedButton(onClick = { vm.cambiarCantidad(linea.medicamento, linea.cantidad - 1) }) { Text("−") }
                        Text("${linea.cantidad}", style = MaterialTheme.typography.titleMedium)
                        OutlinedButton(onClick = { vm.cambiarCantidad(linea.medicamento, linea.cantidad + 1) }) { Text("+") }
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Fila("Subtotal", pesos(totales.subtotalCop))
                        Fila("Seguro paga (${plan?.coberturaPct ?: 0} %)", "- ${pesos(totales.coberturaCop)}")
                        Fila("Tú pagas", pesos(totales.copagoCop), fuerte = true)
                        if (petId != null && plan == null) {
                            Text("Esta mascota no tiene póliza: pagas el 100 %.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        scope.launch {
                            if (vm.guardarCotizacion(petId, totales)) nav.navigate("cotizaciones") { popUpTo("seguro") }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("Guardar cotización") }
                Text(
                    "Demostración académica: no se compra ni se cobra nada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun Fila(etiqueta: String, valor: String, fuerte: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, fontWeight = if (fuerte) FontWeight.Bold else FontWeight.Normal)
        Text(valor, fontWeight = if (fuerte) FontWeight.Bold else FontWeight.Normal)
    }
}
