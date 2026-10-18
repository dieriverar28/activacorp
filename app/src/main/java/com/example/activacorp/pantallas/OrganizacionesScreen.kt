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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview
import com.example.activacorp.model.OrganizacionResumen

@Composable
fun OrganizacionesScreen(
    onIrADetalle: (Int) -> Unit = {},
    onIrACrear: () -> Unit = {},
    onVolver: () -> Unit = {},
    viewModel: OrganizacionesViewModel = viewModel()
) {
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()

    OrganizacionesContent(
        organizaciones = organizaciones,
        onIrADetalle = onIrADetalle,
        onIrACrear = onIrACrear,
        onVolver = onVolver
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionesContent(
    organizaciones: List<OrganizacionResumen> = emptyList(),
    onIrADetalle: (Int) -> Unit = {},
    onIrACrear: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Organizaciones")
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

            if (organizaciones.isEmpty()) {
                Text("Aún no hay organizaciones registradas")
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(organizaciones) { organizacion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIrADetalle(organizacion.id)
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = organizacion.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("${organizacion.tipo} · ${organizacion.comuna}")
                            Text("Contactos: ${organizacion.totalContactos}")
                        }
                    }
                }
            }

            Button(
                onClick = onIrACrear,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nueva organización")
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
fun OrganizacionesScreenPreview() {
    OrganizacionesContent(
        organizaciones = listOf(
            OrganizacionResumen(
                id = 1,
                nombre = "Municipalidad Ejemplo",
                tipo = "Municipalidad",
                comuna = "Santiago",
                totalContactos = 3
            )
        )
    )
}
