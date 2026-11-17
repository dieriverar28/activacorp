package com.example.activacorp.domain.usecase

import com.example.activacorp.data.model.AsignacionRequest
import com.example.activacorp.data.model.CambioEstadoRequest
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

/**
 * Casos de uso: el mismo patrón del proyecto de ejemplo
 * (ViewModel -> UseCases -> Repository -> API).
 * Si mañana cambia el origen de datos, solo se toca el repositorio.
 */
class ActivacorpUseCases(
    private val repository: ActivacorpRepository = ActivacorpRepository()
) {

    // Login y usuarios
    suspend fun iniciarSesion(correo: String, clave: String): Usuario =
        repository.iniciarSesion(correo, clave)

    suspend fun ejecutivos(): List<Usuario> = repository.ejecutivos()

    // Oportunidades
    suspend fun oportunidades(usuarioId: Int): List<OportunidadConDatos> =
        repository.oportunidades(usuarioId)

    suspend fun oportunidad(id: Int): OportunidadConDatos = repository.oportunidad(id)

    suspend fun seguimientos(id: Int): List<SeguimientoDetalle> = repository.seguimientos(id)

    suspend fun crearOportunidad(request: OportunidadRequest): OportunidadConDatos =
        repository.crearOportunidad(request)

    suspend fun actualizarOportunidad(
        id: Int,
        request: OportunidadRequest
    ): OportunidadConDatos = repository.actualizarOportunidad(id, request)

    suspend fun cambiarEstado(id: Int, usuarioId: Int, estado: String): OportunidadConDatos =
        repository.cambiarEstado(id, usuarioId, estado)

    suspend fun asignar(id: Int, usuarioId: Int, ejecutivoId: Int): OportunidadConDatos =
        repository.asignar(id, usuarioId, ejecutivoId)

    // Organizaciones y contactos
    suspend fun organizaciones(): List<OrganizacionResumen> = repository.organizaciones()

    suspend fun organizacion(id: Int): Organizacion = repository.organizacion(id)

    suspend fun contactos(organizacionId: Int): List<Contacto> =
        repository.contactos(organizacionId)

    suspend fun crearOrganizacion(request: OrganizacionRequest): Organizacion =
        repository.crearOrganizacion(request)

    suspend fun crearContacto(organizacionId: Int, request: ContactoRequest): Contacto =
        repository.crearContacto(organizacionId, request)
}
