package com.example.activacorp.pantallas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activacorp.model.Prioridad
import com.example.activacorp.util.cargarImagenPrevia

/**
 * PANTALLA: CrearOportunidadScreen (Stateful Composable)
 * Conecta la lógica del ViewModel con la vista para la creación de oportunidades comerciales.
 * Recolecta el estado del formulario y el listado de organizaciones disponibles respetando el ciclo de vida.
 */
@Composable
fun CrearOportunidadScreen(
    onVolver: () -> Unit = {}, // Callback para navegar a la pantalla anterior
    viewModel: CrearOportunidadViewModel = viewModel() // Inyección por defecto del ViewModel
) {
    // Sincronización del estado de la UI y del listado de organizaciones mediante StateFlow
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val organizaciones by viewModel.organizaciones.collectAsStateWithLifecycle()

    // Renderizado del contenido sin estado (Stateless)
    CrearOportunidadContent(
        uiState = uiState,
        organizaciones = organizaciones,
        onNombreChange = viewModel::cambiarNombre,
        onTipoEventoChange = viewModel::cambiarTipoEvento,
        onFechaEventoChange = viewModel::cambiarFechaEvento,
        onUbicacionChange = viewModel::cambiarUbicacion,
        onComunaChange = viewModel::cambiarComuna,
        onPublicoEsperadoChange = viewModel::cambiarPublicoEsperado,
        onObservacionesChange = viewModel::cambiarObservaciones,
        onPrioridadChange = viewModel::cambiarPrioridad,
        onOrganizacionSelect = viewModel::seleccionarOrganizacion,
        onImagenSelect = viewModel::seleccionarImagen,
        onImagenQuitar = viewModel::quitarImagen,
        onGuardar = { viewModel.guardar(onGuardado = onVolver) },
        onVolver = onVolver
    )
}

