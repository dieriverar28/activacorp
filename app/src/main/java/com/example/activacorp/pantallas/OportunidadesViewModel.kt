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

data class OportunidadesUiState(
    // Lista ya filtrada
    val lista: List<OportunidadConDatos> = emptyList(),
    // Total de oportunidades visibles para el usuario, sin aplicar filtros
    val totalSinFiltrar: Int = 0,
    val esSupervisor: Boolean = false,
    val busqueda: String = "",
    val filtroEstado: EstadoOportunidad? = null,
    val filtroPrioridad: Prioridad? = null
)

// Filtros que el usuario va eligiendo en pantalla
private data class Filtros(
    val busqueda: String = "",
    val estado: EstadoOportunidad? = null,
    val prioridad: Prioridad? = null
)

class OportunidadesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )

    private val usuario = Sesion.usuario.value
    private val esSupervisor = usuario?.obtenerRol() == Rol.SUPERVISOR

    // Oportunidades que el usuario tiene permitido ver
    private val fuente: Flow<List<OportunidadConDatos>> =
        if (usuario != null) {
            repository.observarParaUsuario(usuario)
        } else {
            flowOf(emptyList())
        }

    private val _filtros = MutableStateFlow(Filtros())

    val uiState: StateFlow<OportunidadesUiState> = combine(fuente, _filtros) { lista, filtros ->
        val texto = filtros.busqueda.trim()

        val filtrada = lista.filter { oportunidad ->
            val coincideTexto = texto.isEmpty() ||
                    oportunidad.nombre.contains(texto, ignoreCase = true) ||
                    oportunidad.organizacionNombre.contains(texto, ignoreCase = true) ||
                    oportunidad.comuna.contains(texto, ignoreCase = true)

            val coincideEstado = filtros.estado == null ||
                    oportunidad.estado == filtros.estado.name

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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OportunidadesUiState(esSupervisor = esSupervisor)
    )

    fun cambiarBusqueda(texto: String) {
        _filtros.update { it.copy(busqueda = texto) }
    }

    // null significa "todos los estados"
    fun seleccionarEstado(estado: EstadoOportunidad?) {
        _filtros.update { it.copy(estado = estado) }
    }

    // null significa "todas las prioridades"
    fun seleccionarPrioridad(prioridad: Prioridad?) {
        _filtros.update { it.copy(prioridad = prioridad) }
    }

    fun limpiarFiltros() {
        _filtros.value = Filtros()
    }
}
