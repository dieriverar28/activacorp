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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun CrearContactoScreen(
    onVolver: () -> Unit = {},
    viewModel: CrearContactoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CrearContactoContent(
        uiState = uiState,
        onNombreChange = viewModel::cambiarNombre,
        onCargoChange = viewModel::cambiarCargo,
        onCorreoChange = viewModel::cambiarCorreo,
        onTelefonoChange = viewModel::cambiarTelefono,
        onGuardar = { viewModel.guardar(onGuardado = onVolver) },
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearContactoContent(
    uiState: CrearContactoUiState,
    onNombreChange: (String) -> Unit = {},
    onCargoChange: (String) -> Unit = {},
    onCorreoChange: (String) -> Unit = {},
    onTelefonoChange: (String) -> Unit = {},
    onGuardar: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Nuevo contacto")
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
                    Text("Nombre del contacto")
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
                value = uiState.cargo,
                onValueChange = onCargoChange,
                label = {
                    Text("Cargo o área")
                },
                isError = uiState.errorCargo,
                supportingText = {
                    if (uiState.errorCargo) {
                        Text("El cargo o área es obligatorio")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.correo,
                onValueChange = onCorreoChange,
                label = {
                    Text("Correo")
                },
                isError = uiState.errorCorreo || uiState.errorCorreoDuplicado,
                supportingText = {
                    if (uiState.errorCorreoDuplicado) {
                        Text("Ya existe un contacto con ese correo en esta organización")
                    } else if (uiState.errorCorreo) {
                        Text("Ingresa un correo válido")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.telefono,
                onValueChange = onTelefonoChange,
                label = {
                    Text("Teléfono")
                },
                isError = uiState.errorTelefono,
                supportingText = {
                    if (uiState.errorTelefono) {
                        Text("Ingresa un teléfono válido (8 a 12 dígitos, ejemplo: +56 9 5550 0001)")
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
fun CrearContactoScreenPreview() {
    CrearContactoContent(
        uiState = CrearContactoUiState()
    )
}
