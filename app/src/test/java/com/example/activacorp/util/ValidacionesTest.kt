package com.example.activacorp.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ValidacionesTest {

    // ==========================================
    // 1. validarCampoObligatorio (5 pruebas)
    // ==========================================
    @Test
    fun validarCampoObligatorio_textoVacio_retornaFalse() {
        assertFalse(validarCampoObligatorio(""))
    }

    @Test
    fun validarCampoObligatorio_soloEspacios_retornaFalse() {
        assertFalse(validarCampoObligatorio("     "))
    }

    @Test
    fun validarCampoObligatorio_saltosDeLineaYTabs_retornaFalse() {
        assertFalse(validarCampoObligatorio("\n\t  "))
    }

    @Test
    fun validarCampoObligatorio_unSoloCaracter_retornaTrue() {
        assertTrue(validarCampoObligatorio("A"))
    }

    @Test
    fun validarCampoObligatorio_textoNormal_retornaTrue() {
        assertTrue(validarCampoObligatorio("Activacorp"))
    }

    // ==========================================
    // 2. validarNombre (6 pruebas)
    // ==========================================
    @Test
    fun validarNombre_vacio_retornaFalse() {
        assertFalse(validarNombre(""))
    }

    @Test
    fun validarNombre_dosCaracteres_retornaFalse() {
        assertFalse(validarNombre("Ab"))
    }

    @Test
    fun validarNombre_unCaracterConMuchosEspacios_retornaFalse() {
        assertFalse(validarNombre("   A   "))
    }

    @Test
    fun validarNombre_exactamenteTresCaracteres_retornaTrue() {
        assertTrue(validarNombre("Ana"))
    }

    @Test
    fun validarNombre_tresCaracteresConEspaciosALosLados_retornaTrue() {
        assertTrue(validarNombre("  Luz  "))
    }

    @Test
    fun validarNombre_nombreCompletoLargo_retornaTrue() {
        assertTrue(validarNombre("María Fernanda González"))
    }

    // ==========================================
    // 3. validarFecha (7 pruebas)
    // ==========================================
    @Test
    fun validarFecha_vacia_retornaFalse() {
        assertFalse(validarFecha(""))
    }

    @Test
    fun validarFecha_formatoChilenoDiaMesAnio_retornaFalse() {
        assertFalse(validarFecha("23-11-2026"))
    }

    @Test
    fun validarFecha_conSlashes_retornaFalse() {
        assertFalse(validarFecha("2026/11/23"))
    }

    @Test
    fun validarFecha_febrero30Inexistente_retornaFalse() {
        assertFalse(validarFecha("2026-02-30"))
    }

    @Test
    fun validarFecha_febrero29EnAnioNoBisiesto_retornaFalse() {
        assertFalse(validarFecha("2026-02-29"))
    }

    @Test
    fun validarFecha_febrero29EnAnioBisiesto_retornaTrue() {
        assertTrue(validarFecha("2024-02-29"))
    }

    @Test
    fun validarFecha_formatoIsoValido_retornaTrue() {
        assertTrue(validarFecha("2026-11-23"))
    }

    // ==========================================
    // 4. validarFechaEvento (6 pruebas)
    // ==========================================
    @Test
    fun validarFechaEvento_fechaDeAyer_retornaFalse() {
        val ayer = LocalDate.now().minusDays(1).toString()
        assertFalse(validarFechaEvento(ayer))
    }

    @Test
    fun validarFechaEvento_fechaAnioPasado_retornaFalse() {
        val pasado = LocalDate.now().minusYears(1).toString()
        assertFalse(validarFechaEvento(pasado))
    }

    @Test
    fun validarFechaEvento_textoInvalido_retornaFalse() {
        assertFalse(validarFechaEvento("fecha-mala"))
    }

    @Test
    fun validarFechaEvento_vacia_retornaFalse() {
        assertFalse(validarFechaEvento(""))
    }

    @Test
    fun validarFechaEvento_fechaDeHoy_retornaTrue() {
        val hoy = LocalDate.now().toString()
        assertTrue(validarFechaEvento(hoy))
    }

    @Test
    fun validarFechaEvento_fechaFutura_retornaTrue() {
        val manana = LocalDate.now().plusDays(1).toString()
        assertTrue(validarFechaEvento(manana))
    }

    // ==========================================
    // 5. validarPublicoEsperado (6 pruebas)
    // ==========================================
    @Test
    fun validarPublicoEsperado_cero_retornaFalse() {
        assertFalse(validarPublicoEsperado("0"))
    }

    @Test
    fun validarPublicoEsperado_numeroNegativo_retornaFalse() {
        assertFalse(validarPublicoEsperado("-10"))
    }

    @Test
    fun validarPublicoEsperado_textoNoNumerico_retornaFalse() {
        assertFalse(validarPublicoEsperado("abc"))
    }

    @Test
    fun validarPublicoEsperado_decimal_retornaFalse() {
        assertFalse(validarPublicoEsperado("15.5"))
    }

    @Test
    fun validarPublicoEsperado_vacio_retornaFalse() {
        assertFalse(validarPublicoEsperado(""))
    }

    @Test
    fun validarPublicoEsperado_unoYMayorACero_retornaTrue() {
        assertTrue(validarPublicoEsperado("1"))
        assertTrue(validarPublicoEsperado("250"))
    }

    // ==========================================
    // 6. validarTelefono (6 pruebas)
    // ==========================================
    @Test
    fun validarTelefono_menosDe8Digitos_retornaFalse() {
        assertFalse(validarTelefono("1234567"))
    }

    @Test
    fun validarTelefono_masDe12Digitos_retornaFalse() {
        assertFalse(validarTelefono("1234567890123"))
    }

    @Test
    fun validarTelefono_conLetras_retornaFalse() {
        assertFalse(validarTelefono("+56 9 abcd 1234"))
    }

    @Test
    fun validarTelefono_vacio_retornaFalse() {
        assertFalse(validarTelefono(""))
    }

    @Test
    fun validarTelefono_formatoChilenoConMasYEspacios_retornaTrue() {
        assertTrue(validarTelefono("+56 9 5550 0001"))
    }

    @Test
    fun validarTelefono_ochoYDoceDigitosSinMas_retornaTrue() {
        assertTrue(validarTelefono("95550000"))
        assertTrue(validarTelefono("56-9-5550-0001"))
    }

    // ==========================================
    // 7. validarRut (6 pruebas)
    // ==========================================
    @Test
    fun validarRut_menosDe7DigitosBase_retornaFalse() {
        assertFalse(validarRut("123456-7"))
    }

    @Test
    fun validarRut_masDe8DigitosBase_retornaFalse() {
        assertFalse(validarRut("123456789-0"))
    }

    @Test
    fun validarRut_digitoVerificadorLetraInvalida_retornaFalse() {
        assertFalse(validarRut("12.345.678-A"))
    }

    @Test
    fun validarRut_vacio_retornaFalse() {
        assertFalse(validarRut(""))
    }

    @Test
    fun validarRut_conPuntosYGuion_retornaTrue() {
        assertTrue(validarRut("12.345.678-9"))
    }

    @Test
    fun validarRut_conKMayusculaYMinuscula_retornaTrue() {
        assertTrue(validarRut("12345678-K"))
        assertTrue(validarRut("9876543k"))
    }
}