package com.example.activacorp.pantallas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Prioridad


@Composable
fun OportunidadesScreen(
    onIrADetalle: (Int) -> Unit = {},
    onIrACrear: () -> Unit = {},
    onVolver: () -> Unit = {},
    viewModel: OportunidadesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    OportunidadesContent(
        uiState = uiState,
        onIrADetalle = onIrADetalle,
        onIrACrear = onIrACrear,
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesContent(
    uiState: OportunidadesUiState,
    onIrADetalle: (Int) -> Unit = {},
    onIrACrear: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.esSupervisor) "Todas las oportunidades" else "Mis oportunidades"
                    )
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

            if (uiState.lista.isEmpty()) {
                Text("Aún no hay oportunidades registradas")
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.lista) { oportunidad ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIrADetalle(oportunidad.id)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = oportunidad.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(oportunidad.organizacionNombre)
                            Text("Estado: ${EstadoOportunidad.valueOf(oportunidad.estado).etiqueta}")
                            Text("Prioridad: ${Prioridad.valueOf(oportunidad.prioridad).etiqueta}")
                            Text("Fecha del evento: ${oportunidad.fechaEvento}")
                            Text("Responsable: ${oportunidad.responsableNombre ?: "Sin asignar"}")
                        }
                    }
                }
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
fun OportunidadesScreenPreview() {
    OportunidadesContent(
        uiState = OportunidadesUiState()
    )
}
