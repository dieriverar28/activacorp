package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OportunidadRepository
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Rol
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.Sesion
import com.example.activacorp.util.esEstadoFinal
import com.example.activacorp.util.hoyTexto
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ESTADO DE LA UI: DashboardUiState
 * Modela la estructura de datos inmutable que consume la pantalla principal (Dashboard).
 * Almacena métricas calculadas y detalles del perfil del usuario autenticado.
 */
data class DashboardUiState(
    val nombre: String = "",          // Nombre del usuario activo
    val rolEtiqueta: String = "",      // Etiqueta legible del rol (Ejecutivo/Supervisor)
    val esSupervisor: Boolean = false, // Determina si se habilitan controles de supervisión
    val total: Int = 0,               // Total de oportunidades asignadas o visibles
    val proximas: Int = 0,            // Oportunidades con eventos vigentes/futuros
    val enEspera: Int = 0,            // Oportunidades pendientes de aprobación
    val sinAsignar: Int = 0           // Registros huérfanos sin ejecutivo a cargo
)

/**
 * VIEWMODEL: DashboardViewModel (Lógica del Panel Principal)
 * Recolecta la sesión activa, realiza consultas reactivas a Room mediante Flow
 * y calcula métricas clave diferenciadas por rol para mostrarlas en la UI.
 */
class DashboardViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Instancia principal de la base de datos local y su repositorio
    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )

    // Datos del usuario logueado en el Singleton de Sesión
    private val usuario = Sesion.usuario.value
    private val rol = usuario?.obtenerRol()

    // Configuración inicial de UI con la identidad del usuario
    private val estadoBase = DashboardUiState(
        nombre = usuario?.nombre ?: "",
        rolEtiqueta = rol?.etiqueta ?: "",
        esSupervisor = rol == Rol.SUPERVISOR
    )

    /**
     * Flujo reactivo StateFlow que emite el estado del Dashboard procesado en tiempo real.
     * Mantiene los datos sincronizados ante cualquier cambio en la base de datos (Room).
     */
    val uiState: StateFlow<DashboardUiState> =
        (if (usuario != null) {
            // Consulta reactiva filtrada por los permisos del usuario activo
            repository.observarParaUsuario(usuario)
        } else {
            flowOf(emptyList<OportunidadConDatos>())
        })
            .map { lista ->
                val hoy = hoyTexto()

                // Mapeo dinámico y cálculo de métricas contables
                estadoBase.copy(
                    total = lista.size,
                    proximas = lista.count { oportunidad ->
                        // Evalúa eventos vigentes que no estén en un estado cerrado o cancelado
                        oportunidad.fechaEvento >= hoy &&
                                !esEstadoFinal(EstadoOportunidad.valueOf(oportunidad.estado))
                    },
                    enEspera = lista.count { oportunidad ->
                        oportunidad.estado == EstadoOportunidad.EN_ESPERA_DE_RESPUESTA.name
                    },
                    sinAsignar = lista.count { oportunidad ->
                        oportunidad.responsableId == null
                    }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Mantiene la suscripción 5s tras perder la vista
                initialValue = estadoBase
            )

    /**
     * Limpia las credenciales del Singleton de Sesión para cerrar la sesión del usuario.
     */
    fun cerrarSesion() {
        Sesion.cerrar()
    }
}