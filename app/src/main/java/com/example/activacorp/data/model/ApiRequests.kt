package com.example.activacorp.data.model

/**
 * Cuerpos (body) que la app envía a la API.
 * Los nombres de las propiedades deben coincidir con los record de Java de la API.
 * Gson no envía los campos null, así que contactoId/fotoUri pueden ser opcionales.
 */

data class LoginRequest(
    val correo: String,
    val clave: String
)

data class OportunidadRequest(
    val usuarioId: Int,
    val nombre: String,
    val tipoEvento: String,
    // AAAA-MM-DD
    val fechaEvento: String,
    val ubicacion: String,
    val comuna: String,
    val publicoEsperado: Int,
    // ALTA | MEDIA | BAJA
    val prioridad: String,
    val observaciones: String?,
    val organizacionId: Int,
    val contactoId: Int? = null,
    val fotoUri: String? = null
)

data class OrganizacionRequest(
    val nombre: String,
    val tipo: String,
    val comuna: String
)

data class ContactoRequest(
    val nombre: String,
    val cargo: String,
    val correo: String,
    val telefono: String
)

data class CambioEstadoRequest(
    val usuarioId: Int,
    // Nombre exacto del enum de la API
    val estado: String
)

data class AsignacionRequest(
    val usuarioId: Int,
    val ejecutivoId: Int
)
