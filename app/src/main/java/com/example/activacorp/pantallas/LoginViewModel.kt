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

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = UsuarioRepository(database.usuarioDao())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Carga las cuentas y datos ficticios la primera vez que se abre la app
        viewModelScope.launch {
            cargarDatosIniciales(database)
        }
    }

    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(correo = nuevoCorreo, errorCorreo = false, errorCredenciales = false)
        }
    }

    fun cambiarClave(nuevaClave: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(clave = nuevaClave, errorClave = false, errorCredenciales = false)
        }
    }

    // onExito se ejecuta solo si las credenciales son correctas
    fun iniciarSesion(onExito: () -> Unit) {
        val estado = _uiState.value
        val correo = estado.correo.trim().lowercase()

        val errorCorreo = !validarCorreo(correo)
        val errorClave = !validarCampoObligatorio(estado.clave)

        _uiState.update { estadoActual ->
            estadoActual.copy(errorCorreo = errorCorreo, errorClave = errorClave)
        }

        if (errorCorreo || errorClave) {
            return
        }

        viewModelScope.launch {
            _uiState.update { estadoActual -> estadoActual.copy(cargando = true) }

            val usuario = repository.iniciarSesion(correo, estado.clave)

            if (usuario == null) {
                _uiState.update { estadoActual ->
                    estadoActual.copy(cargando = false, errorCredenciales = true)
                }
            } else {
                Sesion.iniciar(usuario)
                _uiState.value = LoginUiState()
                onExito()
            }
        }
    }
}
