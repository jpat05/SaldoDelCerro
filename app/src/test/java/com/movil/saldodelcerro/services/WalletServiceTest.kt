package com.movil.saldodelcerro.services

import org.junit.Assert.assertEquals
import org.junit.Test

class WalletServiceTest {

    @Test
    fun capitalBaseTieneNombreCorrecto() {

        // Arrange
        val nombreEsperado = "Capital base"

        // Act
        val nombreObtenido = WalletService.CAPITAL_TYPE_NAME

        // Assert
        assertEquals(nombreEsperado, nombreObtenido)
    }
}