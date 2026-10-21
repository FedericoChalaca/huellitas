package com.huellitas.mascotas.ui.seguro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huellitas.mascotas.data.PLANES
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.ui.BackScaffold

/** Comparación de los tres planes (los datos están en data/Insurer.kt, PLANES). */
@Composable
fun PlanesScreen(nav: NavController) {
    BackScaffold("Planes del seguro", onBack = { nav.popBackStack() }) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(PLANES, key = { it.id }) { plan ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(plan.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("${pesos(plan.precioMesCop)} al mes", style = MaterialTheme.typography.titleMedium)
                        Text("El seguro paga el ${plan.coberturaPct} % de cada gasto")
                        Text("Límite anual: ${pesos(plan.limiteAnualCop)}")
                        Text(if (plan.deducibleCop == 0L) "Sin deducible" else "Deducible: ${pesos(plan.deducibleCop)}")
                        plan.incluye.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }
            item {
                Text(
                    "Demostración académica: planes y precios inventados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
