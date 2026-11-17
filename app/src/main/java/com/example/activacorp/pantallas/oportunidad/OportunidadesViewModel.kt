package com.example.activacorp.pantallas.oportunidad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Prioridad
import com.example.activacorp.model.Rol
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.Sesion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI: lista ya filtrada + criterios + estado de la carga.
 */
data class OportunidadesUiState(
    val lista: List<OportunidadConDatos> = emptyList(),
    val totalSinFiltrar: Int = 0,
    val esSupervisor: Boolean = false,
    val busqueda: String = "",
    val filtroEstado: EstadoOportunidad? = null,
    val filtroPrioridad: Prioridad? = null,
    val cargando: Boolean = false,
    val mensajeError: String? = null
)

private data class Filtros(
    val busqueda: String = "",
    val estado: EstadoOportunidad? = null,
    val prioridad: Prioridad? = null
)

/**
 * Consume GET /oportunidades?usuarioId=...
 * El filtrado por texto/estado/prioridad se sigue haciendo en memoria, igual que antes.
 * Como la API no es reactiva, se debe llamar a recargar() al volver a la pantalla.
 */
class OportunidadesViewModel(
    private val casos: ActivacorpUseCases = ActivacorpUseCases()
) : ViewModel() {

    private val usuario = Sesion.usuario.value
    private val esSupervisor = usuario?.obtenerRol() == Rol.SUPERVISOR

    private val _lista = MutableStateFlow<List<OportunidadConDatos>>(emptyList())
    private val _filtros = MutableStateFlow(Filtros())
    private val _cargando = MutableStateFlow(true)
    private val _mensajeError = MutableStateFlow<String?>(null)

    private var cargandoAhora = false

    val uiState: StateFlow<OportunidadesUiState> = combine(
        _lista,
        _filtros,
        _cargando,
        _mensajeError
    ) { lista, filtros, cargando, mensajeError ->
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
            filtroPrioridad = filtros.prioridad,
            cargando = cargando,
            mensajeError = mensajeError
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = OportunidadesUiState(esSupervisor = esSupervisor, cargando = true)
    )

    init {
        recargar()
    }

    fun recargar() {
        val actual = Sesion.usuario.value ?: return

        if (cargandoAhora) return
        cargandoAhora = true

        viewModelScope.launch {
            _cargando.value = true

            try {
                _lista.value = casos.oportunidades(actual.id)
                _mensajeError.value = null

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensajeError.value = e.message
                    ?: "No se pudieron cargar las oportunidades."

            } finally {
                _cargando.value = false
                cargandoAhora = false
            }
        }
    }

    fun cambiarBusqueda(texto: String) {
        _filtros.update { it.copy(busqueda = texto) }
    }

    fun seleccionarEstado(estado: EstadoOportunidad?) {
        _filtros.update { it.copy(estado = estado) }
    }

    fun seleccionarPrioridad(prioridad: Prioridad?) {
        _filtros.update { it.copy(prioridad = prioridad) }
    }

    fun limpiarFiltros() {
        _filtros.value = Filtros()
    }
}
