package org.dsm.fixpoint.ui.chiefUI


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.chiefVM.AssignIncidentDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignIncidentDetailScreen(
    incidentId: String? = "0", // Default to "0" or some indicator for no incident selected
    assignIncidentDetailViewModel: AssignIncidentDetailViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    // Set the incident code from navigation arguments once
    LaunchedEffect(incidentId) {
        assignIncidentDetailViewModel.setIncidentCode(incidentId)
    }

    val technicianCode by assignIncidentDetailViewModel.technicianCode.collectAsState()
    val technicianName by assignIncidentDetailViewModel.technicianName.collectAsState()
    val currentIncidentCode by assignIncidentDetailViewModel.incidentCode.collectAsState()
    val assignButtonEnabled by assignIncidentDetailViewModel.assignButtonEnabled.collectAsState()
    val technicians by assignIncidentDetailViewModel.technicians.collectAsState() // Observe technicians list

    // State for the dropdown menu
    var expanded by remember { mutableStateOf(false) }

    // Observe assignment success to navigate back
    val assignmentSuccess by assignIncidentDetailViewModel.assignmentSuccess.collectAsState()

    LaunchedEffect(assignmentSuccess) {
        if (assignmentSuccess) {
            onBackClick() // Navigate back on success
            assignIncidentDetailViewModel.resetAssignmentSuccess() // Reset the flag
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Incidencia", color = Color.White) },
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Asignación de Incidencia",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp),
                color = Color.Black
            )

            // "Código de incidencia" Field (Read-only, populated from navigation args)
            OutlinedTextField(
                value = currentIncidentCode,
                onValueChange = { /* Read-only */ },
                label = { Text("Código de incidencia:") },
                singleLine = true,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // "Código de técnico" Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = technicianCode,
                    onValueChange = { /* Read-only, selected via dropdown */ },
                    readOnly = true,
                    label = { Text("Código de técnico:") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (technicians.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No hay técnicos disponibles") },
                            onClick = { expanded = false },
                            enabled = false
                        )
                    } else {
                        technicians.forEach { user ->
                            DropdownMenuItem(
                                text = { Text("${user.idUsuario} - ${user.nombre}") },
                                onClick = {
                                    assignIncidentDetailViewModel.onTechnicianCodeChange(user.idUsuario)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // "Nombre del técnico" Field (Auto-completed)
            OutlinedTextField(
                value = technicianName,
                onValueChange = { /* Read-only, auto-completed */ },
                label = { Text("Nombre del técnico:") },
                singleLine = true,
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // "Asignar" Button
            Button(
                onClick = { assignIncidentDetailViewModel.onAssignClick() },
                enabled = assignButtonEnabled,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE),
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text("Asignar", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAssignIncidentDetailScreen() {
    FixPointTheme {
        AssignIncidentDetailScreen()
    }
}