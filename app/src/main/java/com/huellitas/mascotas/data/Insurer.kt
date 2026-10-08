package com.huellitas.mascotas.data

import java.time.DayOfWeek
import java.time.LocalDate

// La "aseguradora" de la demo. NO existe ninguna aseguradora real detrás: todo lo que sigue son
// datos inventados que viven dentro de la app y funcionan sin internet.

const val ASEGURADORA = "Huellitas Seguro"

val MOTIVOS = listOf("Vacunación", "Desparasitación", "Consulta general", "Especialista", "Urgencia")

val ESPECIALIDADES = listOf(
    "Medicina general", "Dermatología", "Cardiología", "Oftalmología",
    "Ortopedia", "Odontología", "Comportamiento", "Nutrición",
)

// En las rutas de navegación el motivo viaja como una palabra sin tildes ni espacios; "-" = ninguno.
fun motivoDeRuta(token: String): String? = when (token) {
    "vacunacion" -> "Vacunación"
    "desparasitacion" -> "Desparasitación"
    "consulta" -> "Consulta general"
    "especialista" -> "Especialista"
    "urgencia" -> "Urgencia"
    else -> null
}

fun rutaDeMotivo(motivo: String): String = when (motivo) {
    "Vacunación" -> "vacunacion"
    "Desparasitación" -> "desparasitacion"
    "Consulta general" -> "consulta"
    "Especialista" -> "especialista"
    "Urgencia" -> "urgencia"
    else -> "-"
}

// ---------------------------------------------------------------- Planes

data class Plan(
    val id: String,
    val nombre: String,
    val precioMesCop: Long,
    /** Qué porcentaje de cada gasto paga el seguro. */
    val coberturaPct: Int,
    val limiteAnualCop: Long,
    val deducibleCop: Long,
    val incluye: List<String>,
)

val PLANES = listOf(
    Plan("basico", "Básico", 39_900, 50, 2_000_000, 100_000, listOf("Consultas generales", "Vacunas anuales", "Urgencias con copago")),
    Plan("plus", "Plus", 69_900, 70, 5_000_000, 50_000, listOf("Todo lo del Básico", "Especialistas", "Desparasitaciones", "Medicamentos con descuento")),
    Plan("premium", "Premium", 109_900, 90, 10_000_000, 0, listOf("Todo lo del Plus", "Hospitalización", "Odontología", "Urgencias 24 h sin deducible")),
)

fun planPorId(id: String): Plan? = PLANES.firstOrNull { it.id == id }

// ---------------------------------------------------------------- Clínicas aliadas

data class Clinica(
    val id: String,
    val nombre: String,
    val ciudad: String,
    val direccion: String,
    /** Números inventados (empiezan por 000): no llaman a nadie real. */
    val telefono: String,
    val abierta24h: Boolean,
    val especialidades: List<String>,
    val distanciaKm: Double,
)

val CLINICAS = listOf(
    Clinica("centro", "Clínica Huellitas Centro", "Medellín", "Calle 10 # 20-30 (dirección de ejemplo)", "604 000 0001", true, ESPECIALIDADES, 1.2),
    Clinica("norte", "Veterinaria Patitas Norte", "Medellín", "Carrera 50 # 80-12 (dirección de ejemplo)", "604 000 0002", false, listOf("Medicina general", "Dermatología", "Odontología"), 3.4),
    Clinica("hospital24", "Hospital Veterinario 24 Horas", "Medellín", "Avenida 80 # 33-45 (dirección de ejemplo)", "604 000 0003", true, listOf("Medicina general", "Cardiología", "Ortopedia", "Oftalmología"), 5.1),
    Clinica("comportamiento", "Centro de Comportamiento Animal", "Envigado", "Calle 37 Sur # 25-10 (dirección de ejemplo)", "604 000 0004", false, listOf("Comportamiento", "Nutrición", "Medicina general"), 6.8),
    Clinica("sur", "Clínica Mascotas del Sur", "Sabaneta", "Carrera 45 # 70 Sur-20 (dirección de ejemplo)", "604 000 0005", true, listOf("Medicina general", "Ortopedia"), 8.0),
)

fun clinicaPorId(id: String): Clinica? = CLINICAS.firstOrNull { it.id == id }

/** Las clínicas que atienden una especialidad (para el paso "elige la clínica" al pedir una cita). */
fun clinicasPara(especialidad: String): List<Clinica> = CLINICAS.filter { especialidad in it.especialidades }

/**
 * Horas libres de una clínica en un día: de 08:00 a 17:30 cada media hora. Algunas salen "ocupadas",
 * pero siempre las mismas para la misma clínica y el mismo día. Los domingos solo atienden las de 24 h.
 */
fun horariosDisponibles(clinicaId: String, day: Long): List<String> {
    val clinica = clinicaPorId(clinicaId) ?: return emptyList()
    if (LocalDate.ofEpochDay(day).dayOfWeek == DayOfWeek.SUNDAY && !clinica.abierta24h) return emptyList()
    val todos = (8..17).flatMap { h -> listOf("%02d:00".format(h), "%02d:30".format(h)) }
    return todos.filterIndexed { i, _ -> Math.floorMod(clinicaId.hashCode().toLong() + day + i, 4L) != 0L }
}
