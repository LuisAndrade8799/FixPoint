package org.dsm.fixpoint.ui.viewmodel.userVM

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dsm.fixpoint.database.entities.Incidente
import org.dsm.fixpoint.ui.userUI.IncidentStatusScreen
import org.dsm.fixpoint.ui.viewmodel.technicianVM.PendingIncidentsViewModel

class IncidentStatusViewModel(
    application: Application, // Necesita el contexto de la aplicación para la base de datos
    private val name: String // Recibe el ID del técnico logueado
) : AndroidViewModel(application) {

    private val _assignedIncidents = MutableStateFlow<List<Incidente>>(emptyList()) // Cambiado a List<Incidente>
    val assignedIncidents: StateFlow<List<Incidente>> = _assignedIncidents.asStateFlow()

    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()
    init {
        loadAssignedIncidents()
    }

    // Function to load incidents assigned to the specific technician from the database
    fun loadAssignedIncidents() {
        viewModelScope.launch {
            try {
                val result = firestore.collection("incidente")
                    .whereEqualTo("nombreUsuario", name) // Filtra por el campo nombreUsuario
                    .get()
                    .await()

                val incidentesList = result.documents.mapNotNull { document ->
                    try {
                        Incidente(
                            areaUsuario = document.getString("areaUsuario") ?: "",
                            codigo = document.getString("codigo") ?: "",
                            nombreEquipo = document.getString("nombreEquipo") ?: "",
                            codigoTecnico = document.getString("codigoTecnico"),
                            descripcion = document.getString("descripcion") ?: "",
                            estado = document.getString("estado") ?: "",
                            nombreUsuario = document.getString("nombreUsuario") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("IncidentesUsuarioVM", "Error al mapear documento a Incidente: ${document.id}", e)
                        null
                    }
                }
                _assignedIncidents.value = incidentesList
                Log.d("IncidentesUsuarioVM", "Incidentes para '$name' encontrados: ${incidentesList.size}")

            } catch (e: Exception) {
                val message = "Error al obtener incidentes por usuario: ${e.localizedMessage ?: "Error desconocido"}"
                Log.e("IncidentesUsuarioVM", message, e)
                _assignedIncidents.value = emptyList()
            }
        }
    }

    fun onAttendClick(incidentCode: Int) {
        // This function will be called when an "Atender" button is clicked for a specific incident.
        // It should only pass the incident's code (ID) for further processing (e.g., navigating to a detail screen).
        println("Atender button clicked for Incident Code: $incidentCode")
        // No modification to the list here, as attending will happen on another screen
        // and then loadAssignedIncidents() will refresh the list (if status changes).
    }

    // Factory for creating AssignedIncidentsViewModel with a custom constructor
    class Factory(private val application: Application, private val name: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(IncidentStatusViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return IncidentStatusViewModel(application, name) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    // This screen is for displaying status, so typically no click actions on individual cards
    // If you wanted to view incident details on click, you'd add a function here and pass it down.
}









