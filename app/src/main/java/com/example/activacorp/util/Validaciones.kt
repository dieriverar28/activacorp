package com.example.activacorp.util

import android.util.Patterns
import java.time.LocalDate
import java.time.format.DateTimeParseException

fun validarCampoObligatorio(texto: String): Boolean {
    return texto.isNotBlank()
}

fun validarNombre(nombre: String): Boolean {
    return nombre.trim().length >= 3
}

fun validarCorreo(correo: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(correo).matches()
}

// La fecha debe tener formato AAAA-MM-DD y existir en el calendario
fun validarFecha(fecha: String): Boolean {
    return try {
        LocalDate.parse(fecha)
        true
    } catch (e: DateTimeParseException) {
        false
    }
}

// Regla de negocio: el evento no puede estar en el pasado
fun validarFechaEvento(fecha: String): Boolean {
    return try {
        !LocalDate.parse(fecha).isBefore(LocalDate.now())
    } catch (e: DateTimeParseException) {
        false
    }
}

// Debe ser un número entero mayor que cero
fun validarPublicoEsperado(texto: String): Boolean {
    val numero = texto.toIntOrNull()
    return numero != null && numero > 0
}

// Acepta números como +56 9 5550 0001 o 955500001 (entre 8 y 12 dígitos, con + opcional al inicio)
fun validarTelefono(telefono: String): Boolean {
    val limpio = telefono.replace(" ", "").replace("-", "")
    return Regex("^\\+?[0-9]{8,12}$").matches(limpio)
}
