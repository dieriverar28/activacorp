package com.example.activacorp.pantallas.oportunidad

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.model.OportunidadRequest
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Organizacion
import com.example.activacorp.model.Prioridad
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.guardarImagenInterna
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarFecha
import com.example.activacorp.util.validarFechaEvento
import com.example.activacorp.util.validarNombre
import com.example.activacorp.util.validarPublicoEsperado
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Crea oportunidades con POST /oportunidades.
 * La API exige usuarioId (ejecutivo), organización existente y fecha no pasada.
 * El estado inicial y el responsable los pone la API, no la app.
 */
class CrearOportunidadViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val casos = ActivacorpUseCases()

    private val _uiState = MutableStateFlow(CrearOportunidadUiState())
    val uiState: StateFlow<CrearOportunidadUiState> = _uiState.asStateFlow()

    private val _organizaciones = MutableStateFlow<List<Organizacion>>(emptyList())
    val organizaciones: StateFlow<List<Organizacion>> = _organizaciones.asStateFlow()

    init {
        cargarOrganizaciones()
    }

    fun recargarOrganizaciones() {
        cargarOrganizaciones()
    }

    private fun cargarOrganizaciones() {
        viewModelScope.launch {
            try {
                _organizaciones.value = casos.organizaciones().map { resumen ->
                    Organizacion(
                        id = resumen.id,
                        nombre = resumen.nombre,
                        tipo = resumen.tipo,
                        comuna = resumen.comuna
                    )
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        mensajeError = e.message
                            ?: "No se pudieron cargar las organizaciones."
                    )
                }
            }
        }
    }

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

        _uiState.update {
            it.copy(
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

        val formularioValido = !errorNombre && !errorTipoEvento && !errorFechaEvento &&
                !errorUbicacion && !errorComuna && !errorPublicoEsperado && !errorOrganizacion

        if (!formularioValido) return

        val usuario = Sesion.usuario.value ?: return
        val organizacionId = estado.organizacionId ?: return
        val publicoEsperado = estado.publicoEsperado.toIntOrNull() ?: return

        val solicitud = OportunidadRequest(
            usuarioId = usuario.id,
            nombre = estado.nombre.trim(),
            tipoEvento = estado.tipoEvento.trim(),
            fechaEvento = estado.fechaEvento.trim(),
            ubicacion = estado.ubicacion.trim(),
            comuna = estado.comuna.trim(),
            publicoEsperado = publicoEsperado,
            prioridad = estado.prioridad.name,
            observaciones = estado.observaciones.trim(),
            organizacionId = organizacionId,
            contactoId = null, // El formulario actual todavía no elige contacto
            fotoUri = estado.fotoUri
        )

        viewModelScope.launch {
            try {
                casos.crearOportunidad(solicitud)
                _uiState.value = CrearOportunidadUiState()
                onGuardado()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                // Aquí se ven los 403 ("Solo los ejecutivos...") y 400 de la API
                _uiState.update {
                    it.copy(
                        mensajeError = e.message
                            ?: "No se pudo registrar la oportunidad."
                    )
                }
            }
        }
    }
}
