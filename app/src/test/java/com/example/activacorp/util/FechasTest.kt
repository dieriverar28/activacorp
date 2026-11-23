package com.example.activacorp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class FechasTest {

    @Test
    fun hoyTexto_retornaFechaActualEnFormatoIso() {
        // ACT
        val resultado = hoyTexto()

        // ASSERT
        assertEquals(LocalDate.now().toString(), resultado)
        assertTrue(Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(resultado))
    }

    @Test
    fun ahoraTexto_retornaFechaYHoraEnFormatoEsperado() {
        // ACT
        val resultado = ahoraTexto()

        // ASSERT
        assertTrue(Regex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$").matches(resultado))
    }
}