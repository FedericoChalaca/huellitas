package com.huellitas.mascotas.ui.urgencias

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.huellitas.mascotas.ui.BackScaffold
import com.huellitas.mascotas.ui.NotaDemo
import com.huellitas.mascotas.ui.SpeciesBadge
import com.huellitas.mascotas.ui.Tarjeta
import com.huellitas.mascotas.ui.aparecer

private data class Auxilio(val emoji: String, val titulo: String, val pasos: List<String>)

// Consejos generales y prudentes: ninguno da dosis ni nombra medicamentos.
private val AUXILIOS = listOf(
    Auxilio("😮", "Atragantamiento", listOf("Mantén la calma y mira dentro de la boca sin meter los dedos hasta el fondo.", "Si ves el objeto y sale fácil, sácalo con cuidado.", "Si no respira o no sale, ve ya a una clínica de urgencias.")),
    Auxilio("🥵", "Golpe de calor", listOf("Llévalo a un lugar fresco y con sombra.", "Mójale el cuerpo con agua fresca (no helada), sobre todo patas y barriga.", "Ofrécele agua en pocas cantidades.", "Llama a una clínica de urgencias aunque parezca recuperarse.")),
    Auxilio("☠️", "Envenenamiento o intoxicación", listOf("Aparta a la mascota de lo que haya comido o lamido y guarda el empaque.", "No le provoques el vómito a menos que un veterinario te lo diga.", "Llama a una clínica de urgencias y cuéntales qué fue y cuánto.")),
    Auxilio("🩸", "Herida con sangrado", listOf("Presiona la herida con una gasa o un paño limpio durante varios minutos.", "No quites el paño si se empapa: pon otro encima y sigue presionando.", "Ve a una clínica de urgencias si el sangrado no para o la herida es profunda.")),
    Auxilio("⚡", "Convulsión", listOf("Quita los objetos con los que pueda golpearse y no lo sujetes.", "No le metas la mano en la boca.", "Anota cuánto dura y llama a una clínica de urgencias.")),
    Auxilio("🐝", "Picadura o mordedura", listOf("Si ves el aguijón, retíralo raspando con una tarjeta.", "Aplica una compresa fría sobre la zona.", "Si se hincha la cara, le cuesta respirar o está muy decaída, ve ya a urgencias.")),
)

/** Primeros auxilios: 6 situaciones con consejos cortos en tarjetas. */
@Composable
fun PrimerosAuxiliosScreen(nav: NavController) {
    BackScaffold("Primeros auxilios", onBack = { nav.popBackStack() }) { padding ->
        LazyColumn(
            Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(key = "aviso") {
                NotaDemo("Esta información es general y no reemplaza a un veterinario. Demostración académica.", Modifier.aparecer())
            }
            itemsIndexed(AUXILIOS, key = { _, a -> a.titulo }) { i, a ->
                Tarjeta(Modifier.fillMaxWidth().aparecer(i + 1)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SpeciesBadge(a.emoji, size = 44)
                            Text(a.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        a.pasos.forEachIndexed { n, paso -> Paso(n + 1, paso) }
                    }
                }
            }
        }
    }
}

/** Un paso numerado: el número en un circulito verde y el texto al lado. */
@Composable
private fun Paso(numero: Int, texto: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("$numero", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }
        Text(texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}
