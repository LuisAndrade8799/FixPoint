package org.dsm.fixpoint.ui.viewmodel.technicianVM

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

class PendingIncidentsViewModel(
    application: Application, // Necesita el contexto de la aplicación para la base de datos
    private val loggedInTechnicianId: String // Recibe el ID del técnico logueado
) : AndroidViewModel(application) { // Cambiado a AndroidViewModel

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
                    .whereEqualTo("codigoTecnico", loggedInTechnicianId)
                    .whereEqualTo("estado","Pendiente")
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
                        Log.e("IncidenteViewModel", "Error al mapear documento a Incidente: ${document.id}", e)
                        null
                    }
                }
                _assignedIncidents.value = incidentesList
                Log.d("IncidenteViewModel", "Incidentes asignados al técnico $loggedInTechnicianId: ${incidentesList.size}")

            } catch (e: Exception) {
                val message = "Error al obtener incidentes asignados: ${e.localizedMessage ?: "Error desconocido"}"
                Log.e("IncidenteViewModel", message, e)
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
    class Factory(private val application: Application, private val technicianId: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PendingIncidentsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PendingIncidentsViewModel(application, technicianId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}










