package com.example.activacorp.model

// No es una tabla: es el resultado de unir Oportunidad con Organizacion y Usuario
data class OportunidadConDatos(
    val id: Int,
    val nombre: String,
    val tipoEvento: String,
    val fechaEvento: String,
    val ubicacion: String,
    val comuna: String,
    val publicoEsperado: Int,
    val prioridad: String,
    val estado: String,
    val observaciones: String,
    val fotoUri: String?,
    val organizacionId: Int,
    val organizacionNombre: String,
    val responsableId: Int?,
    val responsableNombre: String?
)
