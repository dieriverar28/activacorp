package com.example.activacorp.pantallas

data class CrearContactoUiState(
    // Variables
    var nombre: String = "",
    var cargo: String = "",
    var correo: String = "",
    var telefono: String = "",

    // Estados de error
    var errorNombre: Boolean = false,
    var errorCargo: Boolean = false,
    var errorCorreo: Boolean = false,
    var errorCorreoDuplicado: Boolean = false,
    var errorTelefono: Boolean = false
)
