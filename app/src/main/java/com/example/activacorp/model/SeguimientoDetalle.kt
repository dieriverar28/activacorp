package com.example.activacorp.model

// No es una tabla: es el resultado de unir Seguimiento con Usuario
data class SeguimientoDetalle(
    val id: Int,
    val fecha: String,
    val accion: String,
    val descripcion: String,
    val estadoAnterior: String?,
    val estadoNuevo: String?,
    val usuarioNombre: String
)
