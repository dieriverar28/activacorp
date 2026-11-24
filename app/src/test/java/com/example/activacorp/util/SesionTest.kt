package com.example.activacorp.util

import com.example.activacorp.model.Usuario
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test

class SesionTest {

    private val usuarioEjecutivo = Usuario(
        id = 10,
        nombre = "Camila",
        correo = "camila@activacorp.cl",
        clave = "1234",
        rol = "EJECUTIVO"
    )

    private val usuarioAdmin = Usuario(
        id = 20,
        nombre = "Roberto",
        correo = "roberto@activacorp.cl",
        clave = "abcd",
        rol = "ADMIN"
    )

    @Before
    @After
    fun limpiarSesion() {
        Sesion.cerrar()
    }

    // ==========================================
    // 1. Estado inicial y lectura (3 pruebas)
    // ==========================================
    @Test
    fun estadoInicial_sinIniciarSesion_esNull() {
        assertNull(Sesion.usuario.value)
    }

    @Test
    fun iniciar_usuarioDejaDeSerNull() {
        Sesion.iniciar(usuarioEjecutivo)
        assertNotNull(Sesion.usuario.value)
    }

    @Test
    fun iniciar_guardaUsuarioEnStateFlow() {
        Sesion.iniciar(usuarioEjecutivo)
        assertEquals(usuarioEjecutivo, Sesion.usuario.value)
    }

    // ==========================================
    // 2. Integridad de datos del Usuario (6 pruebas)
    // ==========================================
    @Test
    fun iniciar_conservaIdDelUsuario() {
        Sesion.iniciar(usuarioEjecutivo)
        assertEquals(10, Sesion.usuario.value?.id)
    }

    @Test
    fun iniciar_conservaNombreDelUsuario() {
        Sesion.iniciar(usuarioEjecutivo)
        assertEquals("Camila", Sesion.usuario.value?.nombre)
    }

    @Test
    fun iniciar_conservaCorreoDelUsuario() {
        Sesion.iniciar(usuarioEjecutivo)
        assertEquals("camila@activacorp.cl", Sesion.usuario.value?.correo)
    }

    @Test
    fun iniciar_conservaClaveDelUsuario() {
        Sesion.iniciar(usuarioEjecutivo)
        assertEquals("1234", Sesion.usuario.value?.clave)
    }

    @Test
    fun iniciar_conservaRolDelUsuario() {
        Sesion.iniciar(usuarioEjecutivo)
        assertEquals("EJECUTIVO", Sesion.usuario.value?.rol)
    }

    @Test
    fun iniciar_mantieneLaMismaReferenciaDeObjeto() {
        Sesion.iniciar(usuarioEjecutivo)
        assertSame(usuarioEjecutivo, Sesion.usuario.value)
    }

    // ==========================================
    // 3. Cambio y actualización de sesión (4 pruebas)
    // ==========================================
    @Test
    fun iniciar_conNuevoUsuario_reemplazaAlAnterior() {
        Sesion.iniciar(usuarioEjecutivo)
        Sesion.iniciar(usuarioAdmin)

        assertEquals(usuarioAdmin, Sesion.usuario.value)
        assertNotEquals(usuarioEjecutivo, Sesion.usuario.value)
    }

    @Test
    fun iniciar_conNuevoUsuario_actualizaElRolEnSesion() {
        Sesion.iniciar(usuarioEjecutivo)
        Sesion.iniciar(usuarioAdmin)

        assertEquals("ADMIN", Sesion.usuario.value?.rol)
    }

    @Test
    fun iniciar_dosVecesConElMismoUsuario_mantieneSesionValida() {
        Sesion.iniciar(usuarioEjecutivo)
        Sesion.iniciar(usuarioEjecutivo)

        assertEquals(usuarioEjecutivo, Sesion.usuario.value)
    }

    @Test
    fun iniciar_conCopiaModificadaDeUsuario_reflejaCambio() {
        Sesion.iniciar(usuarioEjecutivo)
        val usuarioActualizado = usuarioEjecutivo.copy(nombre = "Camila Soto")
        Sesion.iniciar(usuarioActualizado)

        assertEquals("Camila Soto", Sesion.usuario.value?.nombre)
    }

    // ==========================================
    // 4. Cierre de sesión y ciclos completos (5 pruebas)
    // ==========================================
    @Test
    fun cerrar_limpiaUsuarioDeLaSesion() {
        Sesion.iniciar(usuarioEjecutivo)
        Sesion.cerrar()

        assertNull(Sesion.usuario.value)
    }

    @Test
    fun cerrar_cuandoYaEstaCerrada_noLanzaErrorYMantieneNull() {
        Sesion.cerrar()
        Sesion.cerrar()

        assertNull(Sesion.usuario.value)
    }

    @Test
    fun cicloCompleto_iniciarCerrarYVolverAIniciar_funcionaCorrectamente() {
        Sesion.iniciar(usuarioEjecutivo)
        assertEquals(usuarioEjecutivo, Sesion.usuario.value)

        Sesion.cerrar()
        assertNull(Sesion.usuario.value)

        Sesion.iniciar(usuarioAdmin)
        assertEquals(usuarioAdmin, Sesion.usuario.value)
    }

    @Test
    fun cerrar_despuesDeReemplazarUsuario_dejaSesionEnNull() {
        Sesion.iniciar(usuarioEjecutivo)
        Sesion.iniciar(usuarioAdmin)
        Sesion.cerrar()

        assertNull(Sesion.usuario.value)
    }

    @Test
    fun stateFlow_exponeSiempreElUltimoValorAsignado() {
        val flujo = Sesion.usuario
        assertNull(flujo.value)

        Sesion.iniciar(usuarioEjecutivo)
        assertEquals(10, flujo.value?.id)

        Sesion.cerrar()
        assertNull(flujo.value)
    }
}