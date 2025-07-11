package org.dsm.fixpoint.ui.technicianUI

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.technicianVM.AttendIncidentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendIncidentScreen(
    incidentId: String, // Changed to Int?
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    // AndroidViewModel automatically gets the Application context
    val attendIncidentViewModel: AttendIncidentViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AttendIncidentViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AttendIncidentViewModel(context.applicationContext as Application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    // Set the incident ID from navigation arguments once when the screen starts
    LaunchedEffect(incidentId) {
        attendIncidentViewModel.setIncidentId(incidentId)
    }

    // Collect UI state from ViewModel
    val incidente by attendIncidentViewModel.incident.collectAsState()
    val statusMessage by attendIncidentViewModel.statusMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            attendIncidentViewModel.clearStatusMessage() // Clear message after showing
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atender Incidencias", color = Color.White) },
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
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost
        containerColor = Color(0xFFEEEEEE)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // "Código de incidencia" Field (Read-only)
            OutlinedTextField(
                value = incidente?.codigo ?: "", // Now comes from ViewModel
                onValueChange = { /* Read-only */ },
                label = { Text("Código de incidencia:") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // "Nombre de usuario" Field (Read-only)
            OutlinedTextField(
                value = incidente?.nombreUsuario ?: "",
                onValueChange = { /* Read-only */ },
                label = { Text("Nombre de usuario:") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // "Area del usuario" Field (Read-only)
            OutlinedTextField(
                value = incidente?.areaUsuario ?: "",
                onValueChange = { /* Read-only */ },
                label = { Text("Area del usuario:") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // "Descripción" Field (Potentially editable for technician notes/solution)
            OutlinedTextField(
                value = incidente?.descripcion ?: "",
                onValueChange = { attendIncidentViewModel.onDescriptionChange(it) },
                label = { Text("Descripción:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Solucionado" Button
                Button(
                    onClick = { attendIncidentViewModel.onSolvedClick() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Solucionado", color = Color.White)
                }

                // "Pendiente" Button
                Button(
                    onClick = { attendIncidentViewModel.onPendingClick() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107)
                    )
                ) {
                    Text("Pendiente", color = Color.Black)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAttendIncidentScreen() {
    FixPointTheme {
        AttendIncidentScreen("") // Pass a dummy Int ID for preview
    }
}