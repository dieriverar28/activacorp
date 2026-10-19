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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.Organizacion

/**
 * PANTALLA: DetalleOrganizacionScreen (Stateful Composable)
 * Conecta el estado del DetalleOrganizacionViewModel con la interfaz de usuario.
 * Observa de manera reactiva el ciclo de vida de la app mediante collectAsStateWithLifecycle.
 */
@Composable
fun DetalleOrganizacionScreen(
    onIrACrearContacto: (Int) -> Unit = {}, // Callback para navegar a la pantalla de nuevo contacto
    onVolver: () -> Unit = {},             // Callback para regresar al directorio
    viewModel: DetalleOrganizacionViewModel = viewModel() // Inyección por defecto del ViewModel
) {
    // Sincronización del estado UI reactivo
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Renderizado del componente Stateless de presentación
    DetalleOrganizacionContent(
        uiState = uiState,
        onIrACrearContacto = onIrACrearContacto,
        onVolver = onVolver
    )
}

/**
 * PANTALLA: DetalleOrganizacionContent (Stateless Composable)
 * Muestra la ficha detallada de una organización específica y la lista de contactos asociados.
 * Permite la navegación hacia la creación de un nuevo contacto pasando el id de la entidad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOrganizacionContent(
    uiState: DetalleOrganizacionUiState,
    onIrACrearContacto: (Int) -> Unit = {},
    onVolver: () -> Unit = {}
) {
    val organizacion = uiState.organizacion

    // Estructura base Scaffold con la barra superior de Material Design 3
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Organización")
                }
            )
        }
    ) { innerPadding ->
        // Contenedor vertical con soporte de Scroll
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Control de Estados: Carga, inexistencia o despliegue de datos
            if (uiState.cargando) {
                CircularProgressIndicator() // Indicador visual durante la lectura en Room
            } else if (organizacion == null) {
                Text("La organización no existe")
            } else {
                // ----- FICHA TÉCNICA DE LA ORGANIZACIÓN -----
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = organizacion.nombre,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text("Tipo: ${organizacion.tipo}")
                        Text("Comuna: ${organizacion.comuna}")
                    }
                }

                // ----- SECCIÓN DE CONTACTOS ASOCIADOS -----
                Text(
                    text = "Contactos",
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.contactos.isEmpty()) {
                    Text("Esta organización aún no tiene contactos")
                }

                // Despliegue de tarjetas con los datos de cada contacto corporativo
                uiState.contactos.forEach { contacto ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = contacto.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(contacto.cargo)
                            Text(contacto.correo)
                            Text(contacto.telefono)
                        }
                    }
                }

                // Botón para agregar un nuevo contacto vinculado a esta organización
                Button(
                    onClick = {
                        onIrACrearContacto(organizacion.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar contacto")
                }
            }

            // Botón secundario para regresar al listado general
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

/**
 * PREVIEW: Renderizado de prueba estática del detalle de organización en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun DetalleOrganizacionScreenPreview() {
    DetalleOrganizacionContent(
        uiState = DetalleOrganizacionUiState(
            cargando = false,
            organizacion = Organizacion(
                id = 1,
                nombre = "Municipalidad Ejemplo",
                tipo = "Municipalidad",
                comuna = "Santiago"
            ),
            contactos = listOf(
                Contacto(
                    id = 1,
                    organizacionId = 1,
                    nombre = "Juan Pérez",
                    cargo = "Jefe de Eventos",
                    correo = "juan.perez@ejemplo.cl",
                    telefono = "+56 9 1234 5678"
                )
            )
        )
    )
}