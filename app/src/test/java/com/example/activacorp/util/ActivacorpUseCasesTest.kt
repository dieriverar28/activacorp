package com.example.activacorp.domain.usecase

import com.example.activacorp.data.model.ContactoRequest
import com.example.activacorp.data.model.OportunidadRequest
import com.example.activacorp.data.model.OrganizacionRequest
import com.example.activacorp.data.repository.ActivacorpRepository
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.OrganizacionResumen
import com.example.activacorp.model.SeguimientoDetalle
import com.example.activacorp.model.Usuario
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ActivacorpUseCasesTest {

    private lateinit var repository: ActivacorpRepository
    private lateinit var useCases: ActivacorpUseCases

    @Before
    fun setUp() {
        repository = mockk()
        useCases = ActivacorpUseCases(repository)
    }

    // ==========================================
    // 1. Autenticación y Usuarios (4 pruebas)
    // ==========================================
    @Test
    fun iniciarSesion_delegaEnRepositorio() = runTest {
        val usuario = Usuario(1, "Ana", "ana@activacorp.cl", "1234", "EJECUTIVO")
        coEvery { repository.iniciarSesion("ana@activacorp.cl", "1234") } returns usuario

        val resultado = useCases.iniciarSesion("ana@activacorp.cl", "1234")

        assertEquals(usuario, resultado)
        coVerify(exactly = 1) { repository.iniciarSesion("ana@activacorp.cl", "1234") }
    }

    @Test(expected = IllegalStateException::class)
    fun iniciarSesion_cuandoCredencialesFallan_propagaExcepcion() = runTest {
        coEvery { repository.iniciarSesion(any(), any()) } throws IllegalStateException("Credenciales inválidas")

        useCases.iniciarSesion("malo@activacorp.cl", "0000")
    }

    @Test
    fun ejecutivos_delegaEnRepositorio() = runTest {
        val lista = listOf(Usuario(1, "Ana", "ana@activacorp.cl", "1234", "EJECUTIVO"))
        coEvery { repository.ejecutivos() } returns lista

        val resultado = useCases.ejecutivos()

        assertEquals(lista, resultado)
        coVerify(exactly = 1) { repository.ejecutivos() }
    }

    @Test
    fun ejecutivos_cuandoNoHayRegistros_retornaListaVacia() = runTest {
        coEvery { repository.ejecutivos() } returns emptyList()

        val resultado = useCases.ejecutivos()

        assertTrue(resultado.isEmpty())
    }

    // ==========================================
    // 2. Consulta de Oportunidades y Seguimientos (6 pruebas)
    // ==========================================
    @Test
    fun oportunidades_conEjecutivoId_delegaEnRepositorio() = runTest {
        val lista = listOf(mockk<OportunidadConDatos>(relaxed = true))
        coEvery { repository.oportunidades(10) } returns lista

        val resultado = useCases.oportunidades(10)

        assertEquals(lista, resultado)
        coVerify(exactly = 1) { repository.oportunidades(10) }
    }

    @Test
    fun oportunidades_conMultiplesRegistros_retornaListaCompleta() = runTest {
        val lista = listOf(
            mockk<OportunidadConDatos>(relaxed = true),
            mockk<OportunidadConDatos>(relaxed = true)
        )
        coEvery { repository.oportunidades(10) } returns lista

        val resultado = useCases.oportunidades(10)

        assertEquals(2, resultado.size)
        coVerify(exactly = 1) { repository.oportunidades(10) }
    }

    @Test
    fun oportunidades_cuandoEstaVacio_retornaListaVacia() = runTest {
        coEvery { repository.oportunidades(any()) } returns emptyList()

        val resultado = useCases.oportunidades(99)

        assertTrue(resultado.isEmpty())
    }

    @Test
    fun oportunidad_delegaEnRepositorio() = runTest {
        val item = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.oportunidad(5) } returns item

        val resultado = useCases.oportunidad(5)

        assertEquals(item, resultado)
        coVerify(exactly = 1) { repository.oportunidad(5) }
    }

    @Test
    fun seguimientos_delegaEnRepositorio() = runTest {
        val lista = listOf(mockk<SeguimientoDetalle>(relaxed = true))
        coEvery { repository.seguimientos(5) } returns lista

        val resultado = useCases.seguimientos(5)

        assertEquals(lista, resultado)
        coVerify(exactly = 1) { repository.seguimientos(5) }
    }

    @Test
    fun seguimientos_sinHistorial_retornaListaVacia() = runTest {
        coEvery { repository.seguimientos(5) } returns emptyList()

        val resultado = useCases.seguimientos(5)

        assertTrue(resultado.isEmpty())
    }

    // ==========================================
    // 3. Gestión y Mutación de Oportunidades (8 pruebas)
    // ==========================================
    @Test
    fun crearOportunidad_delegaEnRepositorio() = runTest {
        val request = mockk<OportunidadRequest>(relaxed = true)
        val creado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.crearOportunidad(request) } returns creado

        val resultado = useCases.crearOportunidad(request)

        assertEquals(creado, resultado)
        coVerify(exactly = 1) { repository.crearOportunidad(request) }
    }

    @Test(expected = RuntimeException::class)
    fun crearOportunidad_cuandoFallaServidor_propagaError() = runTest {
        val request = mockk<OportunidadRequest>(relaxed = true)
        coEvery { repository.crearOportunidad(request) } throws RuntimeException("Error 500")

        useCases.crearOportunidad(request)
    }

    @Test
    fun actualizarOportunidad_delegaEnRepositorio() = runTest {
        val request = mockk<OportunidadRequest>(relaxed = true)
        val actualizado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.actualizarOportunidad(5, request) } returns actualizado

        val resultado = useCases.actualizarOportunidad(5, request)

        assertEquals(actualizado, resultado)
        coVerify(exactly = 1) { repository.actualizarOportunidad(5, request) }
    }

    @Test(expected = NoSuchElementException::class)
    fun actualizarOportunidad_idInexistente_propagaExcepcion() = runTest {
        val request = mockk<OportunidadRequest>(relaxed = true)
        coEvery { repository.actualizarOportunidad(999, request) } throws NoSuchElementException("No existe")

        useCases.actualizarOportunidad(999, request)
    }

    @Test
    fun cambiarEstado_contactoRealizado_delegaEnRepositorio() = runTest {
        val actualizado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.cambiarEstado(5, 1, "CONTACTO_REALIZADO") } returns actualizado

        val resultado = useCases.cambiarEstado(5, 1, "CONTACTO_REALIZADO")

        assertEquals(actualizado, resultado)
        coVerify(exactly = 1) { repository.cambiarEstado(5, 1, "CONTACTO_REALIZADO") }
    }

    @Test
    fun cambiarEstado_cerradaGanada_delegaEnRepositorio() = runTest {
        val actualizado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.cambiarEstado(5, 1, "CERRADA_GANADA") } returns actualizado

        val resultado = useCases.cambiarEstado(5, 1, "CERRADA_GANADA")

        assertEquals(actualizado, resultado)
        coVerify(exactly = 1) { repository.cambiarEstado(5, 1, "CERRADA_GANADA") }
    }

    @Test
    fun asignar_delegaEnRepositorio() = runTest {
        val asignado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.asignar(5, 1, 8) } returns asignado

        val resultado = useCases.asignar(5, 1, 8)

        assertEquals(asignado, resultado)
        coVerify(exactly = 1) { repository.asignar(5, 1, 8) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun asignar_ejecutivoInvalido_propagaExcepcion() = runTest {
        coEvery { repository.asignar(5, 1, -1) } throws IllegalArgumentException("Ejecutivo no válido")

        useCases.asignar(5, 1, -1)}
}


// ==========================================
// 4. Organizaciones y Contactos (10 pruebas)
// =================================