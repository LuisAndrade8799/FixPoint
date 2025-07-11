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
import com.github.tehras.charts.bar.BarChart
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.bar.renderer.bar.SimpleBarDrawer
import com.github.tehras.charts.bar.renderer.label.SimpleValueDrawer
import com.github.tehras.charts.bar.renderer.xaxis.SimpleXAxisDrawer
import com.github.tehras.charts.bar.renderer.yaxis.SimpleYAxisDrawer
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.dashboards.charScreenViewModel

// NUEVAS IMPORTACIONES PARA TOPAPPBAR
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // Importar el icono de flecha atrás
import androidx.compose.material3.TopAppBarDefaults


@OptIn(ExperimentalMaterial3Api::class) // Necesario para TopAppBar
@Composable
fun IncidentesChartScreen(
    onBackClick: () -> Unit = {}, // NUEVO: Lambda para manejar el regreso
    viewModel: charScreenViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gráficos de Incidentes", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        // Icono de flecha hacia atrás
                        androidx.compose.material.icons.Icons.Filled.ArrowBack
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
                text = "Incidentes por Área de Usuario",
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
                uiState.incidentesPorArea.isEmpty() -> {
                    Text("No hay datos de incidentes para mostrar en el gráfico.")
                }
                else -> {
                    val barChartData = BarChartData(
                        bars = uiState.incidentesPorArea.map { (area, count) ->
                            BarChartData.Bar(
                                label = area,
                                value = count.toFloat(),
                                color = Color((0xFF000000 + (Math.random() * 0xFFFFFF).toInt()) or 0xFF000000)
                            )
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        BarChart(
                            barChartData = barChartData,
                            barDrawer = SimpleBarDrawer(),
                            xAxisDrawer = SimpleXAxisDrawer(),
                            yAxisDrawer = SimpleYAxisDrawer(),
                            labelDrawer = SimpleValueDrawer(
                                drawLocation = SimpleValueDrawer.DrawLocation.XAxis
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text("Detalle de Incidentes:")
                        uiState.incidentesPorArea.forEach { (area, count) ->
                            Text("- $area: $count incidentes")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIncidentesChartScreen() {
    FixPointTheme {
        IncidentesChartScreen()
    }
}