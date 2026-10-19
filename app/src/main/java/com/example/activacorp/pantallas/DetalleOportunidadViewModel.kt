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

/**
 * ESTADO DE LA UI: DetalleUiState
 * Representa la información requerida en la pantalla de detalle de una oportunidad.
 * Contiene el objeto de datos, el historial de gestiones, los estados permitidos según el rol y flags de control.
 */
data class DetalleUiState(
    val cargando: Boolean = true,                          // Indica si la consulta a Room está en ejecución
    val detalle: OportunidadConDatos? = null,             // Datos de la oportunidad con nombres de relación
    val historial: List<SeguimientoDetalle> = emptyList(), // Lista de seguimientos/gestiones registradas
    val ejecutivos: List<Usuario> = emptyList(),           // Lista de ejecutivos disponibles para asignación
    val estadosPermitidos: List<EstadoOportunidad> = emptyList(), // Transiciones válidas según rol y estado actual
    val esSupervisor: Boolean = false,                     // Flag que identifica si el usuario es supervisor
    val tieneAcceso: Boolean = false,                      // Control de seguridad según asignación o rol
    val mensaje: String? = null                            // Feedback visual de confirmación o error
)

/**
 * VIEWMODEL: DetalleOportunidadViewModel (Lógica de Detalle, Permisos e Historial)
 * Combina múltiples flujos de datos (Room) mediante corrutinas y StateFlow para evaluar
 * los permisos de acceso, las transiciones de estado válidas y la reasignación de ejecutivos.
 */
class DetalleOportunidadViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle // Permite extraer argumentos de la ruta de navegación Compose
) : AndroidViewModel(application) {

    // Identificador único de la oportunidad recuperado desde la ruta ("detalle/{id}")
    private val oportunidadId: Int = savedStateHandle.get<Int>("id") ?: 0

    // Instancia de la base de datos local y repositorios de datos
    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )
    private val usuarioRepository = UsuarioRepository(database.usuarioDao())

    // Estado interno para almacenar mensajes emergentes o notificaciones en pantalla
    private val _mensaje = MutableStateFlow<String?>(null)

    /**
     * Flujo reactivo StateFlow que combina los datos de la oportunidad, el historial de gestiones,
     * la lista de ejecutivos y el estado de mensajes. Evalúa los permisos de acceso en tiempo real.
     */
    val uiState: StateFlow<DetalleUiState> = combine(
        repository.observarDetalle(oportunidadId),
        repository.observarHistorial(oportunidadId),
        usuarioRepository.observarEjecutivos(),
        _mensaje
    ) { detalle, historial, ejecutivos, mensaje ->
        val usuario = Sesion.usuario.value
        val rol = usuario?.obtenerRol()

        // Regla de seguridad: Un ejecutivo solo puede gestionar sus oportunidades asignadas (el supervisor tiene acceso global)
        val acceso = usuario != null && detalle != null &&
                puedeGestionar(usuario, detalle.responsableId)

        // Cálculo de transiciones de estado permitidas según la máquina de estados del sistema
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
        started = SharingStarted.WhileSubscribed(5000), // Mantiene activo el flujo 5 segundos tras cambiar de vista
        initialValue = DetalleUiState()
    )

    /**
     * Transiciona la oportunidad al siguiente estado válido según el flujo de negocio,
     * registrando el evento correspondiente en el historial de Room.
     */
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

    /**
     * Reasigna la oportunidad a un ejecutivo comercial específico (Función exclusiva del Rol Supervisor).
     */
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