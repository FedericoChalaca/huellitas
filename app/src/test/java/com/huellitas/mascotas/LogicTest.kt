package com.huellitas.mascotas

import com.huellitas.mascotas.data.Dates
import com.huellitas.mascotas.data.Deworming
import com.huellitas.mascotas.data.DewormingWithPet
import com.huellitas.mascotas.data.Passwords
import com.huellitas.mascotas.data.Validators
import com.huellitas.mascotas.data.Vaccine
import com.huellitas.mascotas.data.VaccineStatus
import com.huellitas.mascotas.data.VaccineWithPet
import com.huellitas.mascotas.data.pesos
import com.huellitas.mascotas.data.unirPendientes
import com.huellitas.mascotas.data.vaccineStatus
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicTest {
    @Test
    fun laContrasenaSeVerificaSinGuardarseEnClaro() {
        val salt = Passwords.newSalt()
        val hash = Passwords.hash("clave-segura-1", salt)
        assertTrue(Passwords.matches("clave-segura-1", salt, hash))
        assertFalse(Passwords.matches("otra-clave-2", salt, hash))
        assertNotEquals("clave-segura-1", hash)
        // Con otra sal, la misma contraseña da otro hash.
        assertNotEquals(hash, Passwords.hash("clave-segura-1", Passwords.newSalt()))
    }

    @Test
    fun validaCorreoYContrasena() {
        assertTrue(Validators.email("ana@correo.com"))
        assertTrue(Validators.email("  ana@correo.com "))
        assertFalse(Validators.email("ana@correo"))
        assertFalse(Validators.email("ana correo.com"))
        assertNotNull(Validators.password("corta1"))
        assertNotNull(Validators.password("12345678"))
        assertNull(Validators.password("perro1234"))
    }

    @Test
    fun estadoDeLaVacuna() {
        val hoy = 20_000L
        assertEquals(VaccineStatus.NONE, vaccineStatus(null, hoy, 7))
        assertEquals(VaccineStatus.OVERDUE, vaccineStatus(hoy - 1, hoy, 7))
        assertEquals(VaccineStatus.SOON, vaccineStatus(hoy, hoy, 7))
        assertEquals(VaccineStatus.SOON, vaccineStatus(hoy + 7, hoy, 7))
        assertEquals(VaccineStatus.OK, vaccineStatus(hoy + 8, hoy, 7))
        // Con otro aviso configurado cambia el límite.
        assertEquals(VaccineStatus.OK, vaccineStatus(hoy + 8, hoy, 3))
    }

    @Test
    fun edadDeLaMascota() {
        val hoy = LocalDate.of(2026, 10, 7).toEpochDay()
        assertEquals("2 años", Dates.age(LocalDate.of(2024, 9, 1).toEpochDay(), hoy))
        assertEquals("1 año", Dates.age(LocalDate.of(2025, 10, 7).toEpochDay(), hoy))
        assertEquals("3 meses", Dates.age(LocalDate.of(2026, 7, 1).toEpochDay(), hoy))
        assertEquals("Menos de 1 mes", Dates.age(LocalDate.of(2026, 9, 30).toEpochDay(), hoy))
    }

    @Test
    fun pesosConPuntoDeMiles() {
        assertEquals("$ 0", pesos(0))
        assertEquals("$ 900", pesos(900))
        assertEquals("$ 48.000", pesos(48_000))
        assertEquals("$ 10.000.000", pesos(10_000_000))
        assertEquals("- $ 78.400", pesos(-78_400))
    }

    @Test
    fun pendientesJuntaVacunasYDesparasitacionesPorFecha() {
        val vacunas = listOf(
            VaccineWithPet(Vaccine(id = 1, petId = 10, name = "Rabia", appliedDay = 100, nextDueDay = 500), "Luna", "Gato"),
            VaccineWithPet(Vaccine(id = 2, petId = 11, name = "Triple", appliedDay = 100, nextDueDay = 300), "Rocky", "Perro"),
            // Sin próxima dosis no es un pendiente.
            VaccineWithPet(Vaccine(id = 3, petId = 11, name = "Moquillo", appliedDay = 100), "Rocky", "Perro"),
        )
        val desparasitaciones = listOf(
            DewormingWithPet(Deworming(id = 1, petId = 10, product = "Pipeta", kind = "Externa", appliedDay = 100, nextDueDay = 400), "Luna", "Gato"),
        )
        val todo = unirPendientes(vacunas, desparasitaciones)
        assertEquals(listOf("Triple", "Pipeta", "Rabia"), todo.map { it.nombre })
        assertEquals(listOf("Vacunación", "Desparasitación", "Vacunación"), todo.map { it.tipo })
        // La vacuna 1 y la desparasitación 1 comparten id: la clave las distingue.
        assertEquals(3, todo.map { it.clave }.toSet().size)
        assertTrue(unirPendientes(emptyList(), emptyList()).isEmpty())
    }

    @Test
    fun fechasEnEspanol() {
        assertEquals("7 de octubre de 2026", Dates.format(LocalDate.of(2026, 10, 7).toEpochDay()))
        assertEquals(LocalDate.of(2027, 4, 7).toEpochDay(), Dates.addMonths(LocalDate.of(2026, 10, 7).toEpochDay(), 6))
    }
}
