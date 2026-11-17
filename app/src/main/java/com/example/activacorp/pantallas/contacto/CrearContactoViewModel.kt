package com.example.activacorp.pantallas.contacto

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.model.ContactoRequest
import com.example.activacorp.domain.model.ErrorAplicacion
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarCorreo
import com.example.activacorp.util.validarNombre
import com.example.activacorp.util.validarTelefono
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Crea contactos con POST /organizaciones/{id}/contactos.
 * Si el correo ya existe en esa organización, la API responde 409.
 */
class CrearContactoViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val organizacionId: Int = savedStateHandle.get<Int>("organizacionId") ?: 0
    private val casos = ActivacorpUseCases()

    private val _uiState = MutableStateFlow(CrearContactoUiState())
    val uiState: StateFlow<CrearContactoUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = false) }
    }

    fun cambiarCargo(valor: String) {
        _uiState.update { it.copy(cargo = valor, errorCargo = false) }
    }

    fun cambiarCorreo(valor: String) {
        _uiState.update {
            it.copy(
                correo = valor,
                errorCorreo = false,
                errorCorreoDuplicado = false
            )
        }
    }

    fun cambiarTelefono(valor: String) {
        _uiState.update { it.copy(telefono = valor, errorTelefono = false) }
    }

    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value
        val correo = estado.correo.trim()

        val errorNombre = !validarNombre(estado.nombre)
        val errorCargo = !validarCampoObligatorio(estado.cargo)
        val errorCorreo = !validarCorreo(correo)
        val errorTelefono = !validarTelefono(estado.telefono)

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorCargo = errorCargo,
                errorCorreo = errorCorreo,
                errorTelefono = errorTelefono,
                errorCorreoDuplicado = false,
                mensajeError = null
            )
        }

        if (errorNombre || errorCargo || errorCorreo || errorTelefono) return

        viewModelScope.launch {
            try {
                casos.crearContacto(
                    organizacionId,
                    ContactoRequest(
                        nombre = estado.nombre.trim(),
                        cargo = estado.cargo.trim(),
                        correo = correo,
                        telefono = estado.telefono.trim()
                    )
                )

                _uiState.value = CrearContactoUiState()
                onGuardado()

            } catch (e: CancellationException) {
                throw e

            } catch (e: ErrorAplicacion) {
                _uiState.update {
                    it.copy(
                        errorCorreoDuplicado = e.codigoHttp == 409,
                        mensajeError = if (e.codigoHttp == 409) null else e.message
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        mensajeError = e.message
                            ?: "No se pudo crear el contacto."
                    )
                }
            }
        }
    }
}
