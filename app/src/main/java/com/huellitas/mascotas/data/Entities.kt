package com.huellitas.mascotas.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Las fechas se guardan como días desde 1970 (LocalDate.toEpochDay): sin horas ni zonas horarias.
// El dinero se guarda en pesos colombianos enteros (COP), sin decimales.

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val salt: String,
    val hash: String,
)

@Entity(
    tableName = "pets",
    foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["ownerId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("ownerId")],
)
data class Pet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    val name: String,
    val species: String,
    val breed: String = "",
    val birthDay: Long? = null,
    val notes: String = "",
)

@Entity(
    tableName = "vaccines",
    foreignKeys = [ForeignKey(entity = Pet::class, parentColumns = ["id"], childColumns = ["petId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("petId")],
)
data class Vaccine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    val name: String,
    val appliedDay: Long,
    val nextDueDay: Long? = null,
    val notes: String = "",
)

/** Una vacuna junto con los datos de la mascota a la que pertenece (para la pestaña Pendientes). */
data class VaccineWithPet(
    @Embedded val vaccine: Vaccine,
    val petName: String,
    val petSpecies: String,
)

// ---------------------------------------------------------------- Desparasitaciones

@Entity(
    tableName = "dewormings",
    foreignKeys = [ForeignKey(entity = Pet::class, parentColumns = ["id"], childColumns = ["petId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("petId")],
)
data class Deworming(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    val product: String,
    /** "Interna" o "Externa" (ver [DEWORMING_KINDS]). */
    val kind: String,
    val appliedDay: Long,
    val nextDueDay: Long? = null,
    val notes: String = "",
)

data class DewormingWithPet(
    @Embedded val deworming: Deworming,
    val petName: String,
    val petSpecies: String,
)

val DEWORMING_KINDS = listOf("Interna", "Externa")

// ---------------------------------------------------------------- Citas

object AppointmentStatus {
    const val REQUESTED = "Solicitada"
    const val CONFIRMED = "Confirmada"
    const val CANCELLED = "Cancelada"
    const val DONE = "Completada"
}

@Entity(
    tableName = "appointments",
    foreignKeys = [ForeignKey(entity = Pet::class, parentColumns = ["id"], childColumns = ["petId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("petId")],
)
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    /** Uno de [MOTIVOS] (por ejemplo "Vacunación"). */
    val reason: String,
    /** Una de [ESPECIALIDADES]. */
    val specialty: String,
    /** Id de una clínica de [CLINICAS]. */
    val clinicId: String,
    val day: Long,
    /** Hora de inicio en formato "09:30". */
    val slot: String,
    val status: String = AppointmentStatus.REQUESTED,
    val urgent: Boolean = false,
    val notes: String = "",
)

data class AppointmentWithPet(
    @Embedded val appointment: Appointment,
    val petName: String,
    val petSpecies: String,
)

// ---------------------------------------------------------------- Seguro (simulado)

/** La póliza de una mascota. Una mascota tiene como máximo una. */
@Entity(
    tableName = "policies",
    foreignKeys = [ForeignKey(entity = Pet::class, parentColumns = ["id"], childColumns = ["petId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["petId"], unique = true)],
)
data class Policy(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val petId: Long,
    /** Id de un plan de [PLANES]. */
    val planId: String,
    val number: String,
    val startDay: Long,
    val endDay: Long,
    /** Cuánto del límite anual ya se usó, en COP. */
    val usedCop: Long = 0,
)

data class PolicyWithPet(
    @Embedded val policy: Policy,
    val petName: String,
    val petSpecies: String,
)

// ---------------------------------------------------------------- Cotización de medicamentos

@Entity(
    tableName = "quotes",
    foreignKeys = [ForeignKey(entity = User::class, parentColumns = ["id"], childColumns = ["ownerId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("ownerId")],
)
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ownerId: Long,
    /** Mascota para la que se cotizó (para aplicar su póliza), o null. */
    val petId: Long? = null,
    val createdDay: Long,
    val totalCop: Long,
    /** Cuánto paga el seguro de ese total. */
    val coveredCop: Long,
    val status: String = "Solicitada",
)

@Entity(
    tableName = "quote_items",
    foreignKeys = [ForeignKey(entity = Quote::class, parentColumns = ["id"], childColumns = ["quoteId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("quoteId")],
)
data class QuoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quoteId: Long,
    val medicineId: String,
    val name: String,
    val unitPriceCop: Long,
    val quantity: Int,
)
