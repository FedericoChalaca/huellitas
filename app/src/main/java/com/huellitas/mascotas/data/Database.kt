package com.huellitas.mascotas.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun byEmail(email: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun byId(id: Long): User?

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun delete(id: Long)
}

// Todas las consultas llevan el dueño: una cuenta nunca ve ni toca lo de otra.
@Dao
interface PetDao {
    @Query("SELECT * FROM pets WHERE ownerId = :owner ORDER BY name COLLATE NOCASE")
    fun observeAll(owner: Long): Flow<List<Pet>>

    @Query("SELECT * FROM pets WHERE id = :id AND ownerId = :owner")
    fun observe(id: Long, owner: Long): Flow<Pet?>

    @Query("SELECT * FROM pets WHERE id = :id AND ownerId = :owner")
    suspend fun get(id: Long, owner: Long): Pet?

    @Upsert
    suspend fun upsert(pet: Pet): Long

    @Query("DELETE FROM pets WHERE id = :id AND ownerId = :owner")
    suspend fun delete(id: Long, owner: Long)
}

@Dao
interface VaccineDao {
    @Query(
        "SELECT v.* FROM vaccines v JOIN pets p ON p.id = v.petId " +
            "WHERE v.petId = :petId AND p.ownerId = :owner ORDER BY v.appliedDay DESC",
    )
    fun observeForPet(petId: Long, owner: Long): Flow<List<Vaccine>>

    @Query(
        "SELECT v.*, p.name AS petName, p.species AS petSpecies FROM vaccines v JOIN pets p ON p.id = v.petId " +
            "WHERE p.ownerId = :owner AND v.nextDueDay IS NOT NULL ORDER BY v.nextDueDay",
    )
    fun observeUpcoming(owner: Long): Flow<List<VaccineWithPet>>

    @Query(
        "SELECT v.* FROM vaccines v JOIN pets p ON p.id = v.petId WHERE v.id = :id AND p.ownerId = :owner",
    )
    suspend fun get(id: Long, owner: Long): Vaccine?

    @Upsert
    suspend fun upsert(vaccine: Vaccine): Long

    // Al registrar una dosis nueva de la misma vacuna, la fecha "próxima" de las anteriores ya no cuenta.
    @Query("UPDATE vaccines SET nextDueDay = NULL WHERE petId = :petId AND name = :name COLLATE NOCASE")
    suspend fun clearNextDue(petId: Long, name: String)

    @Query("DELETE FROM vaccines WHERE id = :id AND petId IN (SELECT id FROM pets WHERE ownerId = :owner)")
    suspend fun delete(id: Long, owner: Long)
}

@Database(entities = [User::class, Pet::class, Vaccine::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun users(): UserDao
    abstract fun pets(): PetDao
    abstract fun vaccines(): VaccineDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "huellitas.db").build()
    }
}
