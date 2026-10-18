package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OrganizacionRepository
import com.example.activacorp.model.Organizacion
import com.example.activacorp.util.validarCampoObligatorio
import com.example.activacorp.util.validarNombre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CrearOrganizacionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = OrganizacionRepository(database.organizacionDao())

    private val _uiState = MutableStateFlow(CrearOrganizacionUiState())
    val uiState: StateFlow<CrearOrganizacionUiState> = _uiState.asStateFlow()

    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = false, errorNombreDuplicado = false) }
    }

    fun cambiarTipo(valor: String) {
        _uiState.update { it.copy(tipo = valor, errorTipo = false) }
    }

    fun cambiarComuna(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = false) }
    }

    // onGuardado se ejecuta cuando la organización ya quedó guardada
    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value

        val errorNombre = !validarNombre(estado.nombre)
        val errorTipo = !validarCampoObligatorio(estado.tipo)
        val errorComuna = !validarCampoObligatorio(estado.comuna)

        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorTipo = errorTipo,
                errorComuna = errorComuna
            )
        }

        if (errorNombre || errorTipo || errorComuna) {
            return
        }

        viewModelScope.launch {
            val nombre = estado.nombre.trim()

            // Regla de negocio: no se repiten organizaciones
            if (repository.existeNombre(nombre)) {
                _uiState.update { it.copy(errorNombreDuplicado = true) }
            } else {
                repository.crear(
                    Organizacion(
                        nombre = nombre,
                        tipo = estado.tipo.trim(),
                        comuna = estado.comuna.trim()
                    )
                )
                _uiState.value = CrearOrganizacionUiState()
                onGuardado()
            }
        }
    }
}
