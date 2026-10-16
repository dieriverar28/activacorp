package com.example.activacorp.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginContent(
        uiState = uiState,
        onCorreoChange = viewModel::cambiarCorreo,
        onClaveChange = viewModel::cambiarClave,
        onIniciarSesion = { viewModel.iniciarSesion(onExito = onLoginExitoso) }
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onCorreoChange: (String) -> Unit = {},
    onClaveChange: (String) -> Unit = {},
    onIniciarSesion: () -> Unit = {}
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ActivaCorp",
                style = MaterialTheme.typography.headlineMedium
            )
            Text("Inicia sesión para continuar")

            OutlinedTextField(
                value = uiState.correo,
                onValueChange = onCorreoChange,
                label = {
                    Text("Correo")
                },
                singleLine = true,
                isError = uiState.errorCorreo,
                supportingText = {
                    if (uiState.errorCorreo) {
                        Text("Ingresa un correo válido")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.clave,
                onValueChange = onClaveChange,
                label = {
                    Text("Contraseña")
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.errorClave,
                supportingText = {
                    if (uiState.errorClave) {
                        Text("La contraseña es obligatoria")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.errorCredenciales) {
                Text(
                    text = "Correo o contraseña incorrectos",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.cargando) {
                CircularProgressIndicator()
            }

            Button(
                onClick = onIniciarSesion,
                enabled = !uiState.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            // Cuentas ficticias para probar la aplicación
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Cuentas de prueba",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("Ejecutivo: camila.rojas@agencia.cl")
                    Text("Ejecutivo: diego.soto@agencia.cl")
                    Text("Ejecutivo: valentina.perez@agencia.cl")
                    Text("Contraseña ejecutivos: ejecutivo123")
                    Text("Supervisor: marcos.fuentes@agencia.cl")
                    Text("Contraseña supervisor: supervisor123")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginContent(
        uiState = LoginUiState()
    )
}
