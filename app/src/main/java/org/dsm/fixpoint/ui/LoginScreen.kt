package org.dsm.fixpoint.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.R
import org.dsm.fixpoint.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess : (userRole : String) -> Unit = {},
    loginViewModel: LoginViewModel = viewModel()
) {
    val userId by loginViewModel.loggedInUserId.collectAsState()
    val username by loginViewModel.username.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val loginEnabled by loginViewModel.loginEnabled.collectAsState()
    val userRole by loginViewModel.loggedInUserRole.collectAsState()
    val loginMessage by loginViewModel.loginMessage.collectAsState()
    val isCorreo by loginViewModel.isCorreo.collectAsState()// Observe login messages

    val snackbarHostState = remember { SnackbarHostState() } // For showing messages

    // Use a CoroutineScope to launch suspend functions in the UI
    val scope = rememberCoroutineScope()

    // Use LaunchedEffect to react to changes in userRole
    LaunchedEffect(userRole) {
        userRole?.let { role ->
            onLoginSuccess(role)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3366CC)) // A dark blue background, similar to the image
    ) {
        // Background Image (assuming you have a drawable for the tech background)
        Image(
            painter = painterResource(id = R.drawable.fondo), // Replace with your actual background image
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.logo), // Replace with your app logo image
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 32.dp)
            )

            // Username Field
            TextField(
                value = username,
                onValueChange = { loginViewModel.onUsernameChange(it) },
                label = { Text("Usuario") },
                singleLine = true,
                isError = isCorreo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            // Password Field
            TextField(
                value = password,
                onValueChange = { loginViewModel.onPasswordChange(it) },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Ingresar Button
            Button(
                onClick = {
                    loginViewModel.onLoginClick()
                },
                enabled = loginEnabled,
                modifier = Modifier
                    .fillMaxWidth(0.6f) // Adjust width as needed
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6200EE), // A shade of purple for the button
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text("Ingresar", color = Color.White)
            }
        }
    }
}

@Preview(showBackground = true,
    showSystemUi = true)
@Composable
fun PreviewLoginScreen() {
    FixPointTheme {
        LoginScreen()
    }
}