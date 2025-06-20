package org.dsm.fixpoint.ui.userUI

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
import org.dsm.fixpoint.model.Incident
import org.dsm.fixpoint.ui.technicianUI.IncidentAssignedCard
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.technicianVM.PendingIncidentsViewModel
import org.dsm.fixpoint.ui.viewmodel.userVM.IncidentStatusViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentStatusScreen(
    onBackClick: () -> Unit = {}, // Lambda for back button navigation
    name: String? = null
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val name = name?.toString() // Convert userId to Int, default to 0 if null or invalid
    val incidentStatusViewModel: IncidentStatusViewModel = viewModel(
        factory = IncidentStatusViewModel.Factory(application, name.toString())
    )
    val incidents by incidentStatusViewModel.assignedIncidents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estado de Incidencias", color = Color.White) }, // Screen title
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
                        IncidentStatusCard(incident = incident)
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentStatusCard(
    incident: Incidente,
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
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Código de Equipo: ${incident.codigoEquipo}", // Acceder a 'codigoEquipo'
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Estado: ${incident.estado}", // Displaying the status
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIncidentStatusScreen() {
    FixPointTheme {
        IncidentStatusScreen()
    }
}












