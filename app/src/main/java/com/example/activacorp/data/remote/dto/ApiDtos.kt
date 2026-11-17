package com.example.activacorp.data.remote.dto

/**
 * DTO = espejo del JSON que devuelve la API.
 * No son los modelos que usa la UI: aquí la API anida objetos completos
 * (organizacion, contacto, responsable, usuario), por eso se convierten
 * con los mappers de ApiMappers.kt.
 *
 * Los ids llegan como Long en la API, pero Gson los lee como Int sin problema
 * porque los valores son pequeños.
 */

data class UsuarioDto(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String
)

data class OrganizacionDto(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val comuna: String
)

data class OrganizacionResumenDto(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val comuna: String,
    val totalContactos: Int
)

data class ContactoDto(
    val id: Int,
    val organizacion: OrganizacionDto?,
    val nombre: String,
    val cargo: String,
    val correo: String,
    val telefono: String
)

data class OportunidadDto(
    val id: Int,
    val nombre: String,
    val tipoEvento: String,
    // Formato AAAA-MM-DD (Jackson serializa LocalDate como texto)
    val fechaEvento: String,
    val ubicacion: String,
    val comuna: String,
    val publicoEsperado: Int,
    val prioridad: String,   // ALTA | MEDIA | BAJA
    val estado: String,      // EVENTO_IDENTIFICADO | ... | OPORTUNIDAD_DESCARTADA
    val observaciones: String?,
    val organizacion: OrganizacionDto?,
    val contacto: ContactoDto?,
    val responsable: UsuarioDto?,
    val creadoPor: UsuarioDto?,
    val fechaCreacion: String?,
    val fotoUri: String?
)

data class SeguimientoDto(
    val id: Int,
    val usuario: UsuarioDto?,
    // Formato ISO: 2026-11-16T20:09:09
    val fecha: String,
    val accion: String,
    val descripcion: String,
    val estadoAnterior: String?,
    val estadoNuevo: String?,
    val proximaFecha: String?
)
