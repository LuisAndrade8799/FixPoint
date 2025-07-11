package org.dsm.fixpoint.ui.chiefUI

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.chiefVM.AssignIncidentsViewModel
import org.dsm.fixpoint.database.entities.Incidente // Import the Incidente entity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignIncidentsScreen(
    assignIncidentsViewModel: AssignIncidentsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    // Change parameter type to Incidente.codigo (Int)
    onAssignClick: (incidentCode: String) -> Unit = {}
) {
    val incidents by assignIncidentsViewModel.incidents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Incidencias", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5))
            )
        },
        containerColor = Color(0xFFEEEEEE)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            if (incidents.isEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No hay incidencias sin asignar.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(incidents) { incident ->
                        IncidentItem(incident = incident, onAssignClick = onAssignClick)
                    }
                }
            }
        }
    }
}

@Composable
fun IncidentItem(incident: Incidente, onAssignClick: (incidentCode: String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, Color.LightGray), MaterialTheme.shapes.medium),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Text(
                text = "Código de Incidencia: ${incident.codigo}", // Use incident.codigo
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Nombre de usuario: ${incident.nombreUsuario}", // Use incident.nombreUsuario
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Área del usuario: ${incident.areaUsuario}", // Use incident.areaDeUsuario
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Descripción de la incidencia: ${incident.descripcion}", // Use incident.descripcion
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Nombre de Equipo: ${incident.nombreEquipo}", // Display the equipment code
                fontSize = 12.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Button(
                onClick = { onAssignClick(incident.codigo) }, // Pass incident.codigo
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE)
                )
            ) {
                Text("Asignar", color = Color.White)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAssignIncidentsScreen() {
    FixPointTheme {
        AssignIncidentsScreen()
    }
}