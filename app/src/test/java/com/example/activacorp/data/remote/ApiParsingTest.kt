package com.example.activacorp.data.remote

import com.example.activacorp.data.remote.dto.OportunidadDto
import com.example.activacorp.data.remote.dto.SeguimientoDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Verifica que el JSON real de la API se pueda leer con los DTO
 * y que el mapper produzca los modelos que usan las pantallas.
 */
class ApiParsingTest {

    private val gson = Gson()

    @Test
    fun `oportunidad anidada se mapea a OportunidadConDatos`() {
        val json = """
            [
              {
                "id": 1,
                "nombre": "Feria de beneficios municipal",
                "tipoEvento": "Feria municipal",
                "fechaEvento": "2027-01-20",
                "ubicacion": "Plaza de Armas",
                "comuna": "Puente Alto",
                "publicoEsperado": 900,
                "prioridad": "MEDIA",
                "estado": "CONTACTO_PENDIENTE",
                "observaciones": "Editada desde la API",
                "organizacion": {
                  "id": 2,
                  "nombre": "Municipalidad de Puente Alto",
                  "tipo": "Publica",
                  "comuna": "Puente Alto"
                },
                "contacto": {
                  "id": 2,
                  "organizacion": {
                    "id": 2,
                    "nombre": "Municipalidad de Puente Alto",
                    "tipo": "Publica",
                    "comuna": "Puente Alto"
                  },
                  "nombre": "Camilo Bravo",
                  "cargo": "Asistente de eventos",
                  "correo": "camilo.bravo@ejemplo.cl",
                  "telefono": "+56 9 5550 0010"
                },
                "responsable": {
                  "id": 1,
                  "nombre": "Camila Rojas",
                  "correo": "camila.rojas@agencia.cl",
                  "rol": "EJECUTIVO"
                },
                "creadoPor": {
                  "id": 1,
                  "nombre": "Camila Rojas",
                  "correo": "camila.rojas@agencia.cl",
                  "rol": "EJECUTIVO"
                },
                "fechaCreacion": "2026-11-16",
                "fotoUri": null
              }
            ]
        """.trimIndent()

        val tipo = object : TypeToken<List<OportunidadDto>>() {}.type
        val dtos: List<OportunidadDto> = gson.fromJson(json, tipo)
        val modelo = dtos.first().aModelo()

        assertEquals(1, modelo.id)
        assertEquals("Municipalidad de Puente Alto", modelo.organizacionNombre)
        assertEquals(2, modelo.organizacionId)
        assertEquals(1, modelo.responsableId)
        assertEquals("Camila Rojas", modelo.responsableNombre)
        assertEquals("CONTACTO_PENDIENTE", modelo.estado)
        assertEquals("MEDIA", modelo.prioridad)
        assertEquals("2027-01-20", modelo.fechaEvento)
    }

    @Test
    fun `oportunidad sin responsable deja los campos en null`() {
        val json = """
            {
              "id": 5,
              "nombre": "Expo Emprende Sur",
              "tipoEvento": "Feria empresarial",
              "fechaEvento": "2027-06-30",
              "ubicacion": "Centro Cultural",
              "comuna": "La Florida",
              "publicoEsperado": 600,
              "prioridad": "ALTA",
              "estado": "EVENTO_IDENTIFICADO",
              "observaciones": null,
              "organizacion": {"id": 2, "nombre": "Camara de Comercio", "tipo": "Gremio", "comuna": "San Bernardo"},
              "contacto": null,
              "responsable": null,
              "creadoPor": {"id": 4, "nombre": "Marcos Fuentes", "correo": "marcos.fuentes@agencia.cl", "rol": "SUPERVISOR"},
              "fechaCreacion": "2026-11-16",
              "fotoUri": null
            }
        """.trimIndent()

        val modelo = gson.fromJson(json, OportunidadDto::class.java).aModelo()

        assertNull(modelo.responsableId)
        assertNull(modelo.responsableNombre)
        assertEquals("", modelo.observaciones)
        assertEquals(5, modelo.id)
    }

    @Test
    fun `seguimiento ISO se formatea como la app`() {
        val json = """
            {
              "id": 9,
              "usuario": {"id": 1, "nombre": "Camila Rojas", "correo": "camila.rojas@agencia.cl", "rol": "EJECUTIVO"},
              "fecha": "2026-11-16T20:09:09.123",
              "accion": "CAMBIO_ESTADO",
              "descripcion": "Estado: Contacto pendiente a Contacto realizado",
              "estadoAnterior": "CONTACTO_PENDIENTE",
              "estadoNuevo": "CONTACTO_REALIZADO",
              "proximaFecha": null
            }
        """.trimIndent()

        val modelo = gson.fromJson(json, SeguimientoDto::class.java).aModelo()

        assertEquals("2026-11-16 20:09", modelo.fecha)
        assertEquals("Camila Rojas", modelo.usuarioNombre)
        assertEquals("CONTACTO_REALIZADO", modelo.estadoNuevo)
    }
}
