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

/**
 * PANTALLA: LoginScreen (Stateful Composable)
 * Conecta el estado del LoginViewModel con la interfaz visual de autenticación.
 * Observa el flujo del UI State respetando el ciclo de vida de la aplicación mediante collectAsStateWithLifecycle.
 */
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit = {}, // Callback de navegación al autenticar correctamente
    viewModel: LoginViewModel = viewModel() // Inyección por defecto del ViewModel
) {
    // Suscripción segura al estado reactivo expuesto por el ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Delegación del renderizado en el componente Stateless de presentación
    LoginContent(
        uiState = uiState,
        onCorreoChange = viewModel::cambiarCorreo,
        onClaveChange = viewModel::cambiarClave,
        onIniciarSesion = { viewModel.iniciarSesion(onExito = onLoginExitoso) }
    )
}

/**
 * PANTALLA: LoginContent (Stateless Composable)
 * Construye la interfaz gráfica del formulario de inicio de sesión.
 * Recibe el estado actual (uiState) y emite los eventos mediante funciones lambda.
 */
@Composable
fun LoginContent(
    uiState: LoginUiState,
    onCorreoChange: (String) -> Unit = {},
    onClaveChange: (String) -> Unit = {},
    onIniciarSesion: () -> Unit = {}
) {
    // Estructura base de pantalla según especificaciones de Material Design 3
    Scaffold { innerPadding ->
        // Contenedor principal con soporte de desplazamiento vertical (Scroll)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la aplicación e instrucción de acceso
            Text(
                text = "ActivaCorp",
                style = MaterialTheme.typography.headlineMedium
            )
            Text("Inicia sesión para continuar")

            // Campo de entrada: Correo Electrónico (Con validación de formato)
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

            // Campo de entrada: Contraseña (Con enmascaramiento de caracteres)
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

            // Alerta visual ante credenciales inválidas o no encontradas en Room
            if (uiState.errorCredenciales) {
                Text(
                    text = "Correo o contraseña incorrectos",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Indicador de progreso durante la verificación asíncrona de credenciales
            if (uiState.cargando) {
                CircularProgressIndicator()
            }

            // Botón Principal: Ejecutar inicio de sesión
            Button(
                onClick = onIniciarSesion,
                enabled = !uiState.cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ingresar")
            }

            // Tarjeta Informativa: Cuentas ficticias precargadas para pruebas y demostración
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

/**
 * PREVIEW: Renderizado de prueba estática para la pantalla de inicio de sesión en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginContent(
        uiState = LoginUiState()
    )
}