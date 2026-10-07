package com.huellitas.mascotas.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Las fechas se guardan como días desde 1970 (LocalDate.toEpochDay): sin horas ni zonas horarias.

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

/** Una vacuna junto con los datos de la mascota a la que pertenece (para la pestaña Vacunas). */
data class VaccineWithPet(
    @Embedded val vaccine: Vaccine,
    val petName: String,
    val petSpecies: String,
)
