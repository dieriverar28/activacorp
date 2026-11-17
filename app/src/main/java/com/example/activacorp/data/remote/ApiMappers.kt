package com.example.activacorp.data.remote

import com.example.activacorp.data.remote.dto.ContactoDto
import com.example.activacorp.data.remote.dto.OportunidadDto
import com.example.activacorp.data.remote.dto.OrganizacionDto
import com.example.activacorp.data.remote.dto.OrganizacionResumenDto
import com.example.activacorp.data.remote.dto.SeguimientoDto
import com.example.activacorp.data.remote.dto.UsuarioDto
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.OrganizacionResumen
import com.example.activacorp.model.SeguimientoDetalle
import com.example.activacorp.model.Usuario
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// Convierte el JSON anidado de la API a los modelos que ya usan las pantallas.
// Así no hay que reescribir la UI: se cambia solo el origen de los datos.

fun UsuarioDto.aModelo(): Usuario = Usuario(
    id = id,
    nombre = nombre,
    correo = correo,
    // La API nunca devuelve la clave (WRITE_ONLY): ya no se necesita en la app.
    clave = "",
    rol = rol
)

fun OportunidadDto.aModelo(): OportunidadConDatos = OportunidadConDatos(
    id = id,
    nombre = nombre,
    tipoEvento = tipoEvento,
    fechaEvento = fechaEvento,
    ubicacion = ubicacion,
    comuna = comuna,
    publicoEsperado = publicoEsperado,
    prioridad = prioridad,
    estado = estado,
    observaciones = observaciones.orEmpty(),
    fotoUri = fotoUri,
    organizacionId = organizacion?.id ?: 0,
    organizacionNombre = organizacion?.nombre ?: "",
    responsableId = responsable?.id,
    responsableNombre = responsable?.nombre
)

fun SeguimientoDto.aModelo(): SeguimientoDetalle = SeguimientoDetalle(
    id = id,
    fecha = fecha.aFechaHoraLegible(),
    accion = accion,
    descripcion = descripcion,
    estadoAnterior = estadoAnterior,
    estadoNuevo = estadoNuevo,
    usuarioNombre = usuario?.nombre ?: ""
)

fun OrganizacionDto.aModelo(): Organizacion = Organizacion(
    id = id,
    nombre = nombre,
    tipo = tipo,
    comuna = comuna
)

fun OrganizacionResumenDto.aModelo(): OrganizacionResumen = OrganizacionResumen(
    id = id,
    nombre = nombre,
    tipo = tipo,
    comuna = comuna,
    totalContactos = totalContactos
)

fun ContactoDto.aModelo(): Contacto = Contacto(
    id = id,
    organizacionId = organizacion?.id ?: 0,
    nombre = nombre,
    cargo = cargo,
    correo = correo,
    telefono = telefono
)

// La API manda LocalDateTime como 2026-11-16T20:09:09;
// la app muestra AAAA-MM-DD HH:mm (mismo formato de util/Fechas.kt).
private fun String.aFechaHoraLegible(): String {
    return try {
        LocalDateTime.parse(this)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    } catch (e: DateTimeParseException) {
        this
    }
}
