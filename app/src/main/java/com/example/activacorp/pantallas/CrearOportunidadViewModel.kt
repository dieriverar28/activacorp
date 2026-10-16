package com.example.activacorp.pantallas

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OportunidadRepository
import com.example.activacorp.model.Oportunidad
import com.example.activacorp.util.guardarImagenInterna
import com.example.activacorp.util.validarComuna
import com.example.activacorp.util.validarNombre
import com.example.activacorp.util.validarOrganizacion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CrearOportunidadViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(database.oportunidadDao())

    private val _uiState = MutableStateFlow(CrearOportunidadUiState())
    val uiState: StateFlow<CrearOportunidadUiState> = _uiState.asStateFlow()

    // Funciones para actualizar las variables del formulario
    fun cambiarNombre(nuevoNombre: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(nombre = nuevoNombre)
        }
    }

    fun cambiarOrganizacion(nuevaOrganizacion: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(organizacion = nuevaOrganizacion)
        }
    }

    fun cambiarComuna(nuevaComuna: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(comuna = nuevaComuna)
        }
    }

    // Se llama cuando el usuario elige una foto de la galería
    fun cambiarImagen(uri: Uri) {
        viewModelScope.launch {
            // Copiar el archivo es trabajo de disco: se hace fuera del hilo principal
            val ruta = withContext(Dispatchers.IO) {
                guardarImagenInterna(getApplication<Application>(), uri)
            }
            if (ruta != null) {
                _uiState.update { estadoActual ->
                    estadoActual.copy(imagenUri = ruta)
                }
            }
        }
    }

    // Acción del formulario
    // onGuardado se ejecuta cuando la oportunidad ya quedó guardada (la pantalla lo usa para volver atrás)
    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value

        val errorNombre = !validarNombre(estado.nombre)
        val errorOrganizacion = !validarOrganizacion(estado.organizacion)
        val errorComuna = !validarComuna(estado.comuna)

        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorOrganizacion = errorOrganizacion,
                errorComuna = errorComuna
            )
        }

        val formularioValido =
            !errorNombre &&
                    !errorOrganizacion &&
                    !errorComuna

        if (formularioValido) {
            val oportunidad = Oportunidad(
                nombre = estado.nombre,
                organizacion = estado.organizacion,
                comuna = estado.comuna,
                imagenUri = estado.imagenUri
            )
            viewModelScope.launch {
                repository.guardar(oportunidad)
                println("Oportunidad guardada correctamente")
                _uiState.value = CrearOportunidadUiState()
                onGuardado()
            }
        }
    }
}