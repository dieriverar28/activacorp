package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OportunidadRepository
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Rol
import com.example.activacorp.model.obtenerRol
import com.example.activacorp.util.Sesion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class OportunidadesUiState(
    val lista: List<OportunidadConDatos> = emptyList(),
    val esSupervisor: Boolean = false
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

    val uiState: StateFlow<OportunidadesUiState> =
        (if (usuario != null) {
            repository.observarParaUsuario(usuario)
        } else {
            flowOf(emptyList<OportunidadConDatos>())
        })
            .map { lista ->
                OportunidadesUiState(lista = lista, esSupervisor = esSupervisor)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = OportunidadesUiState(esSupervisor = esSupervisor)
            )
}
