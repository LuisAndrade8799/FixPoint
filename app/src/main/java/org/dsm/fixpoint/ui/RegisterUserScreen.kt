package org.dsm.fixpoint.ui

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.ui.viewmodel.RegisterUserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterUserScreen(
    viewModel: RegisterUserViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Observar los estados del ViewModel
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val name by viewModel.name.collectAsState()
    val selectedUserType by viewModel.selectedUserType.collectAsState()
    val areaUsuario by viewModel.areaUsuario.collectAsState()
    val isAreaUsuarioVisible by viewModel.isAreaUsuarioVisible.collectAsState()
    val registrationResult by viewModel.registrationResult.collectAsState()
    val isCorreo by viewModel.isCorreo.collectAsState()

    // Mostrar Toast con el resultado del registro
    LaunchedEffect(registrationResult) {
        registrationResult?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearRegistrationResult() // Limpiar el mensaje después de mostrarlo
            if (message.contains("exitosamente")) {
                onBackClick() // Navegar atrás si el registro fue exitoso
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Usuario") },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Go back",
                        modifier = Modifier.clickable { onBackClick() }
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onUsernameChange(it) },
                label = { Text(
                    text= "Correo",
                    color = if (!isCorreo) Color.Red else LocalContentColor.current
                ) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { viewModel.name.value = it },
                label = { Text("Nombre") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Spinner (DropdownMenu) para el tipo de usuario
            UserTypeDropdown(
                selectedUserType = selectedUserType,
                onUserTypeSelected = { viewModel.selectedUserType.value = it }
            )

            // Campo "Área de usuario" visible condicionalmente
            if (isAreaUsuarioVisible) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = areaUsuario,
                    onValueChange = { viewModel.areaUsuario.value = it },
                    label = { Text("Área de usuario") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.registerUser() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTypeDropdown(
    selectedUserType: String,
    onUserTypeSelected: (String) -> Unit
) {
    val userTypes = listOf("comun", "jefe", "tecnico")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedUserType,
            onValueChange = {}, // No permitir escribir directamente
            readOnly = true,
            label = { Text("Tipo") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            userTypes.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onUserTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterUserScreen() {
    RegisterUserScreen(onBackClick = {})
}