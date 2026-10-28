package com.huellitas.mascotas.ui.seguro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.huellitas.mascotas.data.PLANES
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.NotaDemo
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.aparecer
import com.huellitas.mascotas.ui.colorSobreEncabezado
import com.huellitas.mascotas.ui.esOscuro

/** Comparación de los tres planes (los datos están en data/Insurer.kt, PLANES). */
@Composable
fun PlanesScreen(nav: NavController) {
    BackScaffold("Planes del seguro", onBack = { nav.popBackStack() }) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            itemsIndexed(PLANES, key = { _, p -> p.id }) { i, plan ->
                Tarjeta(Modifier.fillMaxWidth().aparecer(i)) {
                    // Franja de arriba con el nombre y el precio; el tono sube con el nivel del plan.
                    Box(Modifier.fillMaxWidth().background(fondoDelPlan(i)).padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(EMOJI_PLANES.getOrElse(i) { "🛡️" }, fontSize = 30.sp)
                            Column(Modifier.weight(1f)) {
                                Text(plan.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colorSobreEncabezado())
                                Text("${pesos(plan.precioMesCop)} al mes", style = MaterialTheme.typography.titleMedium, color = colorSobreEncabezado())
                            }
                            Surface(color = colorSobreEncabezado().copy(alpha = 0.2f), contentColor = colorSobreEncabezado(), shape = RoundedCornerShape(50)) {
                                Text("${plan.coberturaPct} %", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }
                    }
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("El seguro paga el ${plan.coberturaPct} % de cada gasto")
                        Text("Límite anual: ${pesos(plan.limiteAnualCop)}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (plan.deducibleCop == 0L) "Sin deducible" else "Deducible: ${pesos(plan.deducibleCop)}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        HorizontalDivider()
                        plan.incluye.forEach {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                                Text(it, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
            item {
                NotaDemo("Demostración académica: planes y precios inventados.")
            }
        }
    }
}

private val EMOJI_PLANES = listOf("🌱", "🌿", "🌳")

/** Verde más claro para Básico y más profundo para Premium. */
@Composable
private fun fondoDelPlan(nivel: Int): Brush {
    val oscuro = MaterialTheme.colorScheme.esOscuro
    val colores = when (nivel) {
        0 -> if (oscuro) listOf(Color(0xFF2C5E50), Color(0xFF1E4A3D)) else listOf(Color(0xFF5FA88F), Color(0xFF4A9A80))
        1 -> if (oscuro) listOf(Color(0xFF1B5A48), Color(0xFF14463A)) else listOf(Color(0xFF3F8C74), Color(0xFF2F6F5E))
        else -> if (oscuro) listOf(Color(0xFF0E3B2F), Color(0xFF0A2E25)) else listOf(Color(0xFF2F6F5E), Color(0xFF1F5143))
    }
    return Brush.linearGradient(colores)
}
