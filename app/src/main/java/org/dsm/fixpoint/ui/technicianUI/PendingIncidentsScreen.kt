package org.dsm.fixpoint.ui.technicianUI

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.database.entities.Incidente
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.technicianVM.AssignedIncidentsViewModel
import org.dsm.fixpoint.ui.viewmodel.technicianVM.PendingIncidentsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingIncidentsScreen(
    onBackClick: () -> Unit = {}, // Lambda for back button navigation
    onAttendClick: (Int) -> Unit = {},
    userId : String? = "0"
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val technicianId = userId?.toIntOrNull() ?: 0 // Convert userId to Int, default to 0 if null or invalid

    // Use ViewModelProvider.Factory to pass the technicianId to the ViewModel
    val pendingIncidentsViewModel: PendingIncidentsViewModel = viewModel(
        factory = PendingIncidentsViewModel.Factory(application, technicianId)
    )
    val incidents by pendingIncidentsViewModel.assignedIncidents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidencias Pendientes", color = Color.White) }, // Changed title
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5)) // Deep blue for AppBar
            )
        },
        containerColor = Color(0xFFEEEEEE) // Light gray background for the content area
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (incidents.isEmpty()) {
                Text("No hay incidencias asignadas.", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(incidents) { incident ->
                        PendingIncidentCard(incident = incident, onAttendClick = onAttendClick)
                    }
                }
            }
        }
    }
}




@Composable
fun PendingIncidentCard( // Renamed from AssignedIncidentCard
    incident: Incidente,
    onAttendClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(4.dp) // Smaller padding inside the row
            .border(
                BorderStroke(2.dp, Color.Gray), // Solid gray border to match visual
                MaterialTheme.shapes.small
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Código de incidencia: ${incident.codigo}",
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Nombre de usuario: ${incident.nombreUsuario}",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Area del usuario: ${incident.areaDeUsuario}",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Descripción de la incidencia: ${incident.descripcion}",
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Código de Equipo: ${incident.codigoEquipo}", // Acceder a 'codigoEquipo'
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Estado: ${incident.estado}", // Mostrar el estado
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            incident.codigoTecnico?.let { techCode ->
                Text(text = "Asignado a (Código): $techCode", fontSize = 12.sp, color = Color.Blue)
            }
            Button(
                onClick = { onAttendClick(incident.codigo) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE) // A shade of purple for the button
                )
            ) {
                Text("Atender", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPendingIncidentsScreen() {
    FixPointTheme {
        PendingIncidentsScreen()
    }
}




