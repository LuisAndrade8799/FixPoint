package org.dsm.fixpoint.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.viewmodel.RegisterIncidentViewModel

import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterIncidentScreen(
    registerIncidentViewModel: RegisterIncidentViewModel = viewModel(),
    onBackClick: () -> Unit = {} // Lambda for back button navigation
) {
    val context = LocalContext.current

    val equipmentCode by registerIncidentViewModel.equipmentCode.collectAsState()
    val username by registerIncidentViewModel.username.collectAsState()
    val userArea by registerIncidentViewModel.userArea.collectAsState()
    val description by registerIncidentViewModel.description.collectAsState()
    val sendButtonEnabled by registerIncidentViewModel.sendButtonEnabled.collectAsState()
    val technicians by registerIncidentViewModel.technicians.collectAsState() // Observe technicians list
    var expanded by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Incidencias", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF3F51B5)) // A deep blue for the AppBar
            )
        },
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
            // "Código de equipo" Field
            OutlinedTextField(
                value = equipmentCode,
                onValueChange = { registerIncidentViewModel.onEquipmentCodeChange(it) },
                label = { Text("Código de equipo:") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = { /* Read-only, selected via dropdown */ },
                    readOnly = true,
                    label = { Text("Nombre de usuario:") },
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
                                text = { Text("${user.codigo} - ${user.nombre}") },
                                onClick = {
                                    registerIncidentViewModel.onTechnicianCodeChange(user.nombre.toString())
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // "Area del usuario" Field
            OutlinedTextField(
                value = userArea,
                onValueChange = { registerIncidentViewModel.onUserAreaChange(it) },
                label = { Text("Area del usuario:") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),

            )

            // "Descripción" Field
            OutlinedTextField(
                value = description,
                onValueChange = { registerIncidentViewModel.onDescriptionChange(it) },
                label = { Text("Descripción:") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // Make it a multi-line text area
                    .padding(vertical = 8.dp),

            )

            Spacer(modifier = Modifier.height(24.dp))

            // "Enviar" Button
            Button(
                onClick = { registerIncidentViewModel.onSendClick() },
                enabled = sendButtonEnabled,
                modifier = Modifier
                    .wrapContentWidth() // Wrap content for width
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE), // A shade of purple for the button
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text("Enviar", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterIncidentScreen() {
    FixPointTheme {
        RegisterIncidentScreen()
    }
}









