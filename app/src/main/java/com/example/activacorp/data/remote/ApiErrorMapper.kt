package com.example.activacorp.data.remote

import com.example.activacorp.domain.model.ErrorAplicacion
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Traduce cualquier excepción a ErrorAplicacion.
 *
 * La API tiene server.error.include-message=always, así que los errores de negocio
 * (409 transición no permitida, 403 sin permiso, 400 datos inválidos...) vienen
 * en el JSON con el campo "message". Si existe, se usa tal cual.
 */
fun traducirError(error: Exception): ErrorAplicacion {

    if (error is ErrorAplicacion) return error

    return when (error) {

        is HttpException -> {
            val codigo = error.code()
            val mensajeApi = mensajeDelCuerpo(error)

            val mensaje = mensajeApi ?: when (codigo) {
                400 -> "Los datos enviados no son válidos."
                401 -> "Correo o contraseña incorrectos."
                403 -> "No tienes permisos para esta operación."
                404 -> "El recurso solicitado no existe."
                409 -> "La operación genera un conflicto."
                422 -> "El servidor rechazó los datos."
                in 500..599 -> "Error interno del servidor."
                else -> "Error HTTP $codigo."
            }

            ErrorAplicacion(codigo, mensaje)
        }

        // Ojo: SocketTimeoutException y ConnectException heredan de IOException,
        // por eso van antes.
        is SocketTimeoutException -> ErrorAplicacion(
            null,
            "El servidor tardó demasiado en responder."
        )

        is ConnectException -> ErrorAplicacion(
            null,
            "No se pudo conectar con la API. Verifica que esté corriendo en el puerto 8082."
        )

        is IOException -> ErrorAplicacion(
            null,
            "No fue posible comunicarse con el servidor."
        )

        else -> ErrorAplicacion(
            null,
            error.message ?: "Ocurrió un error inesperado."
        )
    }
}

// El body de error de Spring Boot es: {"timestamp":..., "status":400, "error":"...", "message":"...", "path":"..."}
private fun mensajeDelCuerpo(error: HttpException): String? {
    return try {
        val cuerpo = error.response()?.errorBody()?.string()
        if (cuerpo.isNullOrBlank()) {
            null
        } else {
            JsonParser.parseString(cuerpo)
                .asJsonObject
                .get("message")
                ?.asString
                ?.takeIf { it.isNotBlank() }
        }
    } catch (e: Exception) {
        null
    }
}
