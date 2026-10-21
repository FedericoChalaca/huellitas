package com.huellitas.mascotas

import androidx.sqlite.db.SupportSQLiteDatabase
import com.huellitas.mascotas.data.MIGRATION_1_2
import java.lang.reflect.Proxy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DatabaseMigrationTest {
    @Test
    fun migrationVersion1To2AddsTablesWithoutDroppingExistingData() {
        val statements = mutableListOf<String>()
        val database = Proxy.newProxyInstance(
            SupportSQLiteDatabase::class.java.classLoader,
            arrayOf(SupportSQLiteDatabase::class.java),
        ) { _, method, args ->
            if (method.name == "execSQL") statements += args?.get(0) as String
            null
        } as SupportSQLiteDatabase

        MIGRATION_1_2.migrate(database)

        assertEquals(1, MIGRATION_1_2.startVersion)
        assertEquals(2, MIGRATION_1_2.endVersion)
        assertEquals(10, statements.size)
        assertTrue(statements.any { it.contains("CREATE TABLE IF NOT EXISTS `dewormings`") })
        assertTrue(statements.any { it.contains("CREATE TABLE IF NOT EXISTS `appointments`") })
        assertTrue(statements.any { it.contains("CREATE TABLE IF NOT EXISTS `policies`") })
        assertTrue(statements.any { it.contains("CREATE TABLE IF NOT EXISTS `quotes`") })
        assertTrue(statements.any { it.contains("CREATE TABLE IF NOT EXISTS `quote_items`") })
        assertTrue(statements.none { it.contains("DROP TABLE", ignoreCase = true) })
    }
}
