package com.example.activacorp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FechasTest {

    // ==========================================
    // PRUEBAS PARA hoyTexto() (10 pruebas)
    // ==========================================

    @Test
    fun hoyTexto_retornaFechaActualEnFormatoIso() {
        val resultado = hoyTexto()
        assertEquals(LocalDate.now().toString(), resultado)
        assertTrue(Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(resultado))
    }

    @Test
    fun hoyTexto_noRetornaNuloNiVacio() {
        val resultado = hoyTexto()
        assertNotNull(resultado)
        assertTrue(resultado.isNotBlank())
    }

    @Test
    fun hoyTexto_tieneLongitudExactaDe10Caracteres() {
        val resultado = hoyTexto()
        assertEquals(10, resultado.length)
    }

    @Test
    fun hoyTexto_contieneExactamenteDosGuiones() {
        val resultado = hoyTexto()
        assertEquals(2, resultado.count { it == '-' })
    }

    @Test
    fun hoyTexto_noContieneSlashesNiEspacios() {
        val resultado = hoyTexto()
        assertFalse(resultado.contains("/"))
        assertFalse(resultado.contains(" "))
    }

    @Test
    fun hoyTexto_anioCoincideConElAnioActual() {
        val partes = hoyTexto().split("-")
        assertEquals(LocalDate.now().year.toString(), partes[0])
    }

    @Test
    fun hoyTexto_mesCoincideConElMesActualConDosDigitos() {
        val partes = hoyTexto().split("-")
        val mesEsperado = LocalDate.now().monthValue.toString().padStart(2, '0')
        assertEquals(mesEsperado, partes[1])
    }

    @Test
    fun hoyTexto_diaCoincideConElDiaActualConDosDigitos() {
        val partes = hoyTexto().split("-")
        val diaEsperado = LocalDate.now().dayOfMonth.toString().padStart(2, '0')
        assertEquals(diaEsperado, partes[2])
    }

    @Test
    fun hoyTexto_sePuedeParsearNuevamenteALocalDate() {
        val resultado = hoyTexto()
        val fechaParseada = LocalDate.parse(resultado, DateTimeFormatter.ISO_LOCAL_DATE)
        assertEquals(LocalDate.now(), fechaParseada)
    }

    @Test
    fun hoyTexto_llamadasConsecutivasRetornanElMismoValor() {
        assertEquals(hoyTexto(), hoyTexto())
    }

    // ==========================================
    // PRUEBAS PARA ahoraTexto() (12 pruebas)
    // ==========================================

    @Test
    fun ahoraTexto_retornaFechaYHoraEnFormatoEsperado() {
        val resultado = ahoraTexto()
        assertTrue(Regex("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$").matches(resultado))
    }

    @Test
    fun ahoraTexto_noRetornaNuloNiVacio() {
        val resultado = ahoraTexto()
        assertNotNull(resultado)
        assertTrue(resultado.isNotBlank())
    }

    @Test
    fun ahoraTexto_tieneLongitudExactaDe16Caracteres() {
        val resultado = ahoraTexto()
        assertEquals(16, resultado.length)
    }

    @Test
    fun ahoraTexto_seDivideEnFechaYHoraPorUnEspacio() {
        val partes = ahoraTexto().split(" ")
        assertEquals(2, partes.size)
    }

    @Test
    fun ahoraTexto_parteDeFechaCoincideConHoyTexto() {
        val parteFecha = ahoraTexto().split(" ")[0]
        assertEquals(hoyTexto(), parteFecha)
    }

    @Test
    fun ahoraTexto_parteDeHoraTieneLongitud5YDosPuntos() {
        val parteHora = ahoraTexto().split(" ")[1]
        assertEquals(5, parteHora.length)
        assertEquals(':', parteHora[2])
    }

    @Test
    fun ahoraTexto_noIncluyeSegundos() {
        val parteHora = ahoraTexto().split(" ")[1]
        assertEquals(1, parteHora.count { it == ':' })
    }

    @Test
    fun ahoraTexto_horaEstaEnRangoValido00a23() {
        val hora = ahoraTexto().split(" ")[1].split(":")[0].toInt()
        assertTrue(hora in 0..23)
    }

    @Test
    fun ahoraTexto_minutoEstaEnRangoValido00a59() {
        val minuto = ahoraTexto().split(" ")[1].split(":")[1].toInt()
        assertTrue(minuto in 0..59)
    }

    @Test
    fun ahoraTexto_horaCoincideConLaHoraActualDelSistema() {
        val horaTexto = ahoraTexto().split(" ")[1].split(":")[0].toInt()
        val horaSistema = LocalDateTime.now().hour
        assertEquals(horaSistema, horaTexto)
    }

    @Test
    fun ahoraTexto_sePuedeParsearConDateTimeFormatter() {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val fechaHoraParseada = LocalDateTime.parse(ahoraTexto(), formatter)
        assertEquals(LocalDate.now(), fechaHoraParseada.toLocalDate())
    }

    @Test
    fun ahoraTexto_noUsaFormato12HorasAmPm() {
        val resultado = ahoraTexto().uppercase()
        assertFalse(resultado.contains("AM"))
        assertFalse(resultado.contains("PM"))
    }
}