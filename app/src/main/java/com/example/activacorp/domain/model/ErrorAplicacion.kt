package com.example.activacorp.domain.model

/**
 * Error de negocio/red ya "traducido" a un mensaje que se puede mostrar al usuario.
 * codigoHttp es null cuando el problema no viene de una respuesta HTTP
 * (por ejemplo, la API está apagada o se agotó el tiempo de espera).
 */
class ErrorAplicacion(
    val codigoHttp: Int?,
    override val message: String
) : Exception(message)
