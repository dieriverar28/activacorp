package com.example.activacorp.pantallas.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Rol
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.esEstadoFinal
import com.example.activacorp.util.hoyTexto
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DashboardUiState(
    val nombre: String = "",
    val rolEtiqueta: String = "",
    val esSupervisor: Boolean = false,
    val total: Int = 0,
    val proximas: Int = 0,
    val enEspera: Int = 0,
    val sinAsignar: Int = 0,
    val cargando: Boolean = false,
    val mensajeError: String? = null
)

/**
 * Consume GET /oportunidades?usuarioId=... y calcula las mismas métricas que antes.
 * Al volver al dashboard hay que llamar a recargar() (la API no es reactiva).
 */
class DashboardViewModel(
    private val casos: ActivacorpUseCases = ActivacorpUseCases()
) : ViewModel() {

    private val usuario = Sesion.usuario.value
    private val rol = usuario?.obtenerRol()

    private val estadoBase = DashboardUiState(
        nombre = usuario?.nombre ?: "",
        rolEtiqueta = rol?.etiqueta ?: "",
        esSupervisor = rol == Rol.SUPERVISOR
    )

    private val _uiState = MutableStateFlow(estadoBase)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var cargandoAhora = false

    init {
        recargar()
    }

    fun recargar() {
        val actual = Sesion.usuario.value ?: return

        if (cargandoAhora) return
        cargandoAhora = true

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true, mensajeError = null)

            try {
                val lista = casos.oportunidades(actual.id)
                val hoy = hoyTexto()

                _uiState.value = estadoBase.copy(
                    total = lista.size,
                    proximas = lista.count { oportunidad ->
                        oportunidad.fechaEvento >= hoy &&
                                !esEstadoFinal(
                                    EstadoOportunidad.valueOf(oportunidad.estado)
                                )
                    },
                    enEspera = lista.count {
                        it.estado == EstadoOportunidad.EN_ESPERA_DE_RESPUESTA.name
                    },
                    sinAsignar = lista.count { it.responsableId == null },
                    cargando = false
                )

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    cargando = false,
                    mensajeError = e.message
                        ?: "No se pudo cargar el panel."
                )

            } finally {
                cargandoAhora = false
            }
        }
    }

    fun cerrarSesion() {
        Sesion.cerrar()
    }
}
