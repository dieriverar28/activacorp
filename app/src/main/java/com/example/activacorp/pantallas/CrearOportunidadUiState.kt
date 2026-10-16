package com.example.activacorp.pantallas

import com.example.activacorp.model.Prioridad

data class CrearOportunidadUiState(
    // Variables
    var nombre: String = "",
    var tipoEvento: String = "",
    var fechaEvento: String = "",
    var ubicacion: String = "",
    var comuna: String = "",
    var publicoEsperado: String = "",
    var observaciones: String = "",
    var prioridad: Prioridad = Prioridad.MEDIA,
    var organizacionId: Int? = null,
    // URI de la foto ya copiada al almacenamiento interno (opcional)
    var fotoUri: String? = null,

    // Estados de error
    var errorNombre: Boolean = false,
    var errorTipoEvento: Boolean = false,
    var errorFechaEvento: Boolean = false,
    var errorUbicacion: Boolean = false,
    var errorComuna: Boolean = false,
    var errorPublicoEsperado: Boolean = false,
    var errorOrganizacion: Boolean = false,
    var errorImagen: Boolean = false,

    // Mensaje de error de la operación
    var mensajeError: String? = null
)
