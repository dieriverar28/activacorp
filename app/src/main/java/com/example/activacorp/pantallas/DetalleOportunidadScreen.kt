package com.example.activacorp.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Prioridad
import com.example.activacorp.ui.IndicadorEstadoAnimado
import com.example.activacorp.ui.MensajeAlertaAnimado
import com.example.activacorp.util.cargarImagenPrevia
import com.example.activacorp.util.esEstadoFinal

/**
 * PANTALLA: DetalleOportunidadScreen (Stateful Composable)
 * Conecta el estado reactivo del DetalleOportunidadViewModel con la interfaz visual.
 * Mantiene la suscripción al ciclo de vida mediante collectAsStateWithLifecycle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOportunidadScreen(
    onIrAEditar: () -> Unit = {}, // Callback para navegar a la pantalla de edición
    onVolver: () -> Unit = {}, // Callback para regresar al listado de oportunidades
    viewModel: DetalleOportunidadViewModel = viewModel() // Inyección predeterminada del ViewModel
) {
    // Sincronización del estado UI reactivo
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Renderizado del contenido visual sin estado
    DetalleOportunidadContent(
        uiState = uiState,
        onCambiarEstado = viewModel::cambiarEstado,
        onAsignar = viewModel::asignar,
        onAgregarGestion = { tipo, desc, fechaProx ->
            // Invocación al método de agregar seguimiento en el ViewModel
        },
        onIrAEditar = onIrAEditar,
        onVolver = onVolver
    )
}

/**
 * PANTALLA: DetalleOportunidadContent (Stateless Composable)
 * Muestra el expediente completo de una oportunidad comercial:
 * ficha técnica, evidencia fotográfica, botones de avance de estado, asignación y bitácora de gestiones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOportunidadContent(
    uiState: DetalleUiState,
    onCambiarEstado: (EstadoOportunidad) -> Unit = {},
    onAsignar: (com.example.activacorp.model.Usuario) -> Unit = {},
    onAgregarGestion: (tipo: String, descripcion: String, fechaProximo: String) -> Unit = { _, _, _ -> },
    onIrAEditar: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    val detalle = uiState.detalle
    // Estado local para visibilidad del cuadro de diálogo para registrar una nueva gestión
    var mostrarDialogoGestion by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Detalle de oportunidad")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Soporte de Scroll para contenidos extensos
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Control de Estados de Carga y Acceso
            if (uiState.cargando) {
                CircularProgressIndicator() // Indicador visual durante la lectura en Room
            } else if (detalle == null) {
                Text("La oportunidad no existe")
            } else if (!uiState.tieneAcceso) {
                Text(
                    text = "No tienes acceso a esta oportunidad",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val estadoActual = EstadoOportunidad.valueOf(detalle.estado)

                // ----- FICHA TÉCNICA DE LA OPORTUNIDAD -----
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = detalle.nombre,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Organización: ${detalle.organizacionNombre}")
                        Text("Tipo de evento: ${detalle.tipoEvento}")
                        Text("Fecha del evento: ${detalle.fechaEvento}")
                        Text("Ubicación: ${detalle.ubicacion}, ${detalle.comuna}")
                        Text("Público esperado: ${detalle.publicoEsperado}")
                        Text("Prioridad: ${Prioridad.valueOf(detalle.prioridad).etiqueta}")
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("Estado:")
                            IndicadorEstadoAnimado(
                                estadoConfirmado = detalle.estado == EstadoOportunidad.PARTICIPACION_CONFIRMADA.name,
                                etiqueta = estadoActual.etiqueta
                            )
                        }

                        Text("Responsable: ${detalle.responsableNombre ?: "Sin asignar"}")

                        // Información del contacto corporativo asociado a la entidad
                        Text(
                            text = "Contacto: ${if (detalle.organizacionNombre.isNotBlank()) "Contacto Corporativo Asociado" else "Sin contacto"}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (detalle.observaciones.isNotBlank()) {
                            Text("Observaciones: ${detalle.observaciones}")
                        }
                    }
                }

                // Botón para Editar Oportunidad
                OutlinedButton(
                    onClick = onIrAEditar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Editar Oportunidad")
                }

                // ----- RECURSO VISUAL: Carga de foto del evento -----
                val imagenPrevia = remember(detalle.fotoUri) {
                    detalle.fotoUri?.let { cargarImagenPrevia(it) }
                }
                if (imagenPrevia != null) {
                    Image(
                        bitmap = imagenPrevia,
                        contentDescription = "Foto del evento",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                // Despliegue de mensajes informativos del sistema con Animación
                MensajeAlertaAnimado(
                    visible = uiState.mensaje != null,
                    mensaje = uiState.mensaje ?: "",
                    esError = false
                )

                // ----- TRANSICIONES DE ESTADO PERMITIDAS -----
                Text(
                    text = "Cambiar estado",
                    style = MaterialTheme.typography.titleMedium
                )
                if (uiState.estadosPermitidos.isEmpty()) {
                    if (esEstadoFinal(estadoActual)) {
                        Text("La oportunidad ya tiene una decisión final")
                    } else {
                        Text("No hay acciones disponibles. Esta oportunidad espera la decisión del supervisor.")
                    }
                } else {
                    // Botones dinámicos según las transiciones válidas según el flujo de trabajo
                    uiState.estadosPermitidos.forEach { estado ->
                        Button(
                            onClick = {
                                onCambiarEstado(estado)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Pasar a: ${estado.etiqueta}")
                        }
                    }
                }

                // ----- PANEL DE ASIGNACIÓN (Exclusivo ROL SUPERVISOR) -----
                if (uiState.esSupervisor && !esEstadoFinal(estadoActual)) {
                    Text(
                        text = "Asignar responsable",
                        style = MaterialTheme.typography.titleMedium
                    )
                    uiState.ejecutivos.forEach { ejecutivo ->
                        val esActual = detalle.responsableId == ejecutivo.id
                        if (esActual) {
                            Button(
                                onClick = {
                                    onAsignar(ejecutivo)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("✓ ${ejecutivo.nombre}")
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    onAsignar(ejecutivo)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(ejecutivo.nombre)
                            }
                        }
                    }
                }

                // ----- HISTORIAL Y REGISTRO DE GESTIONES -----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Historial de gestiones",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        onClick = { mostrarDialogoGestion = true }
                    ) {
                        Text("+ Nueva Gestión")
                    }
                }

                if (uiState.historial.isEmpty()) {
                    Text("Aún no hay gestiones registradas")
                }
                // Listado de seguimientos registrados
                uiState.historial.forEach { seguimiento ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Acción: ${seguimiento.accion}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text("Descripción: ${seguimiento.descripcion}")
                            Text("Fecha: ${seguimiento.fecha}")
                            Text("Usuario: ${seguimiento.usuarioNombre}")
                            if (!seguimiento.estadoNuevo.isNullOrBlank()) {
                                Text("Nuevo Estado: ${seguimiento.estadoNuevo}")
                            }
                        }
                    }
                }
            }
        }
    }
}
