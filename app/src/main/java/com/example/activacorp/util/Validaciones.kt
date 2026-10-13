package com.example.activacorp.util

fun validarNombre(nombre: String): Boolean {
    return nombre.isNotBlank()
}

fun validarOrganizacion(organizacion: String): Boolean {
    return organizacion.isNotBlank()
}

fun validarComuna(comuna: String): Boolean {
    return comuna.isNotBlank()
}
