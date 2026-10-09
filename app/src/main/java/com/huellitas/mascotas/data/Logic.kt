package com.huellitas.mascotas.data

import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/** La contraseña nunca se guarda: solo su hash PBKDF2 con una sal propia de cada cuenta. */
object Passwords {
    private const val ITERATIONS = 120_000

    fun newSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hash(password: String, salt: String): String {
        val spec = PBEKeySpec(password.toCharArray(), Base64.getDecoder().decode(salt), ITERATIONS, 256)
        val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun matches(password: String, salt: String, expectedHash: String): Boolean =
        MessageDigest.isEqual(hash(password, salt).toByteArray(), expectedHash.toByteArray())
}

object Validators {
    private val emailRegex = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    fun email(value: String): Boolean = emailRegex.matches(value.trim())

    /** Devuelve el motivo por el que la contraseña no sirve, o null si está bien. */
    fun password(value: String): String? = when {
        value.length < 8 -> "La contraseña debe tener al menos 8 caracteres."
        value.all { it.isDigit() } -> "Agrega alguna letra a la contraseña."
        else -> null
    }
}

enum class VaccineStatus(val label: String) {
    OVERDUE("Vencida"),
    SOON("Pronto"),
    OK("Al día"),
    NONE("Sin próxima dosis"),
}

/** [warnDays]: cuántos días antes de la fecha se marca como "Pronto". */
fun vaccineStatus(nextDueDay: Long?, today: Long, warnDays: Int): VaccineStatus = when {
    nextDueDay == null -> VaccineStatus.NONE
    nextDueDay < today -> VaccineStatus.OVERDUE
    nextDueDay - today <= warnDays -> VaccineStatus.SOON
    else -> VaccineStatus.OK
}

object Dates {
    private val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-CO"))

    fun today(): Long = LocalDate.now().toEpochDay()

    fun format(day: Long): String = LocalDate.ofEpochDay(day).format(formatter)

    fun addMonths(day: Long, months: Long): Long = LocalDate.ofEpochDay(day).plusMonths(months).toEpochDay()

    fun age(birthDay: Long, today: Long): String {
        val p = Period.between(LocalDate.ofEpochDay(birthDay), LocalDate.ofEpochDay(today))
        return when {
            p.isNegative -> "Aún no nace"
            p.years >= 1 -> if (p.years == 1) "1 año" else "${p.years} años"
            p.months >= 1 -> if (p.months == 1) "1 mes" else "${p.months} meses"
            else -> "Menos de 1 mes"
        }
    }
}

/** Pesos colombianos con punto de miles: 48000 se escribe "$ 48.000". */
fun pesos(valor: Long): String {
    val miles = kotlin.math.abs(valor).toString().reversed().chunked(3).joinToString(".").reversed()
    return (if (valor < 0) "- $ " else "$ ") + miles
}

/** Una vacuna o una desparasitación que tiene fecha de próxima dosis (pestaña Pendientes). */
data class Pendiente(
    /** "Vacunación" o "Desparasitación": el mismo texto que el motivo de la cita que la resuelve. */
    val tipo: String,
    val id: Long,
    val petId: Long,
    val petName: String,
    val petSpecies: String,
    val nombre: String,
    val dueDay: Long,
) {
    /** Las vacunas y las desparasitaciones tienen ids aparte: juntas necesitan una clave propia. */
    val clave: String get() = "$tipo-$id"
}

/** Junta vacunas y desparasitaciones en una sola lista, de la fecha más cercana a la más lejana. */
fun unirPendientes(vacunas: List<VaccineWithPet>, desparasitaciones: List<DewormingWithPet>): List<Pendiente> {
    val v = vacunas.mapNotNull { r ->
        r.vaccine.nextDueDay?.let { Pendiente("Vacunación", r.vaccine.id, r.vaccine.petId, r.petName, r.petSpecies, r.vaccine.name, it) }
    }
    val d = desparasitaciones.mapNotNull { r ->
        r.deworming.nextDueDay?.let { Pendiente("Desparasitación", r.deworming.id, r.deworming.petId, r.petName, r.petSpecies, r.deworming.product, it) }
    }
    return (v + d).sortedWith(compareBy({ it.dueDay }, { it.petName }, { it.nombre }))
}

val SPECIES = listOf("Perro", "Gato", "Ave", "Conejo", "Pez", "Reptil", "Otro")

fun speciesEmoji(species: String): String = when (species) {
    "Perro" -> "🐶"
    "Gato" -> "🐱"
    "Ave" -> "🐦"
    "Conejo" -> "🐰"
    "Pez" -> "🐟"
    "Reptil" -> "🦎"
    else -> "🐾"
}
