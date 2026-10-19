package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.OrganizacionRepository
import com.example.activacorp.model.OrganizacionResumen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * VIEWMODEL: OrganizacionesViewModel (Directorio y Consultas)
 * Gestiona el flujo reactivo de datos para el listado de organizaciones.
 * Consume la consulta con resumen de contactos desde Room y expone la colección a la UI.
 */
class OrganizacionesViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Instancia de la base de datos local y repositorio de organizaciones
    private val database = AppDatabase.getInstance(application)
    private val repository = OrganizacionRepository(database.organizacionDao())

    /**
     * Flujo de estado inmutable (StateFlow) que emite la lista consolidada de organizaciones
     * junto con el recuento total de contactos asociados. Mantiene la suscripción activa durante 5 segundos.
     */
    val organizaciones: StateFlow<List<OrganizacionResumen>> =
        repository
            .observarResumen()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000), // Mantiene activo el flujo 5s tras pausar la UI
                initialValue = emptyList()
            )
}