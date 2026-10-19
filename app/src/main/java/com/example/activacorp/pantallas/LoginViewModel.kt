package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.UsuarioRepository
import com.example.activacorp.data.cargarDatosIniciales
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarCorreo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * VIEWMODEL: LoginViewModel (Lógica de Autenticación)
 * Gestiona el proceso de inicio de sesión, la validación de formato de credenciales,
 * la consulta asíncrona de usuarios en la base de datos local (Room) y la inicialización de la sesión activa.
 */
class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Instancia de la base de datos Room y del repositorio de usuarios
    private val database = AppDatabase.getInstance(application)
    private val repository = UsuarioRepository(database.usuarioDao())

    // Estado privado mutable (StateFlow)
    private val _uiState = MutableStateFlow(LoginUiState())
    // Estado inmutable expuesto a la vista de Jetpack Compose
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Precarga de semillas de datos (cuentas de prueba y catálogo inicial) al instanciar el ViewModel
        viewModelScope.launch {
            cargarDatosIniciales(database)
        }
    }

    /**
     * Actualiza el correo ingresado y resetea las alertas visuales de error.
     */
    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(correo = nuevoCorreo, errorCorreo = false, errorCredenciales = false)
        }
    }

    /**
     * Actualiza la contraseña ingresada y resetea los indicadores de error.
     */
    fun cambiarClave(nuevaClave: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(clave = nuevaClave, errorClave = false, errorCredenciales = false)
        }
    }

    /**
     * Valida los campos de entrada y autentica al usuario contra Room.
     * Si la verificación es exitosa, inicializa la Sesión Singleton y ejecuta la navegación.
     */
    fun iniciarSesion(onExito: () -> Unit) {
        val estado = _uiState.value
        val correo = estado.correo.trim().lowercase()

        // Ejecución de utilidades de validación de sintaxis y presencia de datos
        val errorCorreo = !validarCorreo(correo)
        val errorClave = !validarCampoObligatorio(estado.clave)

        // Refleja los errores de formato en la interfaz gráfica
        _uiState.update { estadoActual ->
            estadoActual.copy(errorCorreo = errorCorreo, errorClave = errorClave)
        }

        // Cancela la operación si no se superan las reglas básicas de formulario
        if (errorCorreo || errorClave) {
            return
        }

        // Búsqueda asíncrona en base de datos usando Corrutinas
        viewModelScope.launch {
            _uiState.update { estadoActual -> estadoActual.copy(cargando = true) }

            val usuario = repository.iniciarSesion(correo, estado.clave)

            if (usuario == null) {
                // Notifica credenciales erróneas o inexistentes
                _uiState.update { estadoActual ->
                    estadoActual.copy(cargando = false, errorCredenciales = true)
                }
            } else {
                // Registro global del usuario en el objeto Sesion Singleton
                Sesion.iniciar(usuario)
                _uiState.value = LoginUiState()
                // Callback de navegación al Dashboard
                onExito()
            }
        }
    }
}