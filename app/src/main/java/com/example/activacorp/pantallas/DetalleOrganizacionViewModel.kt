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

/**
 * ESTADO DE LA UI: DetalleOrganizacionUiState
 * Representa la estructura inmutable que contiene la información de la organización solicitada
 * y el listado dinámico de contactos corporativos vinculados a ella.
 */
data class DetalleOrganizacionUiState(
    val cargando: Boolean = true,             // Flag que indica si los datos están consultándose en Room
    val organizacion: Organizacion? = null,   // Ficha técnica de la organización seleccionada
    val contactos: List<Contacto> = emptyList() // Colección de contactos pertenecientes a esta organización
)

/**
 * VIEWMODEL: DetalleOrganizacionViewModel (Lógica de Consulta y Estado)
 * Recupera el identificador enviado mediante la navegación Compose y combina en tiempo real
 * la consulta de la entidad Organización con su lista de contactos asociados desde Room.
 */
class DetalleOrganizacionViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle // Permite extraer argumentos de la ruta de navegación ("organizacion/{id}")
) : AndroidViewModel(application) {

    // Identificador único de la organización recuperado desde los argumentos de la ruta
    private val organizacionId: Int = savedStateHandle.get<Int>("id") ?: 0

    // Instancia de la base de datos local y sus repositorios correspondientes
    private val database = AppDatabase.getInstance(application)
    private val organizacionRepository = OrganizacionRepository(database.organizacionDao())
    private val contactoRepository = ContactoRepository(database.contactoDao())

    /**
     * Flujo de estado inmutable (StateFlow) que combina de forma reactiva la información
     * de la organización y sus contactos. Actualiza automáticamente la UI si se añade un nuevo contacto.
     */
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
        started = SharingStarted.WhileSubscribed(5000), // Mantiene la suscripción activa por 5 segundos al pausar la vista
        initialValue = DetalleOrganizacionUiState()
    )
}