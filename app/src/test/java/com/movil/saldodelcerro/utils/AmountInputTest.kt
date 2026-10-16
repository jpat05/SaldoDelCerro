package com.movil.saldodelcerro.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class AmountInputTest {

    @Test
    fun `enteros pasan sin cambios`() {
        assertEquals("250", "250".filterAmountInput())
    }

    @Test
    fun `acepta decimales con punto`() {
        assertEquals("12.50", "12.50".filterAmountInput())
    }

    @Test
    fun `acepta coma y la normaliza a punto`() {
        assertEquals("12.50", "12,50".filterAmountInput())
    }

    @Test
    fun `mantiene el punto mientras se escriben los decimales`() {
        assertEquals("12.", "12.".filterAmountInput())
    }

    @Test
    fun `un monto sin parte entera se conserva`() {
        assertEquals(".5", ".5".filterAmountInput())
    }

    @Test
    fun `deja un solo separador decimal`() {
        assertEquals("1.23", "1.2.3".filterAmountInput())
    }

    @Test
    fun `recorta a dos decimales`() {
        assertEquals("1.23", "1.234".filterAmountInput())
    }

    @Test
    fun `descarta letras y signos`() {
        assertEquals("100", "1a0b0-".filterAmountInput())
    }
}
