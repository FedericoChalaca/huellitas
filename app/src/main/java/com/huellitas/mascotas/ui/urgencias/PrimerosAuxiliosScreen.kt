package com.huellitas.mascotas.ui.urgencias

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
import com.huellitas.mascotas.ui.BackScaffold

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
            items(AUXILIOS, key = { it.titulo }) { a ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("${a.emoji} ${a.titulo}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        a.pasos.forEachIndexed { i, paso -> Text("${i + 1}. $paso", style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }
            item {
                Text(
                    "Esta información es general y no reemplaza a un veterinario. Demostración académica.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
