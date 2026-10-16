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



@Composable
fun DashboardScreen(
    onIrAOportunidades: () -> Unit = {},
    onIrACrear: () -> Unit = {},
    onCerrarSesion: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onIrAOportunidades = onIrAOportunidades,
        onIrACrear = onIrACrear,
        onCerrarSesion = {
            viewModel.cerrarSesion()
            onCerrarSesion()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onIrAOportunidades: () -> Unit = {},
    onIrACrear: () -> Unit = {},
    onCerrarSesion: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.esSupervisor) "Panel del supervisor" else "Panel del ejecutivo"
                    )
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
            Text(
                text = "Hola, ${uiState.nombre}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(uiState.rolEtiqueta)

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Resumen",
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (uiState.esSupervisor) {
                        Text("Oportunidades totales: ${uiState.total}")
                        Text("Sin responsable asignado: ${uiState.sinAsignar}")
                        Text("Pendientes de tu decisión: ${uiState.enEspera}")
                        Text("Eventos próximos: ${uiState.proximas}")
                    } else {
                        Text("Mis oportunidades: ${uiState.total}")
                        Text("Eventos próximos: ${uiState.proximas}")
                        Text("En espera de respuesta: ${uiState.enEspera}")
                    }
                }
            }

            Button(
                onClick = {
                    onIrAOportunidades()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (uiState.esSupervisor) "Ver todas las oportunidades" else "Ver mis oportunidades"
                )
            }

            // Solo el ejecutivo registra oportunidades
            if (!uiState.esSupervisor) {
                Button(
                    onClick = {
                        onIrACrear()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar oportunidad")
                }
            }

            OutlinedButton(
                onClick = {
                    onCerrarSesion()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardScreenPreview() {
    DashboardContent(
        uiState = DashboardUiState(
            nombre = "Camila Rojas",
            rolEtiqueta = "Ejecutivo",
            esSupervisor = false,
            total = 5,
            proximas = 2,
            enEspera = 1,
            sinAsignar = 0
        )
    )
}
