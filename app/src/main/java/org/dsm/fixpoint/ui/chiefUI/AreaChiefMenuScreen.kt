package org.dsm.fixpoint.ui.chiefUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.R

@Composable
fun AreaChiefMenuScreen(
    onRegisterIncidentsClick: () -> Unit = {},
    onAssignIncidentsClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}, // NEW: Logout click lambda
    onViewChartsClick: () -> Unit = {},
    onViewStatusChartsClick: () -> Unit = {} // NUEVO: Para el gráfico de incidentes por estado
) {
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Menú del Jefe de Área",
                color = Color.White,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Button for Registrar Incidencia
            Button(
                onClick = onRegisterIncidentsClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Adjust width as needed
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Transparent background for gradient
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp), // Remove default padding for custom background
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2))                            ),
                            shape = MaterialTheme.shapes.medium // Rounded corners
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Registrar Incidencia",
                        fontSize = 20.sp // Larger font size for buttons
                    )
                }
            }

            // Spacer between buttons
            Spacer(modifier = Modifier.height(16.dp))

            // Button for Asignar Incidencias
            Button(
                onClick = onAssignIncidentsClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Adjust width as needed
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Transparent background for gradient
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp), // Remove default padding for custom background
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF42A5F5), Color(0xFF1976D2)) // Blue gradient
                            ),
                            shape = MaterialTheme.shapes.medium // Rounded corners
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Asignar Incidencias",
                        fontSize = 20.sp // Larger font size for buttons
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Increased space before logout button

            Button(
                onClick = onViewChartsClick,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF4CAF50), Color(0xFF388E3C)) // Un gradiente verde
                            ),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ver Gráficos por areas",
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NUEVO: Botón para "Gráfico por Estado"
            Button(
                onClick = onViewStatusChartsClick, // Usa la nueva lambda
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(60.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFC107), Color(0xFFFF8F00)) // Un gradiente naranja
                            ),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Gráfico por Estado",
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp)) // Increased space before logout button

            // NEW: Logout Button
            Button(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp)
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F), // Red for logout
                    contentColor = Color.White
                )
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAreaChiefMenuScreen() {
    FixPointTheme {
        AreaChiefMenuScreen(
            onRegisterIncidentsClick = {},
            onAssignIncidentsClick = {},
            onLogoutClick = {} // Provide an empty lambda for preview
        )
    }
}