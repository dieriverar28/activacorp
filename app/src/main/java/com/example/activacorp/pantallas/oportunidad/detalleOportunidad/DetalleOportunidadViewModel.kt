package com.example.activacorp.pantallas.oportunidad.detalleOportunidad

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Rol
import com.example.activacorp.model.SeguimientoDetalle
import com.example.activacorp.model.Usuario
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.estadosSiguientes
import com.example.activacorp.util.puedeGestionar
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DetalleUiState(
    val cargando: Boolean = true,
    val detalle: OportunidadConDatos? = null,
    val historial: List<SeguimientoDetalle> = emptyList(),
    val ejecutivos: List<Usuario> = emptyList(),
    val estadosPermitidos: List<EstadoOportunidad> = emptyList(),
    val esSupervisor: Boolean = false,
    val tieneAcceso: Boolean = false,
    val mensaje: String? = null
)

/**
 * Consume:
 *  - GET /oportunidades/{id}
 *  - GET /oportunidades/{id}/seguimientos
 *  - GET /usuarios/ejecutivos (solo si el usuario es supervisor)
 * Y muta con:
 *  - PUT /oportunidades/{id}/estado
 *  - PUT /oportunidades/{id}/asignacion
 *
 * Los permisos/transiciones se siguen calculando con util/Permisos para decidir
 * qué botones mostrar. La API vuelve a validar y responde 403/409 si corresponde.
 */
class DetalleOportunidadViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val oportunidadId: Int = savedStateHandle.get<Int>("id") ?: 0
    private val casos = ActivacorpUseCases()

    private val _detalle = MutableStateFlow<OportunidadConDatos?>(null)
    private val _historial = MutableStateFlow<List<SeguimientoDetalle>>(emptyList())
    private val _ejecutivos = MutableStateFlow<List<Usuario>>(emptyList())
    private val _mensaje = MutableStateFlow<String?>(null)
    private val _cargando = MutableStateFlow(true)

    private var cargandoAhora = false

    val uiState: StateFlow<DetalleUiState> = combine(
        _detalle,
        _historial,
        _ejecutivos,
        _mensaje,
        _cargando
    ) { detalle, historial, ejecutivos, mensaje, cargando ->
        val usuario = Sesion.usuario.value
        val rol = usuario?.obtenerRol()

        val acceso = usuario != null && detalle != null &&
                puedeGestionar(usuario, detalle.responsableId)

        val permitidos = detalle?.let { oportunidad ->
            if (!acceso || rol == null) {
                emptyList()
            } else {
                estadosSiguientes(EstadoOportunidad.valueOf(oportunidad.estado), rol)
            }
        } ?: emptyList()

        DetalleUiState(
            cargando = cargando,
            detalle = detalle,
            historial = historial,
            ejecutivos = ejecutivos,
            estadosPermitidos = permitidos,
            esSupervisor = rol == Rol.SUPERVISOR,
            tieneAcceso = acceso,
            mensaje = mensaje
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetalleUiState()
    )

    init {
        recargar()
    }

    fun recargar() {
        if (oportunidadId == 0 || cargandoAhora) return
        cargandoAhora = true

        viewModelScope.launch {
            _cargando.value = true

            try {
                _detalle.value = casos.oportunidad(oportunidadId)
                _historial.value = casos.seguimientos(oportunidadId)

                val usuario = Sesion.usuario.value
                _ejecutivos.value = if (usuario?.obtenerRol() == Rol.SUPERVISOR) {
                    casos.ejecutivos()
                } else {
                    emptyList()
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value = e.message
                    ?: "No se pudo cargar la oportunidad."

            } finally {
                _cargando.value = false
                cargandoAhora = false
            }
        }
    }

    fun cambiarEstado(nuevoEstado: EstadoOportunidad) {
        val usuario = Sesion.usuario.value ?: return

        viewModelScope.launch {
            try {
                casos.cambiarEstado(oportunidadId, usuario.id, nuevoEstado.name)
                _mensaje.value = "Estado actualizado a: ${nuevoEstado.etiqueta}"
                recargar()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                // Aquí aparece el 409 de la API: "Transición no permitida: ..."
                _mensaje.value = e.message
                    ?: "No se pudo cambiar el estado."
            }
        }
    }

    fun asignar(ejecutivo: Usuario) {
        val usuario = Sesion.usuario.value ?: return

        viewModelScope.launch {
            try {
                casos.asignar(oportunidadId, usuario.id, ejecutivo.id)
                _mensaje.value = "Oportunidad asignada a ${ejecutivo.nombre}"
                recargar()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value = e.message
                    ?: "No se pudo asignar la oportunidad."
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
