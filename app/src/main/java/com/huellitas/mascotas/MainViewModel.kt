package com.huellitas.mascotas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.huellitas.mascotas.data.Pet
import com.huellitas.mascotas.data.Passwords
import com.huellitas.mascotas.data.Settings
import com.huellitas.mascotas.data.ThemeMode
import com.huellitas.mascotas.data.User
import com.huellitas.mascotas.data.Validators
import com.huellitas.mascotas.data.Vaccine
import com.huellitas.mascotas.data.VaccineWithPet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface Session {
    data object Loading : Session
    data object LoggedOut : Session
    data class LoggedIn(val user: User) : Session
}

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val db = (app as HuellitasApplication).db
    private val prefs = (app as HuellitasApplication).prefs

    val session: StateFlow<Session> = prefs.userId
        .map { id ->
            if (id == null) Session.LoggedOut
            else db.users().byId(id)?.let { Session.LoggedIn(it) } ?: Session.LoggedOut
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Session.Loading)

    val settings: StateFlow<Settings> = prefs.settings
        .stateIn(viewModelScope, SharingStarted.Eagerly, Settings())

    private val ownerId: Flow<Long?> = session.map { (it as? Session.LoggedIn)?.user?.id }

    val pets: StateFlow<List<Pet>> = ownerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else db.pets().observeAll(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val upcoming: StateFlow<List<VaccineWithPet>> = ownerId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else db.vaccines().observeUpcoming(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun pet(id: Long): Flow<Pet?> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(null) else db.pets().observe(id, owner) }

    fun vaccines(petId: Long): Flow<List<Vaccine>> =
        ownerId.flatMapLatest { owner -> if (owner == null) flowOf(emptyList()) else db.vaccines().observeForPet(petId, owner) }

    private fun uid(): Long? = (session.value as? Session.LoggedIn)?.user?.id

    // ---- Cuenta ----

    /** Devuelve el mensaje de error, o null si salió bien. */
    suspend fun register(name: String, email: String, password: String): String? {
        val cleanName = name.trim()
        val cleanEmail = email.trim().lowercase()
        if (cleanName.length < 2) return "Escribe tu nombre."
        if (!Validators.email(cleanEmail)) return "Ese correo no parece válido."
        Validators.password(password)?.let { return it }
        if (db.users().byEmail(cleanEmail) != null) return "Ya hay una cuenta con ese correo."
        val salt = Passwords.newSalt()
        val hash = withContext(Dispatchers.Default) { Passwords.hash(password, salt) }
        val id = db.users().insert(User(name = cleanName, email = cleanEmail, salt = salt, hash = hash))
        prefs.setUser(id)
        return null
    }

    suspend fun login(email: String, password: String): String? {
        val wrong = "Correo o contraseña incorrectos."
        val user = db.users().byEmail(email.trim().lowercase()) ?: return wrong
        val ok = withContext(Dispatchers.Default) { Passwords.matches(password, user.salt, user.hash) }
        if (!ok) return wrong
        prefs.setUser(user.id)
        return null
    }

    fun logout() {
        viewModelScope.launch { prefs.setUser(null) }
    }

    fun deleteAccount() {
        val id = uid() ?: return
        viewModelScope.launch {
            prefs.setUser(null)
            db.users().delete(id) // sus mascotas y vacunas se borran en cascada
        }
    }

    // ---- Mascotas ----

    suspend fun getPet(id: Long): Pet? = uid()?.let { db.pets().get(id, it) }

    /** Devuelve el id de la mascota guardada, o null si no se pudo. */
    suspend fun savePet(pet: Pet): Long? {
        val owner = uid() ?: return null
        if (pet.id != 0L && db.pets().get(pet.id, owner) == null) return null
        val newId = db.pets().upsert(pet.copy(ownerId = owner))
        return if (pet.id == 0L) newId else pet.id
    }

    fun deletePet(id: Long) {
        val owner = uid() ?: return
        viewModelScope.launch { db.pets().delete(id, owner) }
    }

    // ---- Vacunas ----

    suspend fun getVaccine(id: Long): Vaccine? = uid()?.let { db.vaccines().get(id, it) }

    suspend fun saveVaccine(vaccine: Vaccine): Boolean {
        val owner = uid() ?: return false
        if (db.pets().get(vaccine.petId, owner) == null) return false
        if (vaccine.id != 0L) {
            if (db.vaccines().get(vaccine.id, owner) == null) return false
        } else {
            db.vaccines().clearNextDue(vaccine.petId, vaccine.name)
        }
        db.vaccines().upsert(vaccine)
        return true
    }

    fun deleteVaccine(id: Long) {
        val owner = uid() ?: return
        viewModelScope.launch { db.vaccines().delete(id, owner) }
    }

    // ---- Ajustes ----

    fun setTheme(mode: ThemeMode) {
        viewModelScope.launch { prefs.setTheme(mode) }
    }

    fun setWarnDays(days: Int) {
        viewModelScope.launch { prefs.setWarnDays(days) }
    }
}
