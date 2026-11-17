package com.example.activacorp.pantallas.organizacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activacorp.domain.usecase.ActivacorpUseCases
import com.example.activacorp.model.OrganizacionResumen
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Consume GET /organizaciones (ya trae el total de contactos).
 * Se mantiene la propiedad "organizaciones" para no tocar la pantalla.
 */
class OrganizacionesViewModel(
    private val casos: ActivacorpUseCases = ActivacorpUseCases()
) : ViewModel() {

    private val _organizaciones = MutableStateFlow<List<OrganizacionResumen>>(emptyList())
    val organizaciones: StateFlow<List<OrganizacionResumen>> = _organizaciones.asStateFlow()

    private val _cargando = MutableStateFlow(true)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private var cargandoAhora = false

    init {
        recargar()
    }

    fun recargar() {
        if (cargandoAhora) return
        cargandoAhora = true

        viewModelScope.launch {
            _cargando.value = true

            try {
                _organizaciones.value = casos.organizaciones()
                _mensajeError.value = null

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensajeError.value = e.message
                    ?: "No se pudieron cargar las organizaciones."

            } finally {
                _cargando.value = false
                cargandoAhora = false
            }
        }
    }
}
