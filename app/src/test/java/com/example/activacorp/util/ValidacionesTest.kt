package com.example.activacorp.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ValidacionesTest {

    // --- validarCampoObligatorio ---
    @Test
    fun validarCampoObligatorio_textoVacioOEspacios_retornaFalse() {
        assertFalse(validarCampoObligatorio(""))
        assertFalse(validarCampoObligatorio("   "))
    }

    @Test
    fun validarCampoObligatorio_textoValido_retornaTrue() {
        assertTrue(validarCampoObligatorio("Activacorp"))
    }

    // --- validarNombre ---
    @Test
    fun validarNombre_menosDeTresCaracteres_retornaFalse() {
        assertFalse(validarNombre("Ab"))
        assertFalse(validarNombre("  A  "))
    }

    @Test
    fun validarNombre_tresOMasCaracteres_retornaTrue() {
        assertTrue(validarNombre("Ana"))
        assertTrue(validarNombre("  Carlos  "))
    }

    // --- validarFecha ---
    @Test
    fun validarFecha_formatoInvalidoOInexistente_retornaFalse() {
        assertFalse(validarFecha("23-11-2026"))
        assertFalse(validarFecha("2026-02-30"))
        assertFalse(validarFecha(""))
    }

    @Test
    fun validarFecha_formatoValido_retornaTrue() {
        assertTrue(validarFecha("2026-11-23"))
    }

    // --- validarFechaEvento ---
    @Test
    fun validarFechaEvento_fechaPasadaOInvalida_retornaFalse() {
        val ayer = LocalDate.now().minusDays(1).toString()
        assertFalse(validarFechaEvento(ayer))
        assertFalse(validarFechaEvento("fecha-mala"))
    }

    @Test
    fun validarFechaEvento_hoyOFutura_retornaTrue() {
        val hoy = LocalDate.now().toString()
        val manana = LocalDate.now().plusDays(1).toString()
        assertTrue(validarFechaEvento(hoy))
        assertTrue(validarFechaEvento(manana))
    }

    // --- validarPublicoEsperado ---
    @Test
    fun validarPublicoEsperado_ceroNegativoONoNumerico_retornaFalse() {
        assertFalse(validarPublicoEsperado("0"))
        assertFalse(validarPublicoEsperado("-10"))
        assertFalse(validarPublicoEsperado("abc"))
        assertFalse(validarPublicoEsperado(""))
    }

    @Test
    fun validarPublicoEsperado_numeroPositivo_retornaTrue() {
        assertTrue(validarPublicoEsperado("1"))
        assertTrue(validarPublicoEsperado("250"))
    }

    // --- validarTelefono ---
    @Test
    fun validarTelefono_formatoInvalido_retornaFalse() {
        assertFalse(validarTelefono("1234567")) // menos de 8 dígitos
        assertFalse(validarTelefono("1234567890123")) // más de 12 dígitos
        assertFalse(validarTelefono("+56 9 abcd 1234"))
    }

    @Test
    fun validarTelefono_formatoValido_retornaTrue() {
        assertTrue(validarTelefono("+56 9 5550 0001"))
        assertTrue(validarTelefono("955500001"))
        assertTrue(validarTelefono("56-9-5550-0001"))
    }

    // --- validarRut ---
    @Test
    fun validarRut_formatoInvalido_retornaFalse() {
        assertFalse(validarRut("123456-7")) // menos de 7 dígitos base
        assertFalse(validarRut("12.345.678-A")) // dígito verificador inválido
        assertFalse(validarRut(""))
    }

    @Test
    fun validarRut_formatoValidoConYSinPuntos_retornaTrue() {
        assertTrue(validarRut("12.345.678-9"))
        assertTrue(validarRut("12345678-K"))
        assertTrue(validarRut("9876543k"))
    }
}