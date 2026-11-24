package com.example.activacorp.util

import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Rol
import com.example.activacorp.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermisosTest {

    private val supervisor = Usuario(
        id = 1,
        nombre = "Admin",
        correo = "admin@test.cl",
        clave = "1234",
        rol = "SUPERVISOR"
    )

    private val ejecutivo = Usuario(
        id = 5,
        nombre = "Ana",
        correo = "ana@test.cl",
        clave = "1234",
        rol = "EJECUTIVO"
    )

    // ==========================================
    // 1. esEstadoFinal (6 pruebas)
    // ==========================================
    @Test
    fun esEstadoFinal_participacionConfirmada_retornaTrue() {
        assertTrue(esEstadoFinal(EstadoOportunidad.PARTICIPACION_CONFIRMADA))
    }

    @Test
    fun esEstadoFinal_oportunidadDescartada_retornaTrue() {
        assertTrue(esEstadoFinal(EstadoOportunidad.OPORTUNIDAD_DESCARTADA))
    }

    @Test
    fun esEstadoFinal_eventoIdentificado_retornaFalse() {
        assertFalse(esEstadoFinal(EstadoOportunidad.EVENTO_IDENTIFICADO))
    }

    @Test
    fun esEstadoFinal_contactoPendiente_retornaFalse() {
        assertFalse(esEstadoFinal(EstadoOportunidad.CONTACTO_PENDIENTE))
    }

    @Test
    fun esEstadoFinal_contactoRealizado_retornaFalse() {
        assertFalse(esEstadoFinal(EstadoOportunidad.CONTACTO_REALIZADO))
    }

    @Test
    fun esEstadoFinal_propuestaEnviadaYEnEspera_retornaFalse() {
        assertFalse(esEstadoFinal(EstadoOportunidad.PROPUESTA_ENVIADA))
        assertFalse(esEstadoFinal(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA))
    }

    // ==========================================
    // 2. puedeGestionar (6 pruebas)
    // ==========================================
    @Test
    fun puedeGestionar_supervisorConResponsableAsignado_retornaTrue() {
        assertTrue(puedeGestionar(supervisor, responsableId = 99))
    }

    @Test
    fun puedeGestionar_supervisorConSuPropioId_retornaTrue() {
        assertTrue(puedeGestionar(supervisor, responsableId = 1))
    }

    @Test
    fun puedeGestionar_supervisorSinResponsableNull_retornaTrue() {
        assertTrue(puedeGestionar(supervisor, responsableId = null))
    }

    @Test
    fun puedeGestionar_ejecutivoConSuMismoId_retornaTrue() {
        assertTrue(puedeGestionar(ejecutivo, responsableId = 5))
    }

    @Test
    fun puedeGestionar_ejecutivoConIdDeOtroUsuario_retornaFalse() {
        assertFalse(puedeGestionar(ejecutivo, responsableId = 8))
    }

    @Test
    fun puedeGestionar_ejecutivoSinResponsableNull_retornaFalse() {
        assertFalse(puedeGestionar(ejecutivo, responsableId = null))
    }

    // ==========================================
    // 3. estadosSiguientes para EJECUTIVO (6 pruebas)
    // ==========================================
    @Test
    fun estadosSiguientes_ejecutivoDesdeEventoIdentificado_avanzaAContactoPendiente() {
        assertEquals(
            listOf(EstadoOportunidad.CONTACTO_PENDIENTE),
            estadosSiguientes(EstadoOportunidad.EVENTO_IDENTIFICADO, Rol.EJECUTIVO)
        )
    }

    @Test
    fun estadosSiguientes_ejecutivoDesdeContactoPendiente_avanzaAContactoRealizado() {
        assertEquals(
            listOf(EstadoOportunidad.CONTACTO_REALIZADO),
            estadosSiguientes(EstadoOportunidad.CONTACTO_PENDIENTE, Rol.EJECUTIVO)
        )
    }

    @Test
    fun estadosSiguientes_ejecutivoDesdeContactoRealizado_avanzaAPropuestaEnviada() {
        assertEquals(
            listOf(EstadoOportunidad.PROPUESTA_ENVIADA),
            estadosSiguientes(EstadoOportunidad.CONTACTO_REALIZADO, Rol.EJECUTIVO)
        )
    }

    @Test
    fun estadosSiguientes_ejecutivoDesdePropuestaEnviada_avanzaAEnEsperaDeRespuesta() {
        assertEquals(
            listOf(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA),
            estadosSiguientes(EstadoOportunidad.PROPUESTA_ENVIADA, Rol.EJECUTIVO)
        )
    }

    @Test
    fun estadosSiguientes_ejecutivoDesdeEnEsperaDeRespuesta_retornaListaVacia() {
        assertTrue(
            estadosSiguientes(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA, Rol.EJECUTIVO).isEmpty()
        )
    }

    @Test
    fun estadosSiguientes_ejecutivoDesdeEstadosFinales_retornaListaVacia() {
        assertTrue(estadosSiguientes(EstadoOportunidad.PARTICIPACION_CONFIRMADA, Rol.EJECUTIVO).isEmpty())
        assertTrue(estadosSiguientes(EstadoOportunidad.OPORTUNIDAD_DESCARTADA, Rol.EJECUTIVO).isEmpty())
    }

    // ==========================================
    // 4. estadosSiguientes para SUPERVISOR (6 pruebas)
    // ==========================================
    @Test
    fun estadosSiguientes_supervisorDesdeEnEspera_puedeConfirmarODescartar() {
        val resultado = estadosSiguientes(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA, Rol.SUPERVISOR)
        assertEquals(
            listOf(EstadoOportunidad.PARTICIPACION_CONFIRMADA, EstadoOportunidad.OPORTUNIDAD_DESCARTADA),
            resultado
        )
    }

    @Test
    fun estadosSiguientes_supervisorDesdeEventoIdentificado_soloPuedeDescartar() {
        assertEquals(
            listOf(EstadoOportunidad.OPORTUNIDAD_DESCARTADA),
            estadosSiguientes(EstadoOportunidad.EVENTO_IDENTIFICADO, Rol.SUPERVISOR)
        )
    }

    @Test
    fun estadosSiguientes_supervisorDesdeContactoPendiente_soloPuedeDescartar() {
        assertEquals(
            listOf(EstadoOportunidad.OPORTUNIDAD_DESCARTADA),
            estadosSiguientes(EstadoOportunidad.CONTACTO_PENDIENTE, Rol.SUPERVISOR)
        )
    }

    @Test
    fun estadosSiguientes_supervisorDesdeContactoRealizado_soloPuedeDescartar() {
        assertEquals(
            listOf(EstadoOportunidad.OPORTUNIDAD_DESCARTADA),
            estadosSiguientes(EstadoOportunidad.CONTACTO_REALIZADO, Rol.SUPERVISOR)
        )
    }

    @Test
    fun estadosSiguientes_supervisorDesdePropuestaEnviada_soloPuedeDescartar() {
        assertEquals(
            listOf(EstadoOportunidad.OPORTUNIDAD_DESCARTADA),
            estadosSiguientes(EstadoOportunidad.PROPUESTA_ENVIADA, Rol.SUPERVISOR)
        )
    }

    @Test
    fun estadosSiguientes_supervisorDesdeEstadosFinales_retornaListaVacia() {
        assertTrue(estadosSiguientes(EstadoOportunidad.PARTICIPACION_CONFIRMADA, Rol.SUPERVISOR).isEmpty())
        assertTrue(estadosSiguientes(EstadoOportunidad.OPORTUNIDAD_DESCARTADA, Rol.SUPERVISOR).isEmpty())
    }
}