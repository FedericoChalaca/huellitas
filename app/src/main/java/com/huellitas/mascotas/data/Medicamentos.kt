package com.huellitas.mascotas.data

// Catálogo de medicamentos que se pueden cotizar con la aseguradora de la demo (precios inventados).
// Para escribir precios usa pesos(...), que está en Logic.kt: pesos(48_000) da "$ 48.000".

data class Medicamento(
    val id: String,
    val nombre: String,
    val categoria: String,
    val presentacion: String,
    val precioCop: Long,
)

val CATEGORIAS_MEDICAMENTO = listOf("Antiparasitarios", "Antibióticos", "Antiinflamatorios", "Vitaminas", "Piel y oído")

// Nombres genéricos y precios inventados: la app no vende nada.
val MEDICAMENTOS: List<Medicamento> = listOf(
    Medicamento("antiparasitario-oral", "Antiparasitario oral", "Antiparasitarios", "Caja x 3 tabletas", 48_000),
    Medicamento("pipeta-antipulgas", "Pipeta antipulgas", "Antiparasitarios", "Caja x 2 pipetas", 36_000),
    Medicamento("collar-antiparasitario", "Collar antiparasitario", "Antiparasitarios", "Unidad", 72_000),
    Medicamento("desparasitante-liquido", "Desparasitante líquido", "Antiparasitarios", "Frasco 50 ml", 28_000),
    Medicamento("amoxicilina-suspension", "Amoxicilina en suspensión", "Antibióticos", "Frasco 60 ml", 38_000),
    Medicamento("doxiciclina-tabletas", "Doxiciclina en tabletas", "Antibióticos", "Caja x 20 tabletas", 52_000),
    Medicamento("cefalexina-capsulas", "Cefalexina en cápsulas", "Antibióticos", "Caja x 12 cápsulas", 44_000),
    Medicamento("metronidazol-tabletas", "Metronidazol en tabletas", "Antibióticos", "Caja x 20 tabletas", 31_000),
    Medicamento("meloxicam-gotas", "Meloxicam en gotas", "Antiinflamatorios", "Frasco 15 ml", 64_000),
    Medicamento("carprofeno-tabletas", "Carprofeno en tabletas", "Antiinflamatorios", "Caja x 14 tabletas", 86_000),
    Medicamento("prednisolona-tabletas", "Prednisolona en tabletas", "Antiinflamatorios", "Caja x 20 tabletas", 27_000),
    Medicamento("gel-articular", "Gel articular", "Antiinflamatorios", "Tubo 60 g", 58_000),
    Medicamento("multivitaminico-masticable", "Multivitamínico masticable", "Vitaminas", "Frasco x 60", 69_000),
    Medicamento("omega-3-capsulas", "Omega 3 en cápsulas", "Vitaminas", "Frasco x 90", 97_000),
    Medicamento("complejo-b-gotas", "Complejo B en gotas", "Vitaminas", "Frasco 30 ml", 24_000),
    Medicamento("calcio-cachorros", "Calcio para cachorros", "Vitaminas", "Frasco 120 ml", 33_000),
    Medicamento("champu-medicado", "Champú medicado", "Piel y oído", "Frasco 250 ml", 42_000),
    Medicamento("limpiador-de-oidos", "Limpiador de oídos", "Piel y oído", "Frasco 120 ml", 29_000),
    Medicamento("crema-cicatrizante", "Crema cicatrizante", "Piel y oído", "Tubo 30 g", 21_000),
    Medicamento("spray-antiseptico", "Spray antiséptico", "Piel y oído", "Frasco 100 ml", 175_000),
)

/** Un renglón de la cotización: un medicamento y cuántas unidades. */
data class LineaCotizacion(val medicamento: Medicamento, val cantidad: Int)

data class Totales(
    /** Suma de precio × cantidad de todos los renglones. */
    val subtotalCop: Long,
    /** Lo que paga el seguro. */
    val coberturaCop: Long,
    /** Lo que paga la persona: subtotal - cobertura. */
    val copagoCop: Long,
)

/**
 * Los totales de una cotización. [coberturaPct] es el porcentaje del plan (0 si la mascota no tiene
 * póliza); si llega fuera de 0 a 100 se corrige. Todo en pesos enteros: la cobertura se redondea hacia abajo.
 */
fun calcularTotales(lineas: List<LineaCotizacion>, coberturaPct: Int): Totales {
    val subtotal = lineas.sumOf { it.medicamento.precioCop * it.cantidad }
    val cobertura = subtotal * coberturaPct.coerceIn(0, 100) / 100
    return Totales(subtotal, cobertura, subtotal - cobertura)
}
