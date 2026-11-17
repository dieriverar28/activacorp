package com.example.activacorp.data.remote

import com.example.activacorp.data.model.AsignacionRequest
import com.example.activacorp.data.model.CambioEstadoRequest
import com.example.activacorp.data.model.ContactoRequest
import com.example.activacorp.data.model.LoginRequest
import com.example.activacorp.data.model.OportunidadRequest
import com.example.activacorp.data.model.OrganizacionRequest
import com.example.activacorp.data.remote.dto.ContactoDto
import com.example.activacorp.data.remote.dto.OportunidadDto
import com.example.activacorp.data.remote.dto.OrganizacionDto
import com.example.activacorp.data.remote.dto.OrganizacionResumenDto
import com.example.activacorp.data.remote.dto.SeguimientoDto
import com.example.activacorp.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ActivacorpApi {

    // ----- Usuarios / login -----
    @POST("usuarios/login")
    suspend fun login(@Body request: LoginRequest): UsuarioDto

    @GET("usuarios/ejecutivos")
    suspend fun ejecutivos(): List<UsuarioDto>

    // ----- Oportunidades -----
    // El supervisor recibe todas; el ejecutivo solo las suyas.
    @GET("oportunidades")
    suspend fun oportunidades(@Query("usuarioId") usuarioId: Int): List<OportunidadDto>

    @GET("oportunidades/{id}")
    suspend fun oportunidad(@Path("id") id: Int): OportunidadDto

    @GET("oportunidades/{id}/seguimientos")
    suspend fun seguimientos(@Path("id") id: Int): List<SeguimientoDto>

    @POST("oportunidades")
    suspend fun crearOportunidad(@Body request: OportunidadRequest): OportunidadDto

    @PUT("oportunidades/{id}")
    suspend fun actualizarOportunidad(
        @Path("id") id: Int,
        @Body request: OportunidadRequest
    ): OportunidadDto

    @PUT("oportunidades/{id}/estado")
    suspend fun cambiarEstado(
        @Path("id") id: Int,
        @Body request: CambioEstadoRequest
    ): OportunidadDto

    @PUT("oportunidades/{id}/asignacion")
    suspend fun asignar(
        @Path("id") id: Int,
        @Body request: AsignacionRequest
    ): OportunidadDto

    // ----- Organizaciones y contactos -----
    @GET("organizaciones")
    suspend fun organizaciones(): List<OrganizacionResumenDto>

    @GET("organizaciones/{id}")
    suspend fun organizacion(@Path("id") id: Int): OrganizacionDto

    @GET("organizaciones/{id}/contactos")
    suspend fun contactos(@Path("id") id: Int): List<ContactoDto>

    @POST("organizaciones")
    suspend fun crearOrganizacion(@Body request: OrganizacionRequest): OrganizacionDto

    @POST("organizaciones/{id}/contactos")
    suspend fun crearContacto(
        @Path("id") id: Int,
        @Body request: ContactoRequest
    ): ContactoDto
}
