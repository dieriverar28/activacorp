package com.example.activacorp.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Prioridad
import com.example.activacorp.util.cargarImagenPrevia
import com.example.activacorp.util.esEstadoFinal

import androidx.compose.ui.tooling.preview.Preview
import com.example.activacorp.model.OportunidadConDatos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOportunidadScreen(
    onVolver: () -> Unit = {},
    viewModel: DetalleOportunidadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetalleOportunidadContent(
        uiState = uiState,
        onCambiarEstado = viewModel::cambiarEstado,
        onAsignar = viewModel::asignar,
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOportunidadContent(
    uiState: DetalleUiState,
    onCambiarEstado: (EstadoOportunidad) -> Unit = {},
    onAsignar: (com.example.activacorp.model.Usuario) -> Unit = {},
    onVolver: () -> Unit = {}
) {
    val detalle = uiState.detalle

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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            if (uiState.cargando) {
                CircularProgressIndicator()
            } else if (detalle == null) {
                Text("La oportunidad no existe")
            } else if (!uiState.tieneAcceso) {
                Text(
                    text = "No tienes acceso a esta oportunidad",
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val estadoActual = EstadoOportunidad.valueOf(detalle.estado)

                // ----- Información completa -----
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
                        if (detalle.observaciones.isNotBlank()) {
                            Text("Observaciones: ${detalle.observaciones}")
                        }
                    }
                }

                // ----- Foto del evento (si tiene) -----
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

                if (uiState.mensaje != null) {
                    Text(
                        text = uiState.mensaje ?: "",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // ----- Cambio de estado (según rol y transiciones permitidas) -----
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

                // ----- Asignación (solo supervisor) -----
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

                // ----- Historial de gestiones -----
                Text(
                    text = "Historial de gestiones",
                    style = MaterialTheme.typography.titleMedium
                )
                if (uiState.historial.isEmpty()) {
                    Text("Aún no hay gestiones registradas")
                }
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
}

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
