package com.example.activacorp.pantallas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activacorp.model.Prioridad
import com.example.activacorp.util.cargarImagenPrevia

@Composable
fun CrearOportunidadScreen(
    onVolver: () -> Unit = {},
    viewModel: CrearOportunidadViewModel = viewModel()
) {
    // variables para mantener la sincronización de los estados
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()

    CrearOportunidadContent(
        uiState = uiState,
        organizaciones = organizaciones,
        onNombreChange = viewModel::cambiarNombre,
        onTipoEventoChange = viewModel::cambiarTipoEvento,
        onFechaEventoChange = viewModel::cambiarFechaEvento,
        onUbicacionChange = viewModel::cambiarUbicacion,
        onComunaChange = viewModel::cambiarComuna,
        onPublicoEsperadoChange = viewModel::cambiarPublicoEsperado,
        onObservacionesChange = viewModel::cambiarObservaciones,
        onPrioridadChange = viewModel::cambiarPrioridad,
        onOrganizacionSelect = viewModel::seleccionarOrganizacion,
        onImagenSelect = viewModel::seleccionarImagen,
        onImagenQuitar = viewModel::quitarImagen,
        onGuardar = { viewModel.guardar(onGuardado = onVolver) },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOportunidadContent(
    uiState: CrearOportunidadUiState,
    organizaciones: List<com.example.activacorp.model.Organizacion> = emptyList(),
    onNombreChange: (String) -> Unit = {},
    onTipoEventoChange: (String) -> Unit = {},
    onFechaEventoChange: (String) -> Unit = {},
    onUbicacionChange: (String) -> Unit = {},
    onComunaChange: (String) -> Unit = {},
    onPublicoEsperadoChange: (String) -> Unit = {},
    onObservacionesChange: (String) -> Unit = {},
    onPrioridadChange: (Prioridad) -> Unit = {},
    onOrganizacionSelect: (Int) -> Unit = {},
    onImagenSelect: (Uri) -> Unit = {},
    onImagenQuitar: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    // Selector de imágenes de la galería (no necesita permisos)
    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onImagenSelect(uri)
        }
    }

    // Vista previa de la foto ya guardada
    val imagenPrevia = remember(uiState.fotoUri) {
        uiState.fotoUri?.let { cargarImagenPrevia(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Registrar oportunidad")
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

            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = onNombreChange,
                label = {
                    Text("Nombre de la oportunidad")
                },
                isError = uiState.errorNombre,
                supportingText = {
                    if (uiState.errorNombre) {
                        Text("El nombre debe tener al menos 3 caracteres")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.tipoEvento,
                onValueChange = onTipoEventoChange,
                label = {
                    Text("Tipo de evento")
                },
                isError = uiState.errorTipoEvento,
                supportingText = {
                    if (uiState.errorTipoEvento) {
                        Text("El tipo de evento es obligatorio")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.fechaEvento,
                onValueChange = onFechaEventoChange,
                label = {
                    Text("Fecha del evento (AAAA-MM-DD)")
                },
                isError = uiState.errorFechaEvento,
                supportingText = {
                    if (uiState.errorFechaEvento) {
                        Text("Ingresa una fecha válida y que no sea anterior a hoy")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.ubicacion,
                onValueChange = onUbicacionChange,
                label = {
                    Text("Ubicación")
                },
                isError = uiState.errorUbicacion,
                supportingText = {
                    if (uiState.errorUbicacion) {
                        Text("La ubicación es obligatoria")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.comuna,
                onValueChange = onComunaChange,
                label = {
                    Text("Comuna")
                },
                isError = uiState.errorComuna,
                supportingText = {
                    if (uiState.errorComuna) {
                        Text("La comuna es obligatoria")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.publicoEsperado,
                onValueChange = onPublicoEsperadoChange,
                label = {
                    Text("Público esperado")
                },
                isError = uiState.errorPublicoEsperado,
                supportingText = {
                    if (uiState.errorPublicoEsperado) {
                        Text("Ingresa un número mayor que cero")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = onObservacionesChange,
                label = {
                    Text("Observaciones (opcional)")
                },
                modifier = Modifier.fillMaxWidth()
            )

            // ----- Foto del evento -----
            Text(
                text = "Foto del evento (opcional)",
                style = MaterialTheme.typography.titleMedium
            )

            if (imagenPrevia != null) {
                Image(
                    bitmap = imagenPrevia,
                    contentDescription = "Vista previa de la foto del evento",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            OutlinedButton(
                onClick = {
                    lanzadorGaleria.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (uiState.fotoUri == null) "Elegir de la galería" else "Cambiar foto"
                )
            }

            if (uiState.fotoUri != null) {
                OutlinedButton(
                    onClick = onImagenQuitar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quitar foto")
                }
            }

            if (uiState.errorImagen) {
                Text(
                    text = "No se pudo cargar la imagen. Intenta con otra.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // ----- Prioridad -----
            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Prioridad.entries.forEach { prioridad ->
                    if (uiState.prioridad == prioridad) {
                        Button(
                            onClick = {
                                onPrioridadChange(prioridad)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(prioridad.etiqueta)
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                onPrioridadChange(prioridad)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(prioridad.etiqueta)
                        }
                    }
                }
            }

            // ----- Organización -----
            Text(
                text = "Organización",
                style = MaterialTheme.typography.titleMedium
            )
            organizaciones.forEach { organizacion ->
                if (uiState.organizacionId == organizacion.id) {
                    Button(
                        onClick = {
                            onOrganizacionSelect(organizacion.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("✓ ${organizacion.nombre}")
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            onOrganizacionSelect(organizacion.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(organizacion.nombre)
                    }
                }
            }
            if (uiState.errorOrganizacion) {
                Text(
                    text = "Selecciona una organización",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.mensajeError != null) {
                Text(
                    text = uiState.mensajeError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CrearOportunidadScreenPreview() {
    CrearOportunidadContent(
        uiState = CrearOportunidadUiState(),
        organizaciones = listOf(
            com.example.activacorp.model.Organizacion(id = 1, nombre = "Empresa Ejemplo", tipo = "Empresa Privada", comuna = "Santiago")
        )
    )
}
