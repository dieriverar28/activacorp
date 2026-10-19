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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.OportunidadConDatos
import com.example.activacorp.model.Prioridad
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
                        Text("Estado: ${estadoActual.etiqueta}")
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

                // Despliegue de mensajes informativos del sistema
                if (uiState.mensaje != null) {
                    Text(
                        text = uiState.mensaje ?: "",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

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
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = "${seguimiento.fecha} · ${seguimiento.usuarioNombre}",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(seguimiento.descripcion)
                        }
                    }
                }
            }

            // Botón de Retorno
            OutlinedButton(
                onClick = {
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }

    // Modal emergente (AlertDialog) para la creación de un nuevo evento/gestión de seguimiento
    if (mostrarDialogoGestion) {
        DialogoNuevaGestion(
            onDismiss = { mostrarDialogoGestion = false },
            onGuardar = { tipo, desc, fechaProx ->
                onAgregarGestion(tipo, desc, fechaProx)
                mostrarDialogoGestion = false
            }
        )
    }
}

/**
 * COMPONENTE DIÁLOGO: Modal interactivo para el registro de llamadas, reuniones o próximas fechas.
 */
@Composable
fun DialogoNuevaGestion(
    onDismiss: () -> Unit,
    onGuardar: (tipo: String, descripcion: String, fechaProximo: String) -> Unit
) {
    var tipoAccion by remember { mutableStateOf("Llamada") }
    var descripcion by remember { mutableStateOf("") }
    var fechaProximo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Nueva Gestión") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = tipoAccion,
                    onValueChange = { tipoAccion = it },
                    label = { Text("Tipo de Acción") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Detalle / Observación") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fechaProximo,
                    onValueChange = { fechaProximo = it },
                    label = { Text("Próxima fecha (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (descripcion.isNotBlank() && fechaProximo.isNotBlank()) {
                        onGuardar(tipoAccion, descripcion, fechaProximo)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * PREVIEW: Vista previa interactiva de la pantalla de detalle de oportunidad en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun DetalleOportunidadScreenPreview() {
    DetalleOportunidadContent(
        uiState = DetalleUiState(
            cargando = false,
            tieneAcceso = true,
            detalle = OportunidadConDatos(
                id = 1,
                nombre = "Seminario Corporativo 2026",
                tipoEvento = "Seminario",
                fechaEvento = "2026-11-15",
                ubicacion = "Hotel Central",
                comuna = "Santiago",
                publicoEsperado = 150,
                prioridad = Prioridad.ALTA.name,
                estado = EstadoOportunidad.EVENTO_IDENTIFICADO.name,
                observaciones = "Sin observaciones",
                fotoUri = null,
                organizacionId = 1,
                organizacionNombre = "Empresa Ejemplo",
                responsableId = 1,
                responsableNombre = "Camila Rojas"
            )
        )
    )
}