package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OportunidadRepository
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Prioridad
import com.example.activacorp.model.Rol
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.Sesion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

/**
 * ESTADO DE LA UI: OportunidadesUiState
 * Representa el estado inmutable consumido por OportunidadesScreen.
 * Contiene la lista de oportunidades filtrada, las métricas totales y los criterios de búsqueda activos.
 */
data class OportunidadesUiState(
    val lista: List<OportunidadConDatos> = emptyList(), // Lista procesada y filtrada
    val totalSinFiltrar: Int = 0,                       // Total de elementos devueltos por Room
    val esSupervisor: Boolean = false,                  // Identifica si el usuario es supervisor
    val busqueda: String = "",                         // Texto de búsqueda ingresado
    val filtroEstado: EstadoOportunidad? = null,        // Estado seleccionado para filtrar
    val filtroPrioridad: Prioridad? = null              // Nivel de prioridad seleccionado
)

/**
 * ESTRUCTURA INTERNA: Filtros
 * Representa los criterios seleccionados por el usuario en la interfaz.
 */
private data class Filtros(
    val busqueda: String = "",
    val estado: EstadoOportunidad? = null,
    val prioridad: Prioridad? = null
)

/**
 * VIEWMODEL: OportunidadesViewModel (Lógica de Búsqueda y Filtrado)
 * Combina en tiempo real el flujo de datos proveniente de Room con las preferencias
 * de filtrado aplicadas por el usuario utilizando Kotlin Coroutines y StateFlow.
 */
class OportunidadesViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Instancia de la base de datos local y su repositorio
    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )

    // Datos del usuario en sesión y verificación del rol
    private val usuario = Sesion.usuario.value
    private val esSupervisor = usuario?.obtenerRol() == Rol.SUPERVISOR

    // Flujo de origen de datos desde Room (filtrado a nivel de BD según permisos del usuario)
    private val fuente: Flow<List<OportunidadConDatos>> =
        if (usuario != null) {
            repository.observarParaUsuario(usuario)
        } else {
            flowOf(emptyList())
        }

    // Estado reactivo privado con los filtros aplicados en UI
    private val _filtros = MutableStateFlow(Filtros())

    /**
     * Estado UI expuesto de forma reactiva.
     * Utiliza el operador `combine` para aplicar las reglas de filtrado en tiempo real
     * cada vez que cambia la fuente de Room o los criterios ingresados por el usuario.
     */
    val uiState: StateFlow<OportunidadesUiState> = combine(fuente, _filtros) { lista, filtros ->
        val texto = filtros.busqueda.trim()

        // Filtrado en memoria de la colección reactiva
        val filtrada = lista.filter { oportunidad ->
            // Búsqueda por coincidencia de texto en múltiples campos (Nombre, Organización o Comuna)
            val coincideTexto = texto.isEmpty() ||
                    oportunidad.nombre.contains(texto, ignoreCase = true) ||
                    oportunidad.organizacionNombre.contains(texto, ignoreCase = true) ||
                    oportunidad.comuna.contains(texto, ignoreCase = true)

            // Evaluación del filtro por estado
            val coincideEstado = filtros.estado == null ||
                    oportunidad.estado == filtros.estado.name

            // Evaluación del filtro por prioridad
            val coincidePrioridad = filtros.prioridad == null ||
                    oportunidad.prioridad == filtros.prioridad.name

            coincideTexto && coincideEstado && coincidePrioridad
        }

        OportunidadesUiState(
            lista = filtrada,
            totalSinFiltrar = lista.size,
            esSupervisor = esSupervisor,
            busqueda = filtros.busqueda,
            filtroEstado = filtros.estado,
            filtroPrioridad = filtros.prioridad
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000), // Retiene la suscripción 5 segundos tras pausar la vista
        initialValue = OportunidadesUiState(esSupervisor = esSupervisor)
    )

    /**
     * Actualiza el texto de búsqueda en tiempo real.
     */
    fun cambiarBusqueda(texto: String) {
        _filtros.update { it.copy(busqueda = texto) }
    }

    /**
     * Selecciona o desselecciona el filtro por estado (null limpia el filtro).
     */
    fun seleccionarEstado(estado: EstadoOportunidad?) {
        _filtros.update { it.copy(estado = estado) }
    }

    /**
     * Selecciona o desselecciona el filtro por prioridad (null limpia el filtro).
     */
    fun seleccionarPrioridad(prioridad: Prioridad?) {
        _filtros.update { it.copy(prioridad = prioridad) }
    }

    /**
     * Restablece todos los criterios de filtrado a sus valores iniciales.
     */
    fun limpiarFiltros() {
        _filtros.value = Filtros()
    }
}