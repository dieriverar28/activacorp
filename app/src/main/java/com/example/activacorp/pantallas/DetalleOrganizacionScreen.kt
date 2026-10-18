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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.activacorp.model.Contacto
import com.example.activacorp.model.Organizacion

@Composable
fun DetalleOrganizacionScreen(
    onIrACrearContacto: (Int) -> Unit = {},
    onVolver: () -> Unit = {},
    viewModel: DetalleOrganizacionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetalleOrganizacionContent(
        uiState = uiState,
        onIrACrearContacto = onIrACrearContacto,
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleOrganizacionContent(
    uiState: DetalleOrganizacionUiState,
    onIrACrearContacto: (Int) -> Unit = {},
    onVolver: () -> Unit = {}
) {
    val organizacion = uiState.organizacion

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Organización")
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

            if (uiState.cargando) {
                CircularProgressIndicator()
            } else if (organizacion == null) {
                Text("La organización no existe")
            } else {
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

                Text(
                    text = "Contactos",
                    style = MaterialTheme.typography.titleMedium
                )

                if (uiState.contactos.isEmpty()) {
                    Text("Esta organización aún no tiene contactos")
                }

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

                Button(
                    onClick = {
                        onIrACrearContacto(organizacion.id)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agregar contacto")
                }
            }

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