/**
 * PANTALLA: CrearOportunidadContent (Stateless Composable)
 * Contiene el formulario completo de registro de oportunidades, manejo de fotos mediante galería/cámara
 * y selección de prioridades u organizaciones.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearOportunidadContent(
    uiState: CrearOportunidadUiState,
    organizaciones: List<com.example.activacorp.model.Organizacion> = emptyList(),
    onNombreChange: (String) -> Unit = {},
    onTipoEventoChange: (String) -> Unit = {},
    onFechaEventoChange: (String) -> Unit = {},
    onUbicacionChange: (String) -> Unit = {},
    onComunaChange: (String) -> Unit = {},
    onPublicoEsperadoChange: (String) -> Unit = {},
    onObservacionesChange: (String) -> Unit = {},
    onPrioridadChange: (Prioridad) -> Unit = {},
    onOrganizacionSelect: (Int) -> Unit = {},
    onImagenSelect: (Uri) -> Unit = {},
    onImagenQuitar: () -> Unit = {},
    onGuardar: () -> Unit = {},
    onVolver: () -> Unit = {}
) {
    // RECURSO NATIVO: Launcher para seleccionar medios visuales (Galería) sin requerir permisos explícitos
    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onImagenSelect(uri)
        }
    }

    // Carga óptima de la vista previa de la foto seleccionada utilizando memorización
    val imagenPrevia = remember(uiState.fotoUri) {
        uiState.fotoUri?.let { cargarImagenPrevia(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Registrar oportunidad")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()), // Permite desplazamiento en pantallas reducidas
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Campo: Nombre de la oportunidad
            OutlinedTextField(
                value = uiState.nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre de la oportunidad") },
                isError = uiState.errorNombre,
                supportingText = {
                    if (uiState.errorNombre) {
                        Text("El nombre debe tener al menos 3 caracteres")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Tipo de evento
            OutlinedTextField(
                value = uiState.tipoEvento,
                onValueChange = onTipoEventoChange,
                label = { Text("Tipo de evento") },
                isError = uiState.errorTipoEvento,
                supportingText = {
                    if (uiState.errorTipoEvento) {
                        Text("El tipo de evento es obligatorio")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Fecha del evento con validación cronológica
            OutlinedTextField(
                value = uiState.fechaEvento,
                onValueChange = onFechaEventoChange,
                label = { Text("Fecha del evento (AAAA-MM-DD)") },
                isError = uiState.errorFechaEvento,
                supportingText = {
                    if (uiState.errorFechaEvento) {
                        Text("Ingresa una fecha válida y que no sea anterior a hoy")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Ubicación física del evento
            OutlinedTextField(
                value = uiState.ubicacion,
                onValueChange = onUbicacionChange,
                label = { Text("Ubicación") },
                isError = uiState.errorUbicacion,
                supportingText = {
                    if (uiState.errorUbicacion) {
                        Text("La ubicación es obligatoria")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Comuna
            OutlinedTextField(
                value = uiState.comuna,
                onValueChange = onComunaChange,
                label = { Text("Comuna") },
                isError = uiState.errorComuna,
                supportingText = {
                    if (uiState.errorComuna) {
                        Text("La comuna es obligatoria")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo: Estimación de público asistente
            OutlinedTextField(
                value = uiState.publicoEsperado,
                onValueChange = onPublicoEsperadoChange,
                label = { Text("Público esperado") },
                isError = uiState.errorPublicoEsperado,
                supportingText = {
                    if (uiState.errorPublicoEsperado) {
                        Text("Ingresa un número mayor que cero")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Campo opcional: Notas u observaciones adicionales
            OutlinedTextField(
                value = uiState.observaciones,
                onValueChange = onObservacionesChange,
                label = { Text("Observaciones (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )

            // SECCIÓN: Adjuntar Foto del Evento
            Text(
                text = "Foto del evento (opcional)",
                style = MaterialTheme.typography.titleMedium
            )

            // Renderizado de la vista previa de la imagen seleccionada
            if (imagenPrevia != null) {
                Image(
                    bitmap = imagenPrevia,
                    contentDescription = "Vista previa de la foto del evento",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            // Botón para invocar la Galería nativa de Android
            OutlinedButton(
                onClick = {
                    lanzadorGaleria.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (uiState.fotoUri == null) "Elegir de la galería" else "Cambiar foto"
                )
            }

            // Botón para remover la imagen cargada
            if (uiState.fotoUri != null) {
                OutlinedButton(
                    onClick = onImagenQuitar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Quitar foto")
                }
            }

            if (uiState.errorImagen) {
                Text(
                    text = "No se pudo cargar la imagen. Intenta con otra.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // SECCIÓN: Selector de Prioridades (Baja, Media, Alta)
            Text(
                text = "Prioridad",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Prioridad.entries.forEach { prioridad ->
                    if (uiState.prioridad == prioridad) {
                        Button(
                            onClick = { onPrioridadChange(prioridad) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(prioridad.etiqueta)
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onPrioridadChange(prioridad) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(prioridad.etiqueta)
                        }
                    }
                }
            }

            // SECCIÓN: Selección de la Organización Asociada
            Text(
                text = "Organización",
                style = MaterialTheme.typography.titleMedium
            )
            organizaciones.forEach { organizacion ->
                if (uiState.organizacionId == organizacion.id) {
                    Button(
                        onClick = { onOrganizacionSelect(organizacion.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("✓ ${organizacion.nombre}")
                    }
                } else {
                    OutlinedButton(
                        onClick = { onOrganizacionSelect(organizacion.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(organizacion.nombre)
                    }
                }
            }
            if (uiState.errorOrganizacion) {
                Text(
                    text = "Selecciona una organización",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Muestra de errores generales de procesamiento
            if (uiState.mensajeError != null) {
                Text(
                    text = uiState.mensajeError ?: "",
                    color = MaterialTheme.colorScheme.error
                )
            }

            // Botón Principal: Confirmar y Guardar
            Button(
                onClick = onGuardar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }

            // Botón Secundario: Volver
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
 * PREVIEW: Vista previa interactiva de la pantalla de creación de oportunidad en Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun CrearOportunidadScreenPreview() {
    CrearOportunidadContent(
        uiState = CrearOportunidadUiState(),
        organizaciones = listOf(
            com.example.activacorp.model.Organizacion(
                id = 1,
                nombre = "Empresa Ejemplo",
                tipo = "Empresa Privada",
                comuna = "Santiago"
            )
        )
    )
}