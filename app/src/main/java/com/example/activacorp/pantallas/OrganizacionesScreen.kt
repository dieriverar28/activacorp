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
import com.example.activacorp.model.OrganizacionResumen

/**
 * PANTALLA: OrganizacionesScreen (Stateful Composable)
 * Conecta el estado reactivo expuesto por OrganizacionesViewModel con la interfaz visual.
 * Sincroniza la recolección de datos con el ciclo de vida de Compose mediante collectAsStateWithLifecycle.
 */
@Composable
fun OrganizacionesScreen(
    onIrADetalle: (Int) -> Unit = {}, // Callback para navegar a la ficha detallada de la organización
    onIrACrear: () -> Unit = {},      // Callback para abrir el formulario de nueva organización
    onVolver: () -> Unit = {},         // Callback para regresar al Dashboard
    viewModel: OrganizacionesViewModel = viewModel() // Inyección predeterminada del ViewModel
) {
    // Suscripción segura al flujo de organizaciones con resumen de contactos
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()

    // Renderizado del componente Stateless de presentación
    OrganizacionesContent(
        organizaciones = organizaciones,
        onIrADetalle = onIrADetalle,
        onIrACrear = onIrACrear,
        onVolver = onVolver
    )
}

/**
 * PANTALLA: OrganizacionesContent (Stateless Composable)
 * Presenta el directorio de organizaciones mediante un listado optimizado (LazyColumn).
 * Muestra el nombre, tipo, comuna y total de contactos asociados por cada entidad.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizacionesContent(
    organizaciones: List<OrganizacionResumen> = emptyList(),
    onIrADetalle: (Int) -> Unit = {},
    onIrACrear: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    // Estructura base de pantalla según guías de Material Design 3
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

            // Manejo de estado vacío cuando no existen registros en Room
            if (organizaciones.isEmpty()) {
                Text("Aún no hay organizaciones registradas")
            }

            // Lista de reciclaje eficiente (LazyColumn) para el directorio corporativo
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(organizaciones) { organizacion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIrADetalle(organizacion.id) // Navega al detalle pasando el ID
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

            // Botón de Acción Principal: Registrar una nueva organización
            Button(
                onClick = onIrACrear,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Nueva organización")
            }

            // Botón Secundario: Retorno a la pantalla anterior
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
 * PREVIEW: Renderizado de prueba estática del directorio de organizaciones en Android Studio.
 */
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