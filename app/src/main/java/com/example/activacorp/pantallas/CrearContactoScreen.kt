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

/**
 * PANTALLA: CrearContactoScreen (Contenedor de Estado / Stateful Composable)
 * Se encarga de conectar la lógica de negocio del ViewModel con la interfaz de usuario.
 * Maneja la recolección del estado de manera reactiva respetando el ciclo de vida de la aplicación.
 */
@Composable
fun CrearContactoScreen(
    onVolver: () -> Unit = {}, // Callback para navegar hacia la pantalla anterior
    viewModel: CrearContactoViewModel = viewModel() // Inyección por defecto del ViewModel
) {
    // Recolecta el flujo del estado UI (StateFlow) de forma segura en relación al ciclo de vida
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Delega la renderización de la interfaz al componente Stateless
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

/**
 * PANTALLA: CrearContactoContent (Componente de Presentación / Stateless Composable)
 * Contiene la estructura visual y los campos del formulario para el registro de nuevos contactos.
 * Recibe el estado actual y emite eventos hacia el nivel superior mediante lambdas.
 */
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
    // Scaffold provee la estructura base de Material Design (barra superior, contenido, padding)
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Nuevo contacto")
                }
            )
        }
    ) { innerPadding ->
        // Contenedor principal con soporte para desplazamiento vertical (Scroll)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite desplazamiento si la pantalla es pequeña
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espaciado uniforme entre campos
        ) {

            // Campo de Entrada: Nombre del Contacto con validación de longitud
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

            // Campo de Entrada: Cargo o Área dentro de la empresa
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

            // Campo de Entrada: Correo Electrónico (Validación de formato y duplicidad por cliente)
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

            // Campo de Entrada: Teléfono de Contacto (Validación de longitud de dígitos)
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

            // Botón de Acción Principal: Confirmar y Guardar
            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            // Botón Secundario: Cancelar o Regresar a la vista previa
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver")
            }
        }
    }
}

/**
 * PREVIEW: Renderización estática del formulario de creación de contactos para Android Studio Preview.
 */
@Preview(showBackground = true)
@Composable
fun CrearContactoScreenPreview() {
    CrearContactoContent(
        uiState = CrearContactoUiState()
    )
}