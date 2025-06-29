package org.dsm.fixpoint.ui.viewmodel.technicianVM

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.dsm.fixpoint.database.AppDatabase
import org.dsm.fixpoint.database.entities.Incidente

class PendingIncidentsViewModel(
    application: Application, // Necesita el contexto de la aplicación para la base de datos
    private val loggedInTechnicianId: Int // Recibe el ID del técnico logueado
) : AndroidViewModel(application) { // Cambiado a AndroidViewModel

    private val _assignedIncidents = MutableStateFlow<List<Incidente>>(emptyList()) // Cambiado a List<Incidente>
    val assignedIncidents: StateFlow<List<Incidente>> = _assignedIncidents.asStateFlow()

    // Database DAO
    private val incidenteDao = AppDatabase.getDatabase(application).incidenteDao()

    init {
        loadAssignedIncidents()
    }

    // Function to load incidents assigned to the specific technician from the database
    fun loadAssignedIncidents() {
        viewModelScope.launch {
            try {
                // Fetch incidents where codigoTecnico matches the loggedInTechnicianId
                incidenteDao.getIncidentsByTechnician(loggedInTechnicianId).collect { incidents ->
                    val filtrado = incidents.filter{it.estado == "Pendiente"}
                    _assignedIncidents.value = filtrado
                }
            } catch (e: Exception) {
                // Handle potential database errors (e.g., log, show error message)
                println("Error loading assigned incidents: ${e.localizedMessage}")
                // Optionally, clear the list or show an error state
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
    class Factory(private val application: Application, private val technicianId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PendingIncidentsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PendingIncidentsViewModel(application, technicianId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}










