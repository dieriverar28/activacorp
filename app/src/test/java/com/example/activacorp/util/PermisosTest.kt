package com.example.activacorp.util

import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Rol
import com.example.activacorp.model.Usuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PermisosTest {

    // --- esEstadoFinal ---
    @Test
    fun esEstadoFinal_estadosFinales_retornaTrue() {
        assertTrue(esEstadoFinal(EstadoOportunidad.PARTICIPACION_CONFIRMADA))
        assertTrue(esEstadoFinal(EstadoOportunidad.OPORTUNIDAD_DESCARTADA))
    }

    @Test
    fun esEstadoFinal_estadosEnProceso_retornaFalse() {
        assertFalse(esEstadoFinal(EstadoOportunidad.EVENTO_IDENTIFICADO))
        assertFalse(esEstadoFinal(EstadoOportunidad.CONTACTO_PENDIENTE))
        assertFalse(esEstadoFinal(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA))
    }

    // --- puedeGestionar ---
    @Test
    fun puedeGestionar_supervisor_siemprePuedeGestionar() {
        // ARRANGE
        val supervisor = Usuario(
            id = 1,
            nombre = "Admin",
            correo = "admin@test.cl",
            clave = "1234",
            rol = "SUPERVISOR"
        )

        // ACT & ASSERT
        assertTrue(puedeGestionar(supervisor, responsableId = 99))
        assertTrue(puedeGestionar(supervisor, responsableId = null))
    }

    @Test
    fun puedeGestionar_ejecutivo_soloGestionaSusAsignadas() {
        // ARRANGE
        val ejecutivo = Usuario(
            id = 5,
            nombre = "Ana",
            correo = "ana@test.cl",
            clave = "1234",
            rol = "EJECUTIVO"
        )

        // ACT & ASSERT
        assertTrue(puedeGestionar(ejecutivo, responsableId = 5))
        assertFalse(puedeGestionar(ejecutivo, responsableId = 8))
        assertFalse(puedeGestionar(ejecutivo, responsableId = null))
    }

    // --- estadosSiguientes ---
    @Test
    fun estadosSiguientes_estadoFinal_retornaListaVacia() {
        val resultado = estadosSiguientes(EstadoOportunidad.PARTICIPACION_CONFIRMADA, Rol.SUPERVISOR)
        assertTrue(resultado.isEmpty())
    }

    @Test
    fun estadosSiguientes_ejecutivo_avanzaPasoAPasoPeroNoCierra() {
        assertEquals(
            listOf(EstadoOportunidad.CONTACTO_PENDIENTE),
            estadosSiguientes(EstadoOportunidad.EVENTO_IDENTIFICADO, Rol.EJECUTIVO)
        )
        assertEquals(
            listOf(EstadoOportunidad.CONTACTO_REALIZADO),
            estadosSiguientes(EstadoOportunidad.CONTACTO_PENDIENTE, Rol.EJECUTIVO)
        )
        assertEquals(
            listOf(EstadoOportunidad.PROPUESTA_ENVIADA),
            estadosSiguientes(EstadoOportunidad.CONTACTO_REALIZADO, Rol.EJECUTIVO)
        )
        assertEquals(
            listOf(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA),
            estadosSiguientes(EstadoOportunidad.PROPUESTA_ENVIADA, Rol.EJECUTIVO)
        )
        assertTrue(
            estadosSiguientes(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA, Rol.EJECUTIVO).isEmpty()
        )
    }

    @Test
    fun estadosSiguientes_supervisor_confirmaSoloEnEsperaYDescartaSiempre() {
        // En espera de respuesta puede confirmar o descartar
        val enEspera = estadosSiguientes(EstadoOportunidad.EN_ESPERA_DE_RESPUESTA, Rol.SUPERVISOR)
        assertEquals(
            listOf(EstadoOportunidad.PARTICIPACION_CONFIRMADA, EstadoOportunidad.OPORTUNIDAD_DESCARTADA),
            enEspera
        )

        // En cualquier otro estado activo solo puede descartar
        val enContacto = estadosSiguientes(EstadoOportunidad.CONTACTO_PENDIENTE, Rol.SUPERVISOR)
        assertEquals(listOf(EstadoOportunidad.OPORTUNIDAD_DESCARTADA), enContacto)
    }
}