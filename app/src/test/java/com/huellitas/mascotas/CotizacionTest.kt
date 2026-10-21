package com.huellitas.mascotas

import com.huellitas.mascotas.data.CATEGORIAS_MEDICAMENTO
import com.huellitas.mascotas.data.LineaCotizacion
import com.huellitas.mascotas.data.MEDICAMENTOS
import com.huellitas.mascotas.data.Medicamento
import com.huellitas.mascotas.data.calcularTotales
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CotizacionTest {
    private fun med(precio: Long) = Medicamento("m$precio", "M", "Vitaminas", "Unidad", precio)

    @Test
    fun elCatalogoTieneVeinteMedicamentosBienEscritos() {
        assertEquals(20, MEDICAMENTOS.size)
        assertEquals(MEDICAMENTOS.size, MEDICAMENTOS.map { it.id }.toSet().size)
        CATEGORIAS_MEDICAMENTO.forEach { c -> assertEquals(c, 4, MEDICAMENTOS.count { it.categoria == c }) }
        assertTrue(MEDICAMENTOS.all { it.categoria in CATEGORIAS_MEDICAMENTO && it.precioCop in 15_000..180_000 })
    }

    @Test
    fun carritoVacioDaCero() {
        val t = calcularTotales(emptyList(), 70)
        assertEquals(0L, t.subtotalCop)
        assertEquals(0L, t.coberturaCop)
        assertEquals(0L, t.copagoCop)
    }

    @Test
    fun sinPolizaSePagaTodo() {
        val t = calcularTotales(listOf(LineaCotizacion(med(48_000), 1)), 0)
        assertEquals(48_000L, t.copagoCop)
        assertEquals(0L, t.coberturaCop)
    }

    @Test
    fun conPlanPlusPagaElSetentaPorCiento() {
        val lineas = listOf(LineaCotizacion(med(48_000), 1), LineaCotizacion(med(36_000), 1), LineaCotizacion(med(28_000), 1))
        val t = calcularTotales(lineas, 70)
        assertEquals(112_000L, t.subtotalCop)
        assertEquals(78_400L, t.coberturaCop)
        assertEquals(33_600L, t.copagoCop)
    }

    @Test
    fun lasCantidadesMultiplican() {
        assertEquals(72_000L, calcularTotales(listOf(LineaCotizacion(med(36_000), 2)), 0).subtotalCop)
    }

    @Test
    fun conCoberturaTotalNoSePagaNada() {
        assertEquals(0L, calcularTotales(listOf(LineaCotizacion(med(36_000), 1)), 100).copagoCop)
    }

    @Test
    fun porcentajesFueraDeRangoSeCorrigen() {
        val lineas = listOf(LineaCotizacion(med(10_000), 1))
        assertEquals(0L, calcularTotales(lineas, 150).copagoCop)
        assertEquals(10_000L, calcularTotales(lineas, -10).copagoCop)
    }

    @Test
    fun laCoberturaSeRedondeaHaciaAbajoYTodoSuma() {
        val t = calcularTotales(listOf(LineaCotizacion(med(33_333), 1)), 70)
        assertEquals(23_333L, t.coberturaCop)
        assertEquals(t.subtotalCop, t.coberturaCop + t.copagoCop)
    }
}
