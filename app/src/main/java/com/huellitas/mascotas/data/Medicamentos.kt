package com.huellitas.mascotas.data

// Catálogo de medicamentos que se pueden cotizar con la aseguradora de la demo (precios inventados).
// Este archivo es de Nicolás: tarjetas N-08 (catálogo) y N-16 (cálculo de totales).
// Para escribir precios usa pesos(...), que ya existe en Logic.kt: pesos(48_000) da "$ 48.000".

data class Medicamento(
    val id: String,
    val nombre: String,
    val categoria: String,
    val presentacion: String,
    val precioCop: Long,
)

val CATEGORIAS_MEDICAMENTO = listOf("Antiparasitarios", "Antibióticos", "Antiinflamatorios", "Vitaminas", "Piel y oído")

// TODO(N-08): agrega aquí 20 medicamentos (4 por categoría) con precios entre 15.000 y 180.000 COP.
val MEDICAMENTOS: List<Medicamento> = emptyList()

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

// TODO(N-16): calcula los totales. coberturaPct es el porcentaje del plan (0 si la mascota no tiene póliza).
fun calcularTotales(lineas: List<LineaCotizacion>, coberturaPct: Int): Totales = TODO("N-16")
