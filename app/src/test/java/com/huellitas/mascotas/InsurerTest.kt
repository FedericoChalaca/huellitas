package com.huellitas.mascotas

import com.huellitas.mascotas.data.AppointmentStatus
import com.huellitas.mascotas.data.CLINICAS
import com.huellitas.mascotas.data.MEDICINA_GENERAL
import com.huellitas.mascotas.data.PLANES
import com.huellitas.mascotas.data.clinicaDeUrgencias
import com.huellitas.mascotas.data.clinicasPara
import com.huellitas.mascotas.data.errorDeCita
import com.huellitas.mascotas.data.esCitaProxima
import com.huellitas.mascotas.data.horariosDisponibles
import com.huellitas.mascotas.data.motivoDeRuta
import com.huellitas.mascotas.data.numeroDePoliza
import com.huellitas.mascotas.data.planPorId
import com.huellitas.mascotas.data.rutaDeMotivo
import com.huellitas.mascotas.data.usoDeEjemplo
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class InsurerTest {
    private val lunes = LocalDate.of(2026, 10, 12).toEpochDay()
    private val domingo = LocalDate.of(2026, 10, 11).toEpochDay()

    @Test
    fun losHorariosVanDeOchoACincoYMediaYNoCambianSolos() {
        assertEquals(DayOfWeek.MONDAY, LocalDate.ofEpochDay(lunes).dayOfWeek)
        val horas = horariosDisponibles("centro", lunes)
        assertTrue(horas.isNotEmpty())
        assertTrue(horas.all { it >= "08:00" && it <= "17:30" })
        assertTrue(horas.all { it.endsWith(":00") || it.endsWith(":30") })
        assertEquals(horas, horas.sorted())
        // Algunas salen ocupadas, pero siempre las mismas para la misma clínica y el mismo día.
        assertTrue(horas.size < 20)
        assertEquals(horas, horariosDisponibles("centro", lunes))
        assertTrue(horariosDisponibles("no-existe", lunes).isEmpty())
    }

    @Test
    fun elDomingoSoloAtiendenLasDe24Horas() {
        assertEquals(DayOfWeek.SUNDAY, LocalDate.ofEpochDay(domingo).dayOfWeek)
        CLINICAS.forEach { c ->
            assertEquals(c.nombre, c.abierta24h, horariosDisponibles(c.id, domingo).isNotEmpty())
        }
    }

    @Test
    fun lasClinicasSeFiltranPorEspecialidad() {
        assertEquals(CLINICAS.size, clinicasPara(MEDICINA_GENERAL).size)
        assertEquals(listOf("centro", "hospital24"), clinicasPara("Cardiología").map { it.id })
        assertTrue(clinicasPara("No existe").isEmpty())
        // La urgencia va a la clínica 24 horas más cercana.
        assertEquals("centro", clinicaDeUrgencias().id)
    }

    @Test
    fun losPlanesSubenDePrecioYDeCobertura() {
        assertEquals(listOf("basico", "plus", "premium"), PLANES.map { it.id })
        assertEquals(PLANES.map { it.precioMesCop }, PLANES.map { it.precioMesCop }.sorted())
        assertEquals(PLANES.map { it.coberturaPct }, PLANES.map { it.coberturaPct }.sorted())
        assertTrue(PLANES.all { it.coberturaPct in 0..100 && it.limiteAnualCop > 0 && it.deducibleCop >= 0 })
        assertNull(planPorId("otro"))
    }

    @Test
    fun unaCitaSoloSeAceptaSiTodoCuadra() {
        val hora = horariosDisponibles("norte", lunes).first()
        assertNull(errorDeCita("Vacunación", MEDICINA_GENERAL, "norte", lunes, hora, lunes))
        assertNotNull(errorDeCita("Paseo", MEDICINA_GENERAL, "norte", lunes, hora, lunes))
        assertNotNull(errorDeCita("Especialista", "Astrología", "norte", lunes, hora, lunes))
        assertNotNull(errorDeCita("Vacunación", MEDICINA_GENERAL, "no-existe", lunes, hora, lunes))
        // Patitas Norte no tiene cardiología.
        assertNotNull(errorDeCita("Especialista", "Cardiología", "norte", lunes, hora, lunes))
        // Ni días que ya pasaron, ni horas que la clínica no ofrece, ni domingos donde no abren.
        assertNotNull(errorDeCita("Vacunación", MEDICINA_GENERAL, "norte", lunes, hora, lunes + 1))
        assertNotNull(errorDeCita("Vacunación", MEDICINA_GENERAL, "norte", lunes, "23:00", lunes))
        assertNotNull(errorDeCita("Vacunación", MEDICINA_GENERAL, "norte", domingo, hora, domingo))
    }

    @Test
    fun proximasEHistorial() {
        assertTrue(esCitaProxima(lunes, AppointmentStatus.REQUESTED, lunes))
        assertTrue(esCitaProxima(lunes + 3, AppointmentStatus.CONFIRMED, lunes))
        assertFalse(esCitaProxima(lunes - 1, AppointmentStatus.CONFIRMED, lunes))
        assertFalse(esCitaProxima(lunes + 3, AppointmentStatus.CANCELLED, lunes))
        assertFalse(esCitaProxima(lunes + 3, AppointmentStatus.DONE, lunes))
    }

    @Test
    fun elMotivoViajaPorLaRutaYVuelveIgual() {
        listOf("Vacunación", "Desparasitación", "Consulta general", "Especialista", "Urgencia").forEach {
            assertEquals(it, motivoDeRuta(rutaDeMotivo(it)))
        }
        assertNull(motivoDeRuta("-"))
        assertEquals("-", rutaDeMotivo("Otra cosa"))
    }

    @Test
    fun numeroYUsoDeEjemploDeLaPoliza() {
        assertEquals("HS-2026-0007", numeroDePoliza(2026, 7))
        assertEquals("HS-2026-12345", numeroDePoliza(2026, 12345))
        PLANES.forEach { plan ->
            (1L..8L).forEach { petId ->
                val usado = usoDeEjemplo(plan, petId)
                assertTrue(usado > 0 && usado <= plan.limiteAnualCop / 5)
            }
        }
    }
}
