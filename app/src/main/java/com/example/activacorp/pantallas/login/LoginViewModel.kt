package com.example.activacorp.pantallas.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.domain.model.ErrorAplicacion
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarCorreo
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Autenticación contra la API (POST /usuarios/login).
 * La API responde 200 con el usuario (sin clave) o 401 si las credenciales no coinciden.
 */
class LoginViewModel(
    private val casos: ActivacorpUseCases = ActivacorpUseCases()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun cambiarCorreo(nuevoCorreo: String) {
        _uiState.update { estado ->
            estado.copy(
                correo = nuevoCorreo,
                errorCorreo = false,
                errorCredenciales = false,
                mensajeError = null
            )
        }
    }

    fun cambiarClave(nuevaClave: String) {
        _uiState.update { estado ->
            estado.copy(
                clave = nuevaClave,
                errorClave = false,
                errorCredenciales = false,
                mensajeError = null
            )
        }
    }

    fun iniciarSesion(onExito: () -> Unit) {
        val estado = _uiState.value
        val correo = estado.correo.trim().lowercase()

        val errorCorreo = !validarCorreo(correo)
        val errorClave = !validarCampoObligatorio(estado.clave)

        _uiState.update {
            it.copy(
                errorCorreo = errorCorreo,
                errorClave = errorClave,
                errorCredenciales = false,
                mensajeError = null
            )
        }

        if (errorCorreo || errorClave) return

        if (estado.cargando) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }

            try {
                val usuario = casos.iniciarSesion(correo, estado.clave)

                Sesion.iniciar(usuario)
                _uiState.value = LoginUiState()
                onExito()

            } catch (e: CancellationException) {
                throw e

            } catch (e: ErrorAplicacion) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        // 401 = credenciales incorrectas; lo demás se muestra como mensaje
                        errorCredenciales = e.codigoHttp == 401,
                        mensajeError = if (e.codigoHttp == 401) null else e.message
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = e.message ?: "No se pudo iniciar sesión."
                    )
                }
            }
        }
    }
}
