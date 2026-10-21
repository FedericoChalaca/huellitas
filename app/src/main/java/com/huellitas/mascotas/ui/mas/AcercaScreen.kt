package com.huellitas.mascotas.ui.mas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huellitas.mascotas.data.ASEGURADORA
import com.huellitas.mascotas.ui.BackScaffold

/** Dice con claridad qué parte de la app es de mentira (la aseguradora) y cuál es de verdad (tus datos). */
@Composable
fun AcercaScreen(nav: NavController) {
    BackScaffold("Acerca de la demo", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "Huellitas es un trabajo académico de la Universidad Pontificia Bolivariana. «$ASEGURADORA», sus planes, precios, " +
                    "clínicas, teléfonos y medicamentos son datos inventados y no existe ninguna aseguradora real. " +
                    "No se hacen cobros, no se agendan citas ni compras de verdad y no se avisa a ninguna clínica.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Es de mentira", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("• La aseguradora, sus planes y sus precios")
                    Text("• Las clínicas, sus direcciones, teléfonos y horarios")
                    Text("• El catálogo de medicamentos y sus precios")
                    Text("• Las pólizas, las citas y las cotizaciones: solo viven en este celular")
                }
            }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Es de verdad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("• Tus mascotas, sus vacunas y desparasitaciones, con sus fechas y avisos")
                    Text("• Tu cuenta, guardada solo en este celular")
                }
            }
            Text(
                "La app no usa internet ni comparte nada con nadie.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
