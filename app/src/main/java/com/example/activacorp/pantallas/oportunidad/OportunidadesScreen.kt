package com.example.activacorp.pantallas.oportunidad

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.activacorp.model.EstadoOportunidad
import com.example.activacorp.model.Prioridad

/**
 * PANTALLA: OportunidadesScreen (Separación de Oportunidades y Eventos)
 * Permite alternar mediante pestañas entre el catálogo comercial de Oportunidades y el listado de Eventos agendados.
 * Incluye barra de búsqueda por texto, filtros dinámicos por Estado y Prioridad, y lista optimizada con LazyColumn.
 */
@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OportunidadesScreen(
    onIrADetalle: (Int) -> Unit = {}, // Callback para navegar a la ficha detallada
    onIrACrear: () -> Unit = {},      // Callback para abrir el formulario de nueva oportunidad
    onVolver: () -> Unit = {},         // Callback para regresar al Dashboard
    viewModel: OportunidadesViewModel = viewModel() // Inyección predeterminada del ViewModel
) {
    // Sincronización del estado UI reactivo según el ciclo de vida
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado local para alternar entre las pestañas (0: Oportunidades, 1: Eventos)
    var pestanaSeleccionada by remember { mutableIntStateOf(0) }

    // Determina si hay algún filtro activo para mostrar el botón de limpieza
    val hayFiltros = uiState.busqueda.isNotBlank() ||
            uiState.filtroEstado != null ||
            uiState.filtroPrioridad != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.esSupervisor) "Gestión del Sistema" else "Mis Registros"
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
            // ----- PESTAÑAS DE SEPARACIÓN: OPORTUNIDADES VS EVENTOS -----
            TabRow(selectedTabIndex = pestanaSeleccionada) {
                Tab(
                    selected = pestanaSeleccionada == 0,
                    onClick = { pestanaSeleccionada = 0 },
                    text = { Text("💼 Oportunidades (${uiState.lista.size})") }
                )
                Tab(
                    selected = pestanaSeleccionada == 1,
                    onClick = { pestanaSeleccionada = 1 },
                    text = { Text("📅 Eventos (${uiState.lista.size})") }
                )
            }

            // Componente de lista perezosa (LazyColumn) para reciclaje de vistas eficiente
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ----- SECCIÓN DE BÚSQUEDA Y FILTROS -----
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Campo de texto para búsqueda en tiempo real
                        OutlinedTextField(
                            value = uiState.busqueda,
                            onValueChange = {
                                viewModel.cambiarBusqueda(it)
                            },
                            label = {
                                Text(
                                    if (pestanaSeleccionada == 0)
                                        "Buscar por nombre, organización o comuna"
                                    else
                                        "Buscar por tipo de evento, comuna o lugar"
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Filtros por Estado y Prioridad (visibles principalmente en vista Oportunidades)
                        if (pestanaSeleccionada == 0) {
                            Text(
                                text = "Estado",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BotonFiltro(
                                    texto = "Todos",
                                    seleccionado = uiState.filtroEstado == null,
                                    onClick = { viewModel.seleccionarEstado(null) }
                                )
                                EstadoOportunidad.entries.forEach { estado ->
                                    BotonFiltro(
                                        texto = estado.etiqueta,
                                        seleccionado = uiState.filtroEstado == estado,
                                        onClick = { viewModel.seleccionarEstado(estado) }
                                    )
                                }
                            }

                            Text(
                                text = "Prioridad",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                BotonFiltro(
                                    texto = "Todas",
                                    seleccionado = uiState.filtroPrioridad == null,
                                    onClick = { viewModel.seleccionarPrioridad(null) }
                                )
                                Prioridad.entries.forEach { prioridad ->
                                    BotonFiltro(
                                        texto = prioridad.etiqueta,
                                        seleccionado = uiState.filtroPrioridad == prioridad,
                                        onClick = { viewModel.seleccionarPrioridad(prioridad) }
                                    )
                                }
                            }
                        }

                        // Indicador de resultados filtrados vs total
                        Text("Mostrando ${uiState.lista.size} de ${uiState.totalSinFiltrar}")

                        // Botón opcional para restablecer todos los parámetros de filtrado
                        if (hayFiltros) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.limpiarFiltros()
                                }
                            ) {
                                Text("Limpiar filtros")
                            }
                        }
                    }
                }

                // ----- MANEJO DE ESTADO VACÍO -----
                if (uiState.lista.isEmpty()) {
                    item {
                        Text(
                            if (uiState.totalSinFiltrar == 0) {
                                if (pestanaSeleccionada == 0) "Aún no hay oportunidades registradas"
                                else "Aún no hay eventos registrados"
                            } else {
                                "No hay registros que coincidan con los filtros"
                            }
                        )
                    }
                }

                // ----- RENDERIZADO DIFERENCIADO SEGÚN PESTAÑA -----
                items(uiState.lista) { oportunidad ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIrADetalle(oportunidad.id) // Navega al detalle pasando el ID
                            }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (pestanaSeleccionada == 0) {
                                // VISTA PESTAÑA OPORTUNIDADES (Comercial / Negocio)
                                Text(
                                    text = oportunidad.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Organización: ${oportunidad.organizacionNombre}")
                                Text("Estado: ${EstadoOportunidad.valueOf(oportunidad.estado).etiqueta}")
                                Text("Prioridad: ${Prioridad.valueOf(oportunidad.prioridad).etiqueta}")
                                Text("Responsable: ${oportunidad.responsableNombre ?: "Sin asignar"}")
                            } else {
                                // VISTA PESTAÑA EVENTOS (Agendamiento / Logística)
                                Text(
                                    text = "📌 ${oportunidad.tipoEvento}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text("Oportunidad: ${oportunidad.nombre}")
                                Text("📅 Fecha del evento: ${oportunidad.fechaEvento}")
                                Text("📍 Ubicación: ${oportunidad.ubicacion}, ${oportunidad.comuna}")
                                Text("👥 Público estimado: ${oportunidad.publicoEsperado} personas")
                                Text("Organización: ${oportunidad.organizacionNombre}")
                            }
                        }
                    }
                }
            }

            // Control de Permisos en UI: Solo el Ejecutivo puede crear nuevas oportunidades
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

            // Botón para regresar al Dashboard
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

/**
 * COMPONENTE AUXILIAR: Botón con estilo dinámico (Relleno si está activo, Delimitado si no)
 */
@Composable
private fun BotonFiltro(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    if (seleccionado) {
        Button(onClick = onClick) {
            Text(texto)
        }
    } else {
        OutlinedButton(onClick = onClick) {
            Text(texto)
        }
    }
}
