package com.example.activacorp.pantallas

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage

// Stateful composable wrapper that integrates with the ViewModel for runtime navigation.
@Composable
fun CrearOportunidadScreen(
    onVolver: () -> Unit = {},
    viewModel: CrearOportunidadViewModel = viewModel()
) {
    // Variable para mantener la sincronización de los estados
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Launcher: abre el selector de fotos del sistema y entrega la imagen elegida
    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.cambiarImagen(uri)
        }
    }

    CrearOportunidadContent(
        uiState = uiState,
        onNombreChange = { viewModel.cambiarNombre(it) },
        onOrganizacionChange = { viewModel.cambiarOrganizacion(it) },
        onComunaChange = { viewModel.cambiarComuna(it) },
        onSeleccionarImagen = {
            selectorImagen.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onGuardar = { viewModel.guardar(onGuardado = onVolver) },
        onVolver = onVolver
    )
}

// Stateless content composable separated from ViewModel instantiation to allow preview rendering.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOportunidadContent(
    uiState: CrearOportunidadUiState,
    onNombreChange: (String) -> Unit,
    onOrganizacionChange: (String) -> Unit,
    onComunaChange: (String) -> Unit,
    onSeleccionarImagen: () -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Crear oportunidad")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
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
                        Text("El nombre es obligatorio")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.organizacion,
                onValueChange = onOrganizacionChange,
                label = {
                    Text("Organización")
                },
                isError = uiState.errorOrganizacion,
                supportingText = {
                    if (uiState.errorOrganizacion) {
                        Text("La organización es obligatoria")
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

            // Botón para elegir foto + vista previa
            Button(
                onClick = onSeleccionarImagen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (uiState.imagenUri == null) "Seleccionar imagen"
                    else "Cambiar imagen"
                )
            }

            if (uiState.imagenUri != null) {
                AsyncImage(
                    model = uiState.imagenUri,
                    contentDescription = "Imagen de la oportunidad",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            Button(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}

// Dedicated Preview function rendering the stateless content composable without ViewModel dependencies.
@Preview(showBackground = true)
@Composable
fun CrearOportunidadScreenPreview() {
    CrearOportunidadContent(
        uiState = CrearOportunidadUiState(),
        onNombreChange = {},
        onOrganizacionChange = {},
        onComunaChange = {},
        onSeleccionarImagen = {},
        onGuardar = {},
        onVolver = {}
    )
}