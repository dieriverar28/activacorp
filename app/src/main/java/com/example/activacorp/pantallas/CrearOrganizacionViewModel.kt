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

/**
 * VIEWMODEL: CrearOrganizacionViewModel (Lógica de Negocio)
 * Administra el estado de la interfaz de creación de organizaciones, ejecuta validaciones de entrada,
 * comprueba la regla de negocio de duplicados y persiste la entidad mediante el repositorio local.
 */
class CrearOrganizacionViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Instancia de la base de datos Room y del repositorio de organizaciones
    private val database = AppDatabase.getInstance(application)
    private val repository = OrganizacionRepository(database.organizacionDao())

    // Estado UI mutable privado (StateFlow)
    private val _uiState = MutableStateFlow(CrearOrganizacionUiState())
    // Estado UI inmutable expuesto a la pantalla de Jetpack Compose
    val uiState: StateFlow<CrearOrganizacionUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el nombre ingresado y resetea los indicadores de error correspondientes.
     */
    fun cambiarNombre(valor: String) {
        _uiState.update { it.copy(nombre = valor, errorNombre = false, errorNombreDuplicado = false) }
    }

    /**
     * Actualiza el tipo de organización y deshabilita la alerta de error.
     */
    fun cambiarTipo(valor: String) {
        _uiState.update { it.copy(tipo = valor, errorTipo = false) }
    }

    /**
     * Actualiza la comuna seleccionada y limpia su estado de error.
     */
    fun cambiarComuna(valor: String) {
        _uiState.update { it.copy(comuna = valor, errorComuna = false) }
    }

    /**
     * Valida el formulario, ejecuta la regla de negocio sobre organizaciones duplicadas
     * y guarda la nueva entidad en segundo plano.
     */
    fun guardar(onGuardado: () -> Unit) {
        val estado = _uiState.value

        // Comprobación de requerimientos en los campos de entrada
        val errorNombre = !validarNombre(estado.nombre)
        val errorTipo = !validarCampoObligatorio(estado.tipo)
        val errorComuna = !validarCampoObligatorio(estado.comuna)

        // Refleja los resultados de la validación en la interfaz de usuario
        _uiState.update { estadoActual ->
            estadoActual.copy(
                errorNombre = errorNombre,
                errorTipo = errorTipo,
                errorComuna = errorComuna
            )
        }

        // Interrumpe la operación si existen errores de entrada
        if (errorNombre || errorTipo || errorComuna) {
            return
        }

        // Ejecución en segundo plano mediante Corrutinas de Kotlin
        viewModelScope.launch {
            val nombre = estado.nombre.trim()

            // Regla de negocio: Verificar unicidad del nombre de la organización
            if (repository.existeNombre(nombre)) {
                _uiState.update { it.copy(errorNombreDuplicado = true) }
            } else {
                // Inserción en la base de datos a través del repositorio
                repository.crear(
                    Organizacion(
                        nombre = nombre,
                        tipo = estado.tipo.trim(),
                        comuna = estado.comuna.trim()
                    )
                )
                // Restablecer el estado del formulario
                _uiState.value = CrearOrganizacionUiState()
                // Ejecución del evento de retorno tras el guardado exitoso
                onGuardado()
            }
        }
    }
}