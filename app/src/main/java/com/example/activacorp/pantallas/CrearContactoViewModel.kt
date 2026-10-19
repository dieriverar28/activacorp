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

/**
 * VIEWMODEL: CrearContactoViewModel (Lógica de Negocio y Estado de la UI)
 * Gestiona el formulario de creación de contactos, sus validaciones de campos,
 * control de errores en tiempo real y la persistencia en la base de datos local (Room).
 */
class CrearContactoViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle // Permite recuperar argumentos pasados por la ruta de navegación
) : AndroidViewModel(application) {

    // Identificador de la organización asociada, recibido como parámetro desde la navegación
    private val organizacionId: Int = savedStateHandle.get<Int>("organizacionId") ?: 0

    // Inicialización de la base de datos Room y del repositorio de contactos
    private val database = AppDatabase.getInstance(application)
    private val repository = ContactoRepository(database.contactoDao())

    // Estado interno mutable de la pantalla (StateFlow)
    private val _uiState = MutableStateFlow(CrearContactoUiState())
    // Estado expuesto de solo lectura para ser consumido por la UI en Compose
    val uiState: StateFlow<CrearContactoUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el nombre ingresado y limpia el estado de error previo del campo.
     */
    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = false) }
    }

    /**
     * Actualiza el cargo o área ingresada y limpia el estado de error previo.
     */
    fun cambiarCargo(valor: String) {
        _uiState.update { it.copy(cargo = valor, errorCargo = false) }
    }

    /**
     * Actualiza el correo electrónico y restablece las alertas de error de formato y duplicidad.
     */
    fun cambiarCorreo(valor: String) {
        _uiState.update { it.copy(correo = valor, errorCorreo = false, errorCorreoDuplicado = false) }
    }

    /**
     * Actualiza el teléfono ingresado y limpia el estado de error previo.
     */
    fun cambiarTelefono(valor: String) {
        _uiState.update { it.copy(telefono = valor, errorTelefono = false) }
    }

    /**
     * Procesa y valida el formulario completo. Si todas las validaciones son correctas,
     * guarda el contacto en Room y ejecuta el callback de finalización.
     */
    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value
        val correo = estado.correo.trim()

        // Ejecución de funciones utilitarias de validación para cada campo
        val errorNombre = !validarNombre(estado.nombre)
        val errorCargo = !validarCampoObligatorio(estado.cargo)
        val errorCorreo = !validarCorreo(correo)
        val errorTelefono = !validarTelefono(estado.telefono)

        // Se actualizan los flags de error en el estado UI para mostrarlos en la pantalla
        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorCargo = errorCargo,
                errorCorreo = errorCorreo,
                errorTelefono = errorTelefono
            )
        }

        // Si existe algún error de entrada, se detiene la ejecución para no guardar datos inválidos
        if (errorNombre || errorCargo || errorCorreo || errorTelefono) {
            return
        }

        // Ejecución en segundo plano dentro del scope del ViewModel mediante Corrutinas
        viewModelScope.launch {
            // Regla de negocio: Verificar que el correo no esté duplicado en la misma organización
            if (repository.existeCorreo(organizacionId, correo)) {
                _uiState.update { it.copy(errorCorreoDuplicado = true) }
            } else {
                // Inserción de la nueva entidad Contacto a través del repositorio
                repository.crear(
                    Contacto(
                        organizacionId = organizacionId,
                        nombre = estado.nombre.trim(),
                        cargo = estado.cargo.trim(),
                        correo = correo,
                        telefono = estado.telefono.trim()
                    )
                )
                // Restablecer el formulario a su estado inicial
                _uiState.value = CrearContactoUiState()
                // Notificar a la UI para realizar la navegación de retorno
                onGuardado()
            }
        }
    }
}