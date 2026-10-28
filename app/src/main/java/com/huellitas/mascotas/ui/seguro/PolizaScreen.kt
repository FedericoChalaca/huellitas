package com.huellitas.mascotas.ui.seguro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.SeguroViewModel
import com.huellitas.mascotas.data.ASEGURADORA
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.BotonPrincipal
import com.huellitas.mascotas.ui.EmptyState
import com.huellitas.mascotas.ui.HuellasDeAdorno
import com.huellitas.mascotas.ui.NotaDemo
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.aparecer
import com.huellitas.mascotas.ui.colorSobreEncabezado
import com.huellitas.mascotas.ui.encabezadoBrush

@Composable
private fun Dato(etiqueta: String, valor: String, color: Color) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(etiqueta, color = color.copy(alpha = 0.8f), modifier = Modifier.weight(0.45f))
        Text(valor, color = color, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.55f))
    }
}

/** Póliza de una mascota: sus datos, lo que cubre el plan y cuánto del límite anual lleva usado. */
@Composable
fun PolizaScreen(nav: NavController, petId: Long, vm: SeguroViewModel = viewModel()) {
    val poliza by remember(petId) { vm.poliza(petId) }.collectAsStateWithLifecycle(initialValue = null)
    val plan = poliza?.let { planPorId(it.planId) }

    BackScaffold("Póliza", onBack = { nav.popBackStack() }) { padding ->
        val p = poliza
        if (p == null || plan == null) {
            EmptyState(
                "🛡️", "Sin póliza", "Esta mascota todavía no está afiliada.", Modifier.padding(padding),
                accion = { BotonPrincipal("Afiliar", onClick = { nav.navigate("seguro/afiliar/$petId") }) },
            )
            return@BackScaffold
        }
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // La póliza se ve como un carné: verde, con el número y la vigencia.
            val sobre = colorSobreEncabezado()
            Box(Modifier.fillMaxWidth().aparecer().background(encabezadoBrush(), RoundedCornerShape(24.dp))) {
                HuellasDeAdorno(Modifier.matchParentSize())
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🛡️ $ASEGURADORA", style = MaterialTheme.typography.labelLarge, color = sobre.copy(alpha = 0.85f))
                    Text("Plan ${plan.nombre}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = sobre)
                    Text(p.number, style = MaterialTheme.typography.titleMedium, color = sobre, letterSpacing = 2.sp)
                    Spacer(Modifier.height(4.dp))
                    Dato("Desde", Dates.format(p.startDay), sobre)
                    Dato("Hasta", Dates.format(p.endDay), sobre)
                    Dato("Cobertura", "${plan.coberturaPct} %", sobre)
                    Dato("Precio", "${pesos(plan.precioMesCop)} al mes", sobre)
                }
            }
            Tarjeta(Modifier.fillMaxWidth().aparecer(1)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Qué incluye", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    plan.incluye.forEach {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Text(it)
                        }
                    }
                }
            }
            Tarjeta(Modifier.fillMaxWidth().aparecer(2)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Límite anual", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Usado: ${pesos(p.usedCop)} de ${pesos(plan.limiteAnualCop)}")
                    BarraDeUso(p.usedCop, plan.limiteAnualCop)
                    Text(
                        "Te quedan ${pesos(maxOf(0L, plan.limiteAnualCop - p.usedCop))}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            BotonPrincipal("Pedir cita", onClick = { nav.navigate("cita/pedir/$petId/-") }, modifier = Modifier.aparecer(3))
            OutlinedButton(
                onClick = { nav.navigate("seguro/afiliar/$petId") },
                modifier = Modifier.fillMaxWidth().height(50.dp).aparecer(4),
                shape = RoundedCornerShape(16.dp),
            ) { Text("Cambiar de plan") }
            NotaDemo("Demostración académica: no es un seguro real y no se cobra nada.")
        }
    }
}
