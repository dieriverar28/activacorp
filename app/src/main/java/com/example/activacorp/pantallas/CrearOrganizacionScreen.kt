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
 * PANTALLA: CrearOrganizacionScreen (Stateful Composable)
 * Contenedor de estado que conecta el ViewModel de creación de organizaciones con la interfaz de usuario.
 * Recolecta el estado reactivo respetando el ciclo de vida de Android mediante collectAsStateWithLifecycle.
 */
@Composable
fun CrearOrganizacionScreen(
    onVolver: () -> Unit = {}, // Callback para navegar hacia la vista previa
    viewModel: CrearOrganizacionViewModel = viewModel() // Inyección por defecto del ViewModel
) {
    // Sincronización del estado de la UI expuesto por el ViewModel
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Renderizado del componente Stateless de presentación
    CrearOrganizacionContent(
        uiState = uiState,
        onNombreChange = viewModel::cambiarNombre,
        onTipoChange = viewModel::cambiarTipo,
        onComunaChange = viewModel::cambiarComuna,
        onGuardar = { viewModel.guardar(onGuardado = onVolver) },
        onVolver = onVolver
    )
}

/**
 * PANTALLA: CrearOrganizacionContent (Stateless Composable)
 * Dibuja el formulario de alta para nuevas organizaciones o clientes corporativos.
 * Recibe el estado actual (uiState) y emite los eventos a través de funciones lambda.
 */
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
    // Estructura base de pantalla según guías de Material Design 3
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Nueva organización")
                }
            )
        }
    ) { innerPadding ->
        // Contenedor principal con soporte de desplazamiento vertical (Scroll)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espaciado entre elementos
        ) {

            // Campo de Entrada: Nombre de la organización (Con validación de formato y duplicidad)
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

            // Campo de Entrada: Tipo de organización (Municipalidad, Empresa, Colegio, etc.)
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

            // Campo de Entrada: Comuna territorial asignada a la entidad
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

            // Botón de Acción Principal: Validar y registrar la organización
            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            // Botón Secundario: Cancelar o volver a la vista anterior
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
 * PREVIEW: Renderizado de prueba estática para el diseño de la interfaz en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun CrearOrganizacionScreenPreview() {
    CrearOrganizacionContent(
        uiState = CrearOrganizacionUiState()
    )
}