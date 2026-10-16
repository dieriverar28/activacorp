package com.example.activacorp.pantallas

data class CrearOportunidadUiState(
    // Variables
    var nombre: String = "",
    var organizacion: String = "",
    var comuna: String = "",
    var imagenUri: String?=null,

    // Estados de error
    var errorNombre: Boolean = false,
    var errorOrganizacion: Boolean = false,
    var errorComuna: Boolean = false
)
