package org.dsm.fixpoint

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.dsm.fixpoint.ui.*
import org.dsm.fixpoint.ui.chiefUI.AreaChiefMenuScreen
import org.dsm.fixpoint.ui.chiefUI.AssignIncidentDetailScreen
import org.dsm.fixpoint.ui.chiefUI.AssignIncidentsScreen
import org.dsm.fixpoint.ui.technicianUI.AssignedIncidentsScreen
import org.dsm.fixpoint.ui.technicianUI.AttendIncidentScreen
import org.dsm.fixpoint.ui.technicianUI.AttendPendingIncidentScreen
import org.dsm.fixpoint.ui.technicianUI.PendingIncidentsScreen
import org.dsm.fixpoint.ui.technicianUI.TechnicianMenuScreen
import org.dsm.fixpoint.ui.theme.FixPointTheme
import org.dsm.fixpoint.ui.userUI.IncidentStatusScreen
import org.dsm.fixpoint.ui.userUI.UserMenuScreen
import org.dsm.fixpoint.ui.viewmodel.*

//Main

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FixPointTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            // Provide application context to AndroidViewModel factory
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                            )
                            LoginScreen(
                                loginViewModel = loginViewModel,
                                onLoginSuccess = { userRole ->
                                    when (userRole) {
                                        "jefe" -> navController.navigate("areaChiefMenu") { popUpTo("login") { inclusive = true } }
                                        "tecnico" -> {
                                            val userId = loginViewModel.loggedInUserId.value// Get the logged-in username as userId
                                            navController.navigate("technicianMenu/$userId")
                                        }
                                        "comun" -> navController.navigate("userMenu") { popUpTo("login") { inclusive = true } }
                                    }
                                }
                            )
                        }
                        composable("areaChiefMenu") {
                            AreaChiefMenuScreen(
                                onRegisterIncidentsClick = { navController.navigate("registerIncidents") },
                                onAssignIncidentsClick = { navController.navigate("assignIncidentsList") },
                                onLogoutClick = {
                                    // Implement logout logic here:
                                    // 1. Clear any stored user session data (e.g., in a ViewModel or SharedPreferences)
                                    // 2. Navigate back to the login screen
                                    navController.navigate("login") {
                                        popUpTo("login") {
                                            inclusive = true // Clears the back stack up to the login screen
                                        }
                                    }
                                }
                            )
                        }
                        composable(
                            "technicianMenu/{userId}", // Define userId as a NavArgument
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            TechnicianMenuScreen(
                                onRegisterIncidentsClick = { navController.navigate("registerIncident") },
                                onAssignedIncidentsClick = { id -> navController.navigate("assignedIncidents/$id") }, // Pass userId
                                onPendingIncidentsClick = { id -> navController.navigate("pendingIncidents/$id") }, // Pass userId
                                onLogoutClick = { navController.navigate("login") }, // Navigate to login on logout
                                userId = userId // Pass userId to the Composable
                            )
                        }
                        composable("userMenu") {
                            UserMenuScreen(
                                onRegisterIncidentsClick = { navController.navigate("registerIncidents") },
                                onIncidentStatusClick = { navController.navigate("incidentStatus") },
                                onLogoutClick = {
                                    // Implement logout logic here:
                                    // 1. Clear any stored user session data (e.g., in a ViewModel or SharedPreferences)
                                    // 2. Navigate back to the login screen
                                    navController.navigate("login") {
                                        popUpTo("login") {
                                            inclusive = true // Clears the back stack up to the login screen
                                        }
                                    }
                                }
                            )
                        }
                        composable("registerIncidents") {
                            RegisterIncidentScreen(onBackClick = { navController.popBackStack() })
                        }
                        composable("assignIncidentsList") {
                            AssignIncidentsScreen(
                                onBackClick = { navController.popBackStack() },
                                onAssignClick = { incidentCode ->
                                    navController.navigate("assignIncidentDetail/$incidentCode")
                                }
                            )
                        }
                        composable(
                            "assignIncidentDetail/{incidentId}",
                            arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val incidentId = backStackEntry.arguments?.getString("incidentId")
                            AssignIncidentDetailScreen(
                                incidentId = incidentId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "assignedIncidents/{userId}", // Define userId as a NavArgument
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            AssignedIncidentsScreen(
                                onBackClick = { navController.popBackStack() },
                                onAttendClick = { incident ->
                                    navController.navigate("attendIncident/${incident}")
                                },
                                userId = userId // Pass userId to AssignedIncidentsScreen
                            )
                        }
                        composable(
                            "pendingIncidents/{userId}", // Define userId as a NavArgument
                            arguments = listOf(navArgument("userId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            PendingIncidentsScreen(
                                onBackClick = { navController.popBackStack() },
                                onAttendClick = { incident ->
                                    navController.navigate("attendPendingIncident/${incident}")
                                },
                                userId = userId // Pass userId to PendingIncidentsScreen
                            )
                        }
                        composable(
                            "attendIncident/{incidentId}",
                            arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val incidentId = backStackEntry.arguments?.getString("incidentId")
                            AttendIncidentScreen(
                                incidentId = incidentId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "attendPendingIncident/{incidentId}",
                            arguments = listOf(navArgument("incidentId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val incidentId = backStackEntry.arguments?.getString("incidentId")
                            AttendPendingIncidentScreen(
                                incidentId = incidentId,
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        composable("incidentStatus") {
                            IncidentStatusScreen(onBackClick = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
//FinActivity





