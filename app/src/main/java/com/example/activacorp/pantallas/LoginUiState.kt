package com.example.activacorp.pantallas

data class LoginUiState(
    // Variables
    var correo: String = "",
    var clave: String = "",

    // Estados de error
    var errorCorreo: Boolean = false,
    var errorClave: Boolean = false,
    var errorCredenciales: Boolean = false,

    // Indicador de carga
    var cargando: Boolean = false
)
