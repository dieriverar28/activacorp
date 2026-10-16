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

data class DashboardUiState(
    val nombre: String = "",
    val rolEtiqueta: String = "",
    val esSupervisor: Boolean = false,
    val total: Int = 0,
    val proximas: Int = 0,
    val enEspera: Int = 0,
    val sinAsignar: Int = 0
)

class DashboardViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = OportunidadRepository(
        database.oportunidadDao(),
        database.seguimientoDao()
    )

    private val usuario = Sesion.usuario.value
    private val rol = usuario?.obtenerRol()

    private val estadoBase = DashboardUiState(
        nombre = usuario?.nombre ?: "",
        rolEtiqueta = rol?.etiqueta ?: "",
        esSupervisor = rol == Rol.SUPERVISOR
    )

    val uiState: StateFlow<DashboardUiState> =
        (if (usuario != null) {
            repository.observarParaUsuario(usuario)
        } else {
            flowOf(emptyList<OportunidadConDatos>())
        })
            .map { lista ->
                val hoy = hoyTexto()
                estadoBase.copy(
                    total = lista.size,
                    proximas = lista.count { oportunidad ->
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
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = estadoBase
            )

    fun cerrarSesion() {
        Sesion.cerrar()
    }
}
