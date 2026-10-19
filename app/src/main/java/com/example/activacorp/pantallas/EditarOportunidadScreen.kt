package com.example.activacorp.pantallas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * PANTALLA: EditarOportunidadScreen (Formulario de Edición)
 * Permite la modificación de parámetros clave de una oportunidad comercial ya registrada.
 * Utiliza estado recordado localmente (remember/mutableStateOf) para la captura de cambios en UI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarOportunidadScreen(
    oportunidadId: Int,             // Identificador de la oportunidad a modificar
    onVolver: () -> Unit = {},       // Callback para cancelar y regresar a la vista anterior
    onGuardarExito: () -> Unit = {}  // Callback tras confirmar el guardado exitoso
) {
    // Estados locales temporales para la captura de datos en el formulario
    var nombre by remember { mutableStateOf("Oportunidad Existente") }
    var ubicacion by remember { mutableStateOf("Santiago Central") }
    var publicoEsperado by remember { mutableStateOf("200") }

    // Estructura base Scaffold con barra superior dinámica según el ID
    Scaffold(
        topBar = { TopAppBar(title = { Text("Editar Oportunidad #$oportunidadId") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Campo de texto: Modificación del Nombre del Evento
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del evento") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de texto: Modificación de Ubicación o Comuna
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación / Comuna") },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo de texto: Modificación del Estimado de Asistentes
            OutlinedTextField(
                value = publicoEsperado,
                onValueChange = { publicoEsperado = it },
                label = { Text("Público esperado") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Principal: Confirmar y Persistir Cambios
            Button(
                onClick = { onGuardarExito() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }

            // Botón Secundario: Cancelar Operación
            OutlinedButton(
                onClick = onVolver,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar")
            }
        }
    }
}