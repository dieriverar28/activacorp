package com.example.activacorp.pantallas.contacto

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
    var errorTelefono: Boolean = false,

    // Mensaje de error de red o de la API
    var mensajeError: String? = null
)
