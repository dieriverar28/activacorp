package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OportunidadRepository
import com.example.activacorp.data.UsuarioRepository
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Rol
import com.example.activacorp.model.SeguimientoDetalle
import com.example.activacorp.model.Usuario
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.estadosSiguientes
import com.example.activacorp.util.puedeGestionar
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

class DetalleOportunidadViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // El id llega desde la navegación (ruta "detalle/{id}")
    private val oportunidadId: Int = savedStateHandle.get<Int>("id") ?: 0

    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )
    private val usuarioRepository = UsuarioRepository(database.usuarioDao())

    private val _mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DetalleUiState> = combine(
        repository.observarDetalle(oportunidadId),
        repository.observarHistorial(oportunidadId),
        usuarioRepository.observarEjecutivos(),
        _mensaje
    ) { detalle, historial, ejecutivos, mensaje ->
        val usuario = Sesion.usuario.value
        val rol = usuario?.obtenerRol()

        // Un ejecutivo solo puede acceder a sus oportunidades asignadas
        val acceso = usuario != null && detalle != null &&
                puedeGestionar(usuario, detalle.responsableId)

        val permitidos = if (acceso && rol != null && detalle != null) {
            estadosSiguientes(EstadoOportunidad.valueOf(detalle.estado), rol)
        } else {
            emptyList()
        }

        DetalleUiState(
            cargando = false,
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

    fun cambiarEstado(nuevoEstado: EstadoOportunidad) {
        val usuario = Sesion.usuario.value ?: return

        viewModelScope.launch {
            val exito = repository.cambiarEstado(oportunidadId, nuevoEstado, usuario)
            _mensaje.value = if (exito) {
                "Estado actualizado a: ${nuevoEstado.etiqueta}"
            } else {
                "No tienes permiso para realizar este cambio"
            }
        }
    }

    fun asignar(ejecutivo: Usuario) {
        val usuario = Sesion.usuario.value ?: return

        viewModelScope.launch {
            val exito = repository.asignar(oportunidadId, ejecutivo, usuario)
            _mensaje.value = if (exito) {
                "Oportunidad asignada a ${ejecutivo.nombre}"
            } else {
                "No se pudo asignar la oportunidad"
            }
        }
    }
}
