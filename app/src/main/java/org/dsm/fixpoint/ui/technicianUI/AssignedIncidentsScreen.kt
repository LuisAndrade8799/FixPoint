package org.dsm.fixpoint.ui.technicianUI

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
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.database.entities.Incidente // Import the Incidente entity
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.technicianVM.AssignedIncidentsViewModel
import android.app.Application // Import Application

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignedIncidentsScreen(
    userId: String, // <-- Nuevo parámetro para el ID del técnico
    // Remove direct ViewModel instantiation here, will be provided by ViewModelProvider
    onBackClick: () -> Unit = {}, // Lambda for back button navigation
    onAttendClick: (String) -> Unit = {} // Cambiado a Int para pasar el código del incidente
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val technicianId = userId // Convert userId to Int, default to 0 if null or invalid

    // Use ViewModelProvider.Factory to pass the technicianId to the ViewModel
    val assignedIncidentsViewModel: AssignedIncidentsViewModel = viewModel(
        factory = AssignedIncidentsViewModel.Factory(application, technicianId)
    )

    val incidents by assignedIncidentsViewModel.assignedIncidents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidencias Asignadas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
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
                        IncidentAssignedCard(incident = incident, onAttendClick = onAttendClick)
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentAssignedCard(
    incident: Incidente, // Cambiado de Incident a Incidente
    onAttendClick: (String) -> Unit // Cambiado a Int para pasar el código del incidente
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(BorderStroke(1.dp, Color.LightGray), MaterialTheme.shapes.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Código de Incidencia: ${incident.codigo}", // Acceder a 'codigo'
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Nombre de usuario: ${incident.nombreUsuario}", // Acceder a 'nombreUsuario'
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Area del usuario: ${incident.areaUsuario}", // Acceder a 'areaDeUsuario'
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Descripción de la incidencia: ${incident.descripcion}", // Acceder a 'descripcion'
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Nombre de Equipo: ${incident.nombreEquipo}", // Acceder a 'codigoEquipo'
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
                onClick = { onAttendClick(incident.codigo) }, // Pasar incident.codigo
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                )
            ) {
                Text("Atender", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAssignedIncidentsScreen() {
    FixPointTheme {
        // En el Preview, se necesita pasar un valor para loggedInTechnicianId
        AssignedIncidentsScreen(userId = "")
    }
}



