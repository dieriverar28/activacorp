package com.example.activacorp.util

import com.example.activacorp.model.Usuario
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SesionTest {

    @Before
    @After
    fun limpiarSesion() {
        Sesion.cerrar()
    }

    @Test
    fun iniciar_guardaUsuarioEnStateFlow() {
        // ARRANGE
        val usuario = Usuario(
            id = 10,
            nombre = "Camila",
            correo = "camila@activacorp.cl",
            clave = "1234",
            rol = "EJECUTIVO"
        )

        // ACT
        Sesion.iniciar(usuario)

        // ASSERT
        assertEquals(usuario, Sesion.usuario.value)
    }

    @Test
    fun cerrar_limpiaUsuarioDeLaSesion() {
        // ARRANGE
        val usuario = Usuario(
            id = 10,
            nombre = "Camila",
            correo = "camila@activacorp.cl",
            clave = "1234",
            rol = "EJECUTIVO"
        )
        Sesion.iniciar(usuario)

        // ACT
        Sesion.cerrar()

        // ASSERT
        assertNull(Sesion.usuario.value)
    }
}