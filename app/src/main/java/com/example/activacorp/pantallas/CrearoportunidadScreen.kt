package com.example.activacorp.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// Stateful composable wrapper that integrates with the ViewModel for runtime navigation.
@Composable
fun CrearOportunidadScreen(
    onVolver: () -> Unit = {},
    viewModel: CrearOportunidadViewModel = viewModel()
) {
    // Variable para mantener la sincronización de los estados
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CrearOportunidadContent(
        uiState = uiState,
        onNombreChange = { viewModel.cambiarNombre(it) },
        onOrganizacionChange = { viewModel.cambiarOrganizacion(it) },
        onComunaChange = { viewModel.cambiarComuna(it) },
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
        onGuardar = {},
        onVolver = {}
    )
}
