package com.huellitas.mascotas.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
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

@Dao
interface DewormingDao {
    @Query(
        "SELECT d.* FROM dewormings d JOIN pets p ON p.id = d.petId " +
            "WHERE d.petId = :petId AND p.ownerId = :owner ORDER BY d.appliedDay DESC",
    )
    fun observeForPet(petId: Long, owner: Long): Flow<List<Deworming>>

    @Query(
        "SELECT d.*, p.name AS petName, p.species AS petSpecies FROM dewormings d JOIN pets p ON p.id = d.petId " +
            "WHERE p.ownerId = :owner AND d.nextDueDay IS NOT NULL ORDER BY d.nextDueDay",
    )
    fun observeUpcoming(owner: Long): Flow<List<DewormingWithPet>>

    @Query("SELECT d.* FROM dewormings d JOIN pets p ON p.id = d.petId WHERE d.id = :id AND p.ownerId = :owner")
    suspend fun get(id: Long, owner: Long): Deworming?

    @Upsert
    suspend fun upsert(deworming: Deworming): Long

    // Igual que con las vacunas: al registrar una dosis nueva del mismo producto, la fecha "próxima" de las viejas ya no cuenta.
    @Query("UPDATE dewormings SET nextDueDay = NULL WHERE petId = :petId AND product = :product COLLATE NOCASE")
    suspend fun clearNextDue(petId: Long, product: String)

    @Query("DELETE FROM dewormings WHERE id = :id AND petId IN (SELECT id FROM pets WHERE ownerId = :owner)")
    suspend fun delete(id: Long, owner: Long)
}

@Dao
interface AppointmentDao {
    @Query(
        "SELECT a.*, p.name AS petName, p.species AS petSpecies FROM appointments a JOIN pets p ON p.id = a.petId " +
            "WHERE p.ownerId = :owner ORDER BY a.day, a.slot",
    )
    fun observeAll(owner: Long): Flow<List<AppointmentWithPet>>

    @Query(
        "SELECT a.*, p.name AS petName, p.species AS petSpecies FROM appointments a JOIN pets p ON p.id = a.petId " +
            "WHERE a.id = :id AND p.ownerId = :owner",
    )
    fun observe(id: Long, owner: Long): Flow<AppointmentWithPet?>

    @Query("SELECT a.* FROM appointments a JOIN pets p ON p.id = a.petId WHERE a.id = :id AND p.ownerId = :owner")
    suspend fun get(id: Long, owner: Long): Appointment?

    @Upsert
    suspend fun upsert(appointment: Appointment): Long

    @Query("UPDATE appointments SET status = :status WHERE id = :id AND petId IN (SELECT id FROM pets WHERE ownerId = :owner)")
    suspend fun setStatus(id: Long, status: String, owner: Long)

    @Query("DELETE FROM appointments WHERE id = :id AND petId IN (SELECT id FROM pets WHERE ownerId = :owner)")
    suspend fun delete(id: Long, owner: Long)
}

@Dao
interface PolicyDao {
    @Query(
        "SELECT po.*, p.name AS petName, p.species AS petSpecies FROM policies po JOIN pets p ON p.id = po.petId " +
            "WHERE p.ownerId = :owner ORDER BY p.name COLLATE NOCASE",
    )
    fun observeAll(owner: Long): Flow<List<PolicyWithPet>>

    @Query("SELECT po.* FROM policies po JOIN pets p ON p.id = po.petId WHERE po.petId = :petId AND p.ownerId = :owner")
    fun observeForPet(petId: Long, owner: Long): Flow<Policy?>

    @Query("SELECT po.* FROM policies po JOIN pets p ON p.id = po.petId WHERE po.petId = :petId AND p.ownerId = :owner")
    suspend fun forPet(petId: Long, owner: Long): Policy?

    @Upsert
    suspend fun upsert(policy: Policy): Long

    @Query("DELETE FROM policies WHERE petId = :petId AND petId IN (SELECT id FROM pets WHERE ownerId = :owner)")
    suspend fun delete(petId: Long, owner: Long)
}

@Dao
abstract class QuoteDao {
    @Insert
    abstract suspend fun insert(quote: Quote): Long

    @Insert
    abstract suspend fun insertItems(items: List<QuoteItem>)

    @Query("SELECT * FROM quotes WHERE ownerId = :owner ORDER BY createdDay DESC, id DESC")
    abstract fun observeAll(owner: Long): Flow<List<Quote>>

    @Query(
        "SELECT i.* FROM quote_items i JOIN quotes q ON q.id = i.quoteId " +
            "WHERE i.quoteId = :quoteId AND q.ownerId = :owner ORDER BY i.id",
    )
    abstract fun observeItems(quoteId: Long, owner: Long): Flow<List<QuoteItem>>

    @Query("DELETE FROM quotes WHERE id = :id AND ownerId = :owner")
    abstract suspend fun delete(id: Long, owner: Long)

    /** Guarda la cotización y sus renglones juntos: o entra todo o no entra nada. */
    @Transaction
    open suspend fun saveWithItems(quote: Quote, items: List<QuoteItem>): Long {
        val id = insert(quote)
        insertItems(items.map { it.copy(id = 0, quoteId = id) })
        return id
    }
}

@Database(
    entities = [User::class, Pet::class, Vaccine::class, Deworming::class, Appointment::class, Policy::class, Quote::class, QuoteItem::class],
    version = 2,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun users(): UserDao
    abstract fun pets(): PetDao
    abstract fun vaccines(): VaccineDao
    abstract fun dewormings(): DewormingDao
    abstract fun appointments(): AppointmentDao
    abstract fun policies(): PolicyDao
    abstract fun quotes(): QuoteDao

    companion object {
        // Todavía no hay usuarios reales con la versión 1: si cambia el esquema se recrea la base.
        // ponytail: antes de publicar actualizaciones con datos de usuarios hay que escribir migraciones.
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "huellitas.db")
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
