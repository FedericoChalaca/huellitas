@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.huellitas.mascotas.ui.desparasitacion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.huellitas.mascotas.DesparasitacionesViewModel
import com.huellitas.mascotas.data.DEWORMING_KINDS
import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Deworming
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.BotonPrincipal
import com.huellitas.mascotas.ui.CabeceraFormulario
import com.huellitas.mascotas.ui.DateField
import com.huellitas.mascotas.ui.MensajeError
import kotlinx.coroutines.launch

private val PRODUCTOS_COMUNES = listOf("Antiparasitario oral", "Pipeta antipulgas", "Desparasitante líquido")

/** Formulario para registrar o editar una desparasitación. [id] es 0 si es una nueva. */
@Composable
fun DesparasitacionFormScreen(nav: NavController, petId: Long, id: Long, vm: DesparasitacionesViewModel = viewModel()) {
    var product by remember { mutableStateOf("") }
    var kind by remember { mutableStateOf(DEWORMING_KINDS.first()) }
    var applied by remember { mutableStateOf<Long?>(Dates.today()) }
    var next by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var loaded by remember { mutableStateOf(id == 0L) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(id) {
        if (id != 0L) {
            vm.get(id)?.let {
                product = it.product
                kind = it.kind
                applied = it.appliedDay
                next = it.nextDueDay
                notes = it.notes
            }
            loaded = true
        }
    }

    fun submit() {
        val appliedDay = applied ?: return
        val nextDay = next
        if (nextDay != null && nextDay < appliedDay) {
            error = "La próxima dosis debe ser después de la aplicada."
            return
        }
        scope.launch {
            vm.guardar(Deworming(id = id, petId = petId, product = product.trim(), kind = kind, appliedDay = appliedDay, nextDueDay = nextDay, notes = notes.trim()))
            nav.popBackStack()
        }
    }

    BackScaffold(if (id == 0L) "Registrar desparasitación" else "Editar desparasitación", onBack = { nav.popBackStack() }) { padding ->
        Column(
            Modifier.padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            CabeceraFormulario("🛡️", "Registra el producto y te recordamos la próxima dosis.")
            OutlinedTextField(
                product, { product = it },
                label = { Text("Producto") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PRODUCTOS_COMUNES.forEach { p -> SuggestionChip(onClick = { product = p }, label = { Text(p) }) }
            }
            Text("Tipo", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DEWORMING_KINDS.forEach { k -> FilterChip(selected = kind == k, onClick = { kind = k }, label = { Text(k) }) }
            }
            DateField("Fecha de aplicación", applied, { applied = it ?: applied; error = null })
            DateField("Próxima dosis (opcional)", next, { next = it; error = null }, clearable = true)
            Text("Calcular la próxima dosis desde la aplicación:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("En 1 mes" to 1L, "En 3 meses" to 3L, "En 6 meses" to 6L).forEach { (label, months) ->
                    AssistChip(onClick = { applied?.let { next = Dates.addMonths(it, months); error = null } }, label = { Text(label) })
                }
            }
            OutlinedTextField(
                notes, { notes = it },
                label = { Text("Notas") },
                minLines = 2,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            MensajeError(error)
            BotonPrincipal("Guardar", onClick = { submit() }, enabled = loaded && product.isNotBlank())
        }
    }
}
