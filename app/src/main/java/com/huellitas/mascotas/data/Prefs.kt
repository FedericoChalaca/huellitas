package com.huellitas.mascotas.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "huellitas")

enum class ThemeMode(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
}

data class Settings(val theme: ThemeMode = ThemeMode.SYSTEM, val warnDays: Int = 7)

/** Sesión y ajustes: lo que debe sobrevivir a cerrar la app. */
class Prefs(private val context: Context) {
    private val userKey = longPreferencesKey("user")
    private val themeKey = stringPreferencesKey("theme")
    private val warnKey = intPreferencesKey("warn_days")

    val userId: Flow<Long?> = context.dataStore.data.map { it[userKey] }

    val settings: Flow<Settings> = context.dataStore.data.map { it.toSettings() }

    private fun Preferences.toSettings() = Settings(
        theme = runCatching { ThemeMode.valueOf(this[themeKey] ?: "SYSTEM") }.getOrDefault(ThemeMode.SYSTEM),
        warnDays = this[warnKey] ?: 7,
    )

    suspend fun setUser(id: Long?) {
        context.dataStore.edit { if (id == null) it.remove(userKey) else it[userKey] = id }
    }

    suspend fun setTheme(mode: ThemeMode) {
        context.dataStore.edit { it[themeKey] = mode.name }
    }

    suspend fun setWarnDays(days: Int) {
        context.dataStore.edit { it[warnKey] = days }
    }
}
