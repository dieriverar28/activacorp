package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.ContactoRepository
import com.example.activacorp.model.Contacto
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarCorreo
import com.example.activacorp.util.validarNombre
import com.example.activacorp.util.validarTelefono
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrearContactoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // La organización llega desde la navegación (ruta "crear_contacto/{organizacionId}")
    private val organizacionId: Int = savedStateHandle.get<Int>("organizacionId") ?: 0

    private val database = AppDatabase.getInstance(application)
    private val repository = ContactoRepository(database.contactoDao())

    private val _uiState = MutableStateFlow(CrearContactoUiState())
    val uiState: StateFlow<CrearContactoUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = false) }
    }

    fun cambiarCargo(valor: String) {
        _uiState.update { it.copy(cargo = valor, errorCargo = false) }
    }

    fun cambiarCorreo(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = false, errorCorreoDuplicado = false) }
    }

    fun cambiarTelefono(valor: String) {
        _uiState.update { it.copy(telefono = valor, errorTelefono = false) }
    }

    // onGuardado se ejecuta cuando el contacto ya quedó guardado
    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value
        val correo = estado.correo.trim()

        val errorNombre = !validarNombre(estado.nombre)
        val errorCargo = !validarCampoObligatorio(estado.cargo)
        val errorCorreo = !validarCorreo(correo)
        val errorTelefono = !validarTelefono(estado.telefono)

        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorCargo = errorCargo,
                errorCorreo = errorCorreo,
                errorTelefono = errorTelefono
            )
        }

        if (errorNombre || errorCargo || errorCorreo || errorTelefono) {
            return
        }

        viewModelScope.launch {
            // Regla de negocio: no se repite el correo dentro de la misma organización
            if (repository.existeCorreo(organizacionId, correo)) {
                _uiState.update { it.copy(errorCorreoDuplicado = true) }
            } else {
                repository.crear(
                    Contacto(
                        organizacionId = organizacionId,
                        nombre = estado.nombre.trim(),
                        cargo = estado.cargo.trim(),
                        correo = correo,
                        telefono = estado.telefono.trim()
                    )
                )
                _uiState.value = CrearContactoUiState()
                onGuardado()
            }
        }
    }
}
