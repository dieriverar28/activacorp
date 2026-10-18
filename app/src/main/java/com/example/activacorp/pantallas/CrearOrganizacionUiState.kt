package com.example.activacorp.pantallas

data class CrearOrganizacionUiState(
    // Variables
    var nombre: String = "",
    var tipo: String = "",
    var comuna: String = "",

    // Estados de error
    var errorNombre: Boolean = false,
    var errorNombreDuplicado: Boolean = false,
    var errorTipo: Boolean = false,
    var errorComuna: Boolean = false
)
