package com.example.activacorp.pantallas

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OportunidadRepository
import com.example.activacorp.data.OrganizacionRepository
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Oportunidad
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.Prioridad
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.guardarImagenInterna
import com.example.activacorp.util.hoyTexto
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarFecha
import com.example.activacorp.util.validarFechaEvento
import com.example.activacorp.util.validarNombre
import com.example.activacorp.util.validarPublicoEsperado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * VIEWMODEL: CrearOportunidadViewModel (Lógica de Negocio y Gestión de Estado)
 * Se encarga de procesar la creación de oportunidades comerciales, interactuar con el almacenamiento
 * de imágenes en disco, ejecutar las validaciones del formulario y persistir los datos en Room.
 */
class CrearOportunidadViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Instancia principal de la base de datos Room y de sus repositorios correspondientes
    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )
    private val organizacionRepository = OrganizacionRepository(database.organizacionDao())

    // Estado UI mutable privado
    private val _uiState = MutableStateFlow(CrearOportunidadUiState())
    // Estado UI inmutable expuesto a la vista de Jetpack Compose
    val uiState: StateFlow<CrearOportunidadUiState> = _uiState.asStateFlow()

    // Consulta reactiva del listado de organizaciones disponibles desde el repositorio local
    val organizaciones: StateFlow<List<Organizacion>> =
        organizacionRepository
            .observarTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // ----- Métodos para actualizar dinámicamente los campos del formulario -----

    /** Actualiza el nombre de la oportunidad y limpia su bandera de error. */
    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = false) }
    }

    /** Actualiza el tipo de evento y limpia su error. */
    fun cambiarTipoEvento(valor: String) {
        _uiState.update { it.copy(tipoEvento = valor, errorTipoEvento = false) }
    }

    /** Actualiza la fecha programada para el evento y limpia la alerta de error. */
    fun cambiarFechaEvento(valor: String) {
        _uiState.update { it.copy(fechaEvento = valor, errorFechaEvento = false) }
    }

    /** Actualiza la dirección o ubicación física del evento. */
    fun cambiarUbicacion(valor: String) {
        _uiState.update { it.copy(ubicacion = valor, errorUbicacion = false) }
    }

    /** Actualiza la comuna asignada. */
    fun cambiarComuna(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = false) }
    }

    /** Actualiza la estimación de público asistente. */
    fun cambiarPublicoEsperado(valor: String) {
        _uiState.update { it.copy(publicoEsperado = valor, errorPublicoEsperado = false) }
    }

    /** Actualiza las observaciones u anotaciones adicionales. */
    fun cambiarObservaciones(valor: String) {
        _uiState.update { it.copy(observaciones = valor) }
    }

    /** Establece el nivel de prioridad seleccionado (Baja, Media, Alta). */
    fun cambiarPrioridad(valor: Prioridad) {
        _uiState.update { it.copy(prioridad = valor) }
    }

    /** Asocia la organización elegida para la oportunidad. */
    fun seleccionarOrganizacion(id: Int) {
        _uiState.update { it.copy(organizacionId = id, errorOrganizacion = false) }
    }

    /**
     * PROCESAMIENTO DE IMAGEN: Copia la imagen seleccionada (Galería/Cámara)
     * al almacenamiento privado interno de la aplicación mediante Corrutinas e I/O.
     */
    fun seleccionarImagen(uri: Uri) {
        viewModelScope.launch {
            val ruta = withContext(Dispatchers.IO) {
                guardarImagenInterna(getApplication<Application>(), uri)
            }

            if (ruta == null) {
                _uiState.update { it.copy(errorImagen = true) }
            } else {
                _uiState.update { it.copy(fotoUri = ruta, errorImagen = false) }
            }
        }
    }

    /** Remueve la ruta de la fotografía del estado del formulario. */
    fun quitarImagen() {
        _uiState.update { it.copy(fotoUri = null, errorImagen = false) }
    }

    /**
     * VALIDACIÓN Y PERSISTENCIA:
     * Evalúa la validez del formulario completo y guarda la oportunidad asignando
     * al usuario actual como responsable inicial.
     */
    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value

        // Evaluación de reglas de validación individuales
        val errorNombre = !validarNombre(estado.nombre)
        val errorTipoEvento = !validarCampoObligatorio(estado.tipoEvento)
        val errorFechaEvento = !validarFecha(estado.fechaEvento) ||
                !validarFechaEvento(estado.fechaEvento)
        val errorUbicacion = !validarCampoObligatorio(estado.ubicacion)
        val errorComuna = !validarCampoObligatorio(estado.comuna)
        val errorPublicoEsperado = !validarPublicoEsperado(estado.publicoEsperado)
        val errorOrganizacion = estado.organizacionId == null

        // Notificación de errores a la UI
        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorTipoEvento = errorTipoEvento,
                errorFechaEvento = errorFechaEvento,
                errorUbicacion = errorUbicacion,
                errorComuna = errorComuna,
                errorPublicoEsperado = errorPublicoEsperado,
                errorOrganizacion = errorOrganizacion,
                mensajeError = null
            )
        }

        val formularioValido =
            !errorNombre &&
                    !errorTipoEvento &&
                    !errorFechaEvento &&
                    !errorUbicacion &&
                    !errorComuna &&
                    !errorPublicoEsperado &&
                    !errorOrganizacion

        if (!formularioValido) {
            return
        }

        // Obtención de la sesión del usuario activo
        val usuario = Sesion.usuario.value ?: return
        val organizacionId = estado.organizacionId ?: return
        val publicoEsperado = estado.publicoEsperado.toIntOrNull() ?: return

        // Construcción del objeto de dominio Oportunidad
        val oportunidad = Oportunidad(
            nombre = estado.nombre.trim(),
            tipoEvento = estado.tipoEvento.trim(),
            fechaEvento = estado.fechaEvento.trim(),
            ubicacion = estado.ubicacion.trim(),
            comuna = estado.comuna.trim(),
            publicoEsperado = publicoEsperado,
            prioridad = estado.prioridad.name,
            estado = EstadoOportunidad.EVENTO_IDENTIFICADO.name,
            observaciones = estado.observaciones.trim(),
            organizacionId = organizacionId,
            responsableId = usuario.id, // El usuario creador se asigna como responsable inicial
            creadoPorId = usuario.id,
            fechaCreacion = hoyTexto(),
            fotoUri = estado.fotoUri
        )

        // Persistencia asíncrona mediante el repositorio
        viewModelScope.launch {
            val exito = repository.crear(oportunidad, usuario)

            if (exito) {
                _uiState.value = CrearOportunidadUiState()
                onGuardado()
            } else {
                _uiState.update { estadoActual ->
                    estadoActual.copy(mensajeError = "No tienes permiso para registrar oportunidades")
                }
            }
        }
    }
}