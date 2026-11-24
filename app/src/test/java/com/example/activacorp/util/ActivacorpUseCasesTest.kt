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

    @Test
    fun iniciarSesion_delegaEnRepositorio() = runTest {
        val usuario = Usuario(1, "Ana", "ana@activacorp.cl", "1234", "EJECUTIVO")
        coEvery { repository.iniciarSesion("ana@activacorp.cl", "1234") } returns usuario

        val resultado = useCases.iniciarSesion("ana@activacorp.cl", "1234")

        assertEquals(usuario, resultado)
        coVerify(exactly = 1) { repository.iniciarSesion("ana@activacorp.cl", "1234") }
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
    fun oportunidades_delegaEnRepositorio() = runTest {
        val lista = listOf(mockk<OportunidadConDatos>(relaxed = true))
        coEvery { repository.oportunidades(10) } returns lista

        val resultado = useCases.oportunidades(10)

        assertEquals(lista, resultado)
        coVerify(exactly = 1) { repository.oportunidades(10) }
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
    fun crearOportunidad_delegaEnRepositorio() = runTest {
        val request = mockk<OportunidadRequest>(relaxed = true)
        val creado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.crearOportunidad(request) } returns creado

        val resultado = useCases.crearOportunidad(request)

        assertEquals(creado, resultado)
        coVerify(exactly = 1) { repository.crearOportunidad(request) }
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

    @Test
    fun cambiarEstado_delegaEnRepositorio() = runTest {
        val actualizado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.cambiarEstado(5, 1, "CONTACTO_REALIZADO") } returns actualizado

        val resultado = useCases.cambiarEstado(5, 1, "CONTACTO_REALIZADO")

        assertEquals(actualizado, resultado)
        coVerify(exactly = 1) { repository.cambiarEstado(5, 1, "CONTACTO_REALIZADO") }
    }

    @Test
    fun asignar_delegaEnRepositorio() = runTest {
        val asignado = mockk<OportunidadConDatos>(relaxed = true)
        coEvery { repository.asignar(5, 1, 8) } returns asignado

        val resultado = useCases.asignar(5, 1, 8)

        assertEquals(asignado, resultado)
        coVerify(exactly = 1) { repository.asignar(5, 1, 8) }
    }

    @Test
    fun organizaciones_delegaEnRepositorio() = runTest {
        val lista = listOf(mockk<OrganizacionResumen>(relaxed = true))
        coEvery { repository.organizaciones() } returns lista

        val resultado = useCases.organizaciones()

        assertEquals(lista, resultado)
        coVerify(exactly = 1) { repository.organizaciones() }
    }

    @Test
    fun organizacion_delegaEnRepositorio() = runTest {
        val org = mockk<Organizacion>(relaxed = true)
        coEvery { repository.organizacion(3) } returns org

        val resultado = useCases.organizacion(3)

        assertEquals(org, resultado)
        coVerify(exactly = 1) { repository.organizacion(3) }
    }

    @Test
    fun contactos_delegaEnRepositorio() = runTest {
        val lista = listOf(mockk<Contacto>(relaxed = true))
        coEvery { repository.contactos(3) } returns lista

        val resultado = useCases.contactos(3)

        assertEquals(lista, resultado)
        coVerify(exactly = 1) { repository.contactos(3) }
    }

    @Test
    fun crearOrganizacion_delegaEnRepositorio() = runTest {
        val request = mockk<OrganizacionRequest>(relaxed = true)
        val creada = mockk<Organizacion>(relaxed = true)
        coEvery { repository.crearOrganizacion(request) } returns creada

        val resultado = useCases.crearOrganizacion(request)

        assertEquals(creada, resultado)
        coVerify(exactly = 1) { repository.crearOrganizacion(request) }
    }

    @Test
    fun crearContacto_delegaEnRepositorio() = runTest {
        val request = mockk<ContactoRequest>(relaxed = true)
        val creado = mockk<Contacto>(relaxed = true)
        coEvery { repository.crearContacto(3, request) } returns creado

        val resultado = useCases.crearContacto(3, request)

        assertEquals(creado, resultado)
        coVerify(exactly = 1) { repository.crearContacto(3, request) }
    }
}