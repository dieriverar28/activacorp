package com.example.activacorp.pantallas

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

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesScreen(
    onIrACrear: () -> Unit = {},
    onVolver: () -> Unit = {},
    viewModel: OportunidadesViewModel = viewModel()
) {
    val oportunidades by viewModel.oportunidades.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Oportunidades")
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

            if (oportunidades.isEmpty()) {
                Text("Aún no hay oportunidades registradas")
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(oportunidades) { oportunidad ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = oportunidad.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = oportunidad.organizacion
                            )
                            Text(
                                text = oportunidad.comuna
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    onIrACrear()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear oportunidad")
            }

            Button(
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
