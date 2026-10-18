package com.example.activacorp.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CrearOrganizacionScreen(
    onVolver: () -> Unit = {},
    viewModel: CrearOrganizacionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CrearOrganizacionContent(
        uiState = uiState,
        onNombreChange = viewModel::cambiarNombre,
        onTipoChange = viewModel::cambiarTipo,
        onComunaChange = viewModel::cambiarComuna,
        onGuardar = { viewModel.guardar(onGuardado = onVolver) },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOrganizacionContent(
    uiState: CrearOrganizacionUiState,
    onNombreChange: (String) -> Unit = {},
    onTipoChange: (String) -> Unit = {},
    onComunaChange: (String) -> Unit = {},
    onGuardar: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Nueva organización")
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
                    Text("Nombre de la organización")
                },
                isError = uiState.errorNombre || uiState.errorNombreDuplicado,
                supportingText = {
                    if (uiState.errorNombreDuplicado) {
                        Text("Ya existe una organización con ese nombre")
                    } else if (uiState.errorNombre) {
                        Text("El nombre debe tener al menos 3 caracteres")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.tipo,
                onValueChange = onTipoChange,
                label = {
                    Text("Tipo (municipalidad, empresa, colegio...)")
                },
                isError = uiState.errorTipo,
                supportingText = {
                    if (uiState.errorTipo) {
                        Text("El tipo es obligatorio")
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
fun CrearOrganizacionScreenPreview() {
    CrearOrganizacionContent(
        uiState = CrearOrganizacionUiState()
    )
}
