package org.dsm.fixpoint.ui.dashboards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.dashboards.IncidentesPorEstadoViewModel

// NUEVAS IMPORTACIONES PARA TOPAPPBAR
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.TopAppBarDefaults


@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun IncidetesEstadoPieCharScreen(
    onBackClick: () -> Unit = {}, // NUEVO: Lambda para manejar el regreso
    viewModel: IncidentesPorEstadoViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gráfico de Incidentes por Estado", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        }
    ) { paddingValues -> // Usar paddingValues para aplicar el padding del Scaffold
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Aplicar el padding del Scaffold
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Incidentes por Estado",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.errorMessage != null -> {
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                }
                uiState.incidentesPorEstado.isEmpty() -> {
                    Text("No hay datos de incidentes para mostrar en el gráfico.")
                }
                else -> {
                    val pieChartData = PieChartData(
                        slices = uiState.incidentesPorEstado.map { (estado, count) ->
                            PieChartData.Slice(
                                value = count.toFloat(),
                                color = Color((0xFF000000 + (Math.random() * 0xFFFFFF).toInt()) or 0xFF000000)
                            )
                        }
                    )
                    Box(
                        modifier = Modifier
                            .size(300.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        PieChart(
                            pieChartData = pieChartData,
                            sliceDrawer = SimpleSliceDrawer(
                                sliceThickness = 100f
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Detalle de Estados:")
                        uiState.incidentesPorEstado.forEach { (estado, count) ->
                            Text("- $estado: $count incidentes")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIncidentesEstadoPieChartScreen() {
    FixPointTheme {
        IncidetesEstadoPieCharScreen()
    }
}