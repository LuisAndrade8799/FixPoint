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
import org.dsm.fixpoint.ui.viewmodel.technicianVM.AttendPendingIncidentViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendPendingIncidentScreen(
    incidentId: String?, // Pass the incident ID to load details
    onBackClick: () -> Unit = {} // Lambda for back button navigation
) {
    val context = LocalContext.current
    // AndroidViewModel automatically gets the Application context
    val attendPendingIncidentViewModel: AttendPendingIncidentViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(AttendPendingIncidentViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return AttendPendingIncidentViewModel(context.applicationContext as Application) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )
    LaunchedEffect(incidentId) {
        attendPendingIncidentViewModel.setIncidentId(incidentId)
    }
    // Collect UI state from ViewModel
    val incidente by attendPendingIncidentViewModel.incident.collectAsState()
     // This might be editable
    val statusMessage by attendPendingIncidentViewModel.statusMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            attendPendingIncidentViewModel.clearStatusMessage() // Clear message after showing
        }
    }

    // The ViewModel's init block will handle loading details based on incidentId from SavedStateHandle.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atender Incidencias", color = Color.White) }, // Title from image
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
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Add SnackbarHost
        containerColor = Color(0xFFEEEEEE) // Light gray background for the content area
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
                value = incidente?.codigo ?:"",
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
                onValueChange = { attendPendingIncidentViewModel.onDescriptionChange(it) }, // Allow editing
                label = { Text("Descripción:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Multi-line
                    .padding(vertical = 8.dp),

            )

            Spacer(modifier = Modifier.height(32.dp))

            // "Solucionado" Button
            Button(
                onClick = { attendPendingIncidentViewModel.onSolvedClick() },
                modifier = Modifier
                    .wrapContentWidth() // Wrap content for width
                    .height(50.dp)
                    .padding(horizontal = 24.dp), // Add horizontal padding to the button itself
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50) // Green for solved
                )
            ) {
                Text("Solucionado", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAttendPendingIncidentScreen() {
    FixPointTheme {
        // Provide dummy ID for preview, though it won't load actual data
        AttendPendingIncidentScreen(incidentId = "INC021")
    }
}