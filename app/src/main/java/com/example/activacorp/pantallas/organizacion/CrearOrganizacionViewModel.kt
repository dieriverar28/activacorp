package com.example.activacorp.pantallas.organizacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.model.OrganizacionRequest
import com.example.activacorp.domain.model.ErrorAplicacion
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarNombre
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Crea organizaciones con POST /organizaciones.
 * Si el nombre ya existe, la API responde 409 y se marca errorNombreDuplicado.
 */
class CrearOrganizacionViewModel(
    private val casos: ActivacorpUseCases = ActivacorpUseCases()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearOrganizacionUiState())
    val uiState: StateFlow<CrearOrganizacionUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) {
        _uiState.update {
            it.copy(
                nombre = valor,
                errorNombre = false,
                errorNombreDuplicado = false,
                mensajeError = null
            )
        }
    }

    fun cambiarTipo(valor: String) {
        _uiState.update { it.copy(tipo = valor, errorTipo = false) }
    }

    fun cambiarComuna(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = false) }
    }

    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value

        val errorNombre = !validarNombre(estado.nombre)
        val errorTipo = !validarCampoObligatorio(estado.tipo)
        val errorComuna = !validarCampoObligatorio(estado.comuna)

        _uiState.update {
            it.copy(
                errorNombre = errorNombre,
                errorTipo = errorTipo,
                errorComuna = errorComuna,
                errorNombreDuplicado = false,
                mensajeError = null
            )
        }

        if (errorNombre || errorTipo || errorComuna) return

        viewModelScope.launch {
            try {
                casos.crearOrganizacion(
                    OrganizacionRequest(
                        nombre = estado.nombre.trim(),
                        tipo = estado.tipo.trim(),
                        comuna = estado.comuna.trim()
                    )
                )

                _uiState.value = CrearOrganizacionUiState()
                onGuardado()

            } catch (e: CancellationException) {
                throw e

            } catch (e: ErrorAplicacion) {
                _uiState.update {
                    it.copy(
                        errorNombreDuplicado = e.codigoHttp == 409,
                        mensajeError = if (e.codigoHttp == 409) null else e.message
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        mensajeError = e.message
                            ?: "No se pudo crear la organización."
                    )
                }
            }
        }
    }
}
