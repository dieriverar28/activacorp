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

class CrearOportunidadViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )
    private val organizacionRepository = OrganizacionRepository(database.organizacionDao())

    private val _uiState = MutableStateFlow(CrearOportunidadUiState())
    val uiState: StateFlow<CrearOportunidadUiState> = _uiState.asStateFlow()

    // Organizaciones disponibles para elegir
    val organizaciones: StateFlow<List<Organizacion>> =
        organizacionRepository
            .observarTodas()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Funciones para actualizar las variables del formulario
    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = false) }
    }

    fun cambiarTipoEvento(valor: String) {
        _uiState.update { it.copy(tipoEvento = valor, errorTipoEvento = false) }
    }

    fun cambiarFechaEvento(valor: String) {
        _uiState.update { it.copy(fechaEvento = valor, errorFechaEvento = false) }
    }

    fun cambiarUbicacion(valor: String) {
        _uiState.update { it.copy(ubicacion = valor, errorUbicacion = false) }
    }

    fun cambiarComuna(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = false) }
    }

    fun cambiarPublicoEsperado(valor: String) {
        _uiState.update { it.copy(publicoEsperado = valor, errorPublicoEsperado = false) }
    }

    fun cambiarObservaciones(valor: String) {
        _uiState.update { it.copy(observaciones = valor) }
    }

    fun cambiarPrioridad(valor: Prioridad) {
        _uiState.update { it.copy(prioridad = valor) }
    }

    fun seleccionarOrganizacion(id: Int) {
        _uiState.update { it.copy(organizacionId = id, errorOrganizacion = false) }
    }

    // Recibe la imagen elegida (galería o cámara) y la copia al almacenamiento interno de la app
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

    fun quitarImagen() {
        _uiState.update { it.copy(fotoUri = null, errorImagen = false) }
    }

    // onGuardado se ejecuta cuando la oportunidad ya quedó guardada
    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value

        val errorNombre = !validarNombre(estado.nombre)
        val errorTipoEvento = !validarCampoObligatorio(estado.tipoEvento)
        val errorFechaEvento = !validarFecha(estado.fechaEvento) ||
                !validarFechaEvento(estado.fechaEvento)
        val errorUbicacion = !validarCampoObligatorio(estado.ubicacion)
        val errorComuna = !validarCampoObligatorio(estado.comuna)
        val errorPublicoEsperado = !validarPublicoEsperado(estado.publicoEsperado)
        val errorOrganizacion = estado.organizacionId == null

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

        val usuario = Sesion.usuario.value ?: return
        val organizacionId = estado.organizacionId ?: return
        val publicoEsperado = estado.publicoEsperado.toIntOrNull() ?: return

        // El ejecutivo que registra la oportunidad queda como responsable
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
            responsableId = usuario.id,
            creadoPorId = usuario.id,
            fechaCreacion = hoyTexto(),
            fotoUri = estado.fotoUri
        )

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
