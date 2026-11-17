package com.example.activacorp.data.remote

import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ApiErrorMapperTest {

    private fun httpError(codigo: Int, cuerpo: String): HttpException {
        return HttpException(
            Response.error<Unit>(
                codigo,
                ResponseBody.create(MediaType.parse("application/json"), cuerpo)
            )
        )
    }

    @Test
    fun `409 usa el mensaje de negocio que manda la API`() {
        val cuerpo = """
            {"timestamp":"2026-11-17T00:23:44.743Z","status":409,"error":"Conflict",
             "message":"Ya existe una organización con ese nombre","path":"/organizaciones"}
        """.trimIndent()

        val error = traducirError(httpError(409, cuerpo))

        assertEquals(409, error.codigoHttp)
        assertEquals("Ya existe una organización con ese nombre", error.message)
    }

    @Test
    fun `403 sin cuerpo usa el mensaje por defecto`() {
        val error = traducirError(httpError(403, ""))

        assertEquals(403, error.codigoHttp)
        assertEquals("No tienes permisos para esta operación.", error.message)
    }

    @Test
    fun `401 de login con cuerpo vacio usa credenciales incorrectas`() {
        val error = traducirError(httpError(401, ""))

        assertEquals(401, error.codigoHttp)
        assertEquals("Correo o contraseña incorrectos.", error.message)
    }

    @Test
    fun `error de conexion no tiene codigo HTTP`() {
        val error = traducirError(java.net.ConnectException("Connection refused"))

        assertNull(error.codigoHttp)
        assertEquals(
            "No se pudo conectar con la API. Verifica que esté corriendo en el puerto 8082.",
            error.message
        )
    }
}
