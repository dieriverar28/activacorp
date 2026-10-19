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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * PANTALLA: DashboardScreen (Panel Principal del Sistema)
 * Ofrece una vista diferenciada según el rol del usuario autenticado (Ejecutivo vs Supervisor).
 * Muestra el resumen de métricas clave y sirve como centro de navegación de la aplicación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onIrAOportunidades: () -> Unit = {},  // Navegación al listado de oportunidades
    onIrACrear: () -> Unit = {},          // Navegación al formulario de nueva oportunidad
    onIrAOrganizaciones: () -> Unit = {}, // Navegación al directorio de organizaciones/contactos
    onCerrarSesion: () -> Unit = {},      // Callback para limpiar la sesión y volver al login
    viewModel: DashboardViewModel = viewModel() // Inyección por defecto del ViewModel
) {
    // Sincronización del estado de UI respetando el ciclo de vida de Compose
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estructura contenedora base Material Design 3
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Título adaptativo según el rol detectado en la sesión
                    Text(
                        if (uiState.esSupervisor) "Panel del supervisor" else "Panel del ejecutivo"
                    )
                }
            )
        }
    ) { innerPadding ->
        // Contenedor principal con soporte de Scroll vertical
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Saludo personalizado y etiqueta del rol del usuario activo
            Text(
                text = "Hola, ${uiState.nombre}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(uiState.rolEtiqueta)

            // Tarjeta Informativa (Resumen de Métricas)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Resumen",
                        style = MaterialTheme.typography.titleMedium
                    )

                    // Renderizado condicional de contadores según el perfil de usuario
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

            // Acciones de Navegación del Sistema
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

            // Control de Permisos en UI: Solo el rol Ejecutivo puede registrar nuevas oportunidades
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

            Button(
                onClick = {
                    onIrAOrganizaciones()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Organizaciones y contactos")
            }

            // Cierre de Sesión y limpieza del estado en el ViewModel
            OutlinedButton(
                onClick = {
                    viewModel.cerrarSesion()
                    onCerrarSesion()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}