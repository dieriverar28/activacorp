package com.example.activacorp.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PANTALLA: PanelSupervisorScreen (Vista Global de Supervisión)
 * Proporciona un panel exclusivo para el rol Supervisor con métricas globales del equipo commercial,
 * visualización de asignaciones pendientes y control de decisiones estratégicas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelSupervisorScreen(
    onVolver: () -> Unit = {} // Callback para regresar a la pantalla principal (Dashboard)
) {
    // Estructura contenedora de la pantalla con la barra superior de Material Design 3
    Scaffold(
        topBar = { TopAppBar(title = { Text("Panel Global del Supervisor") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título de la sección de métricas globales
            Text(
                text = "Gestión Global y Reasignación de Ejecutivos",
                style = MaterialTheme.typography.titleMedium
            )

            // Tarjeta informativa con métricas de supervisión y control de cartera
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Total Oportunidades Activas: 12")
                    Text("Sin Responsable Asignado: 3")
                    Text("Gestiones Atrasadas: 2")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de navegación para retornar al Dashboard
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver al Dashboard")
            }
        }
    }
}