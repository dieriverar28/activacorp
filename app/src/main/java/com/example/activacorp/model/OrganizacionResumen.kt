package com.example.activacorp.model

// No es una tabla: es la organización junto con la cantidad de contactos que tiene
data class OrganizacionResumen(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val comuna: String,
    val totalContactos: Int
)
