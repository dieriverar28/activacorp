package com.example.activacorp.pantallas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.activacorp.data.AppDatabase
import com.example.activacorp.data.ContactoRepository
import com.example.activacorp.data.OrganizacionRepository
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.Organizacion
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class DetalleOrganizacionUiState(
    val cargando: Boolean = true,
    val organizacion: Organizacion? = null,
    val contactos: List<Contacto> = emptyList()
)

class DetalleOrganizacionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    // El id llega desde la navegación (ruta "organizacion/{id}")
    private val organizacionId: Int = savedStateHandle.get<Int>("id") ?: 0

    private val database = AppDatabase.getInstance(application)
    private val organizacionRepository = OrganizacionRepository(database.organizacionDao())
    private val contactoRepository = ContactoRepository(database.contactoDao())

    val uiState: StateFlow<DetalleOrganizacionUiState> = combine(
        organizacionRepository.observarPorId(organizacionId),
        contactoRepository.observarPorOrganizacion(organizacionId)
    ) { organizacion, contactos ->
        DetalleOrganizacionUiState(
            cargando = false,
            organizacion = organizacion,
            contactos = contactos
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetalleOrganizacionUiState()
    )
}
