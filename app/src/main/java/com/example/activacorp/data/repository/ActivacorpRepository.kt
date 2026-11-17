package com.example.activacorp.data.repository

import com.example.activacorp.data.model.AsignacionRequest
import com.example.activacorp.data.model.CambioEstadoRequest
import com.example.activacorp.data.model.ContactoRequest
import com.example.activacorp.data.model.LoginRequest
import com.example.activacorp.data.model.OportunidadRequest
import com.example.activacorp.data.model.OrganizacionRequest
import com.example.activacorp.data.remote.ActivacorpApi
import com.example.activacorp.data.remote.RetrofitClient
import com.example.activacorp.data.remote.aModelo
import com.example.activacorp.data.remote.traducirError
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.OrganizacionResumen
import com.example.activacorp.model.SeguimientoDetalle
import com.example.activacorp.model.Usuario
import kotlinx.coroutines.CancellationException

/**
 * Única puerta de entrada a la API.
 * Los ViewModels NUNCA llaman a Retrofit directamente: llaman al repositorio,
 * que devuelve modelos de la app y errores ya traducidos.
 */
class ActivacorpRepository(
    private val api: ActivacorpApi = RetrofitClient.api
) {

    // ----- Login y usuarios -----
    suspend fun iniciarSesion(correo: String, clave: String): Usuario = ejecutar {
        api.login(LoginRequest(correo, clave)).aModelo()
    }

    suspend fun ejecutivos(): List<Usuario> = ejecutar {
        api.ejecutivos().map { it.aModelo() }
    }

    // ----- Oportunidades -----
    suspend fun oportunidades(usuarioId: Int): List<OportunidadConDatos> = ejecutar {
        api.oportunidades(usuarioId).map { it.aModelo() }
    }

    suspend fun oportunidad(id: Int): OportunidadConDatos = ejecutar {
        api.oportunidad(id).aModelo()
    }

    suspend fun seguimientos(id: Int): List<SeguimientoDetalle> = ejecutar {
        api.seguimientos(id).map { it.aModelo() }
    }

    suspend fun crearOportunidad(request: OportunidadRequest): OportunidadConDatos = ejecutar {
        api.crearOportunidad(request).aModelo()
    }

    suspend fun actualizarOportunidad(
        id: Int,
        request: OportunidadRequest
    ): OportunidadConDatos = ejecutar {
        api.actualizarOportunidad(id, request).aModelo()
    }

    suspend fun cambiarEstado(id: Int, usuarioId: Int, estado: String): OportunidadConDatos =
        ejecutar {
            api.cambiarEstado(id, CambioEstadoRequest(usuarioId, estado)).aModelo()
        }

    suspend fun asignar(id: Int, usuarioId: Int, ejecutivoId: Int): OportunidadConDatos =
        ejecutar {
            api.asignar(id, AsignacionRequest(usuarioId, ejecutivoId)).aModelo()
        }

    // ----- Organizaciones y contactos -----
    suspend fun organizaciones(): List<OrganizacionResumen> = ejecutar {
        api.organizaciones().map { it.aModelo() }
    }

    suspend fun organizacion(id: Int): Organizacion = ejecutar {
        api.organizacion(id).aModelo()
    }

    suspend fun contactos(organizacionId: Int): List<Contacto> = ejecutar {
        api.contactos(organizacionId).map { it.aModelo() }
    }

    suspend fun crearOrganizacion(request: OrganizacionRequest): Organizacion = ejecutar {
        api.crearOrganizacion(request).aModelo()
    }

    suspend fun crearContacto(organizacionId: Int, request: ContactoRequest): Contacto =
        ejecutar {
            api.crearContacto(organizacionId, request).aModelo()
        }

    // No se "traga" la cancelación: si la pantalla se cierra, la corrutina debe cortarse.
    private suspend fun <T> ejecutar(operacion: suspend () -> T): T {
        return try {
            operacion()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw traducirError(e)
        }
    }
}
