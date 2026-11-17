package com.example.activacorp.pantallas.organizacion.detalleOrganizacion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.Organizacion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleOrganizacionUiState(
    val cargando: Boolean = true,
    val organizacion: Organizacion? = null,
    val contactos: List<Contacto> = emptyList(),
    val mensajeError: String? = null
)

/**
 * Consume:
 *  - GET /organizaciones/{id}
 *  - GET /organizaciones/{id}/contactos
 */
class DetalleOrganizacionViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val organizacionId: Int = savedStateHandle.get<Int>("id") ?: 0
    private val casos = ActivacorpUseCases()

    private val _uiState = MutableStateFlow(DetalleOrganizacionUiState())
    val uiState: StateFlow<DetalleOrganizacionUiState> = _uiState.asStateFlow()

    private var cargandoAhora = false

    init {
        recargar()
    }

    fun recargar() {
        if (organizacionId == 0 || cargandoAhora) return
        cargandoAhora = true

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true, mensajeError = null)

            try {
                val organizacion = casos.organizacion(organizacionId)
                val contactos = casos.contactos(organizacionId)

                _uiState.value = DetalleOrganizacionUiState(
                    cargando = false,
                    organizacion = organizacion,
                    contactos = contactos
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar la organización."
                )

            } finally {
                cargandoAhora = false
            }
        }
    }
}
