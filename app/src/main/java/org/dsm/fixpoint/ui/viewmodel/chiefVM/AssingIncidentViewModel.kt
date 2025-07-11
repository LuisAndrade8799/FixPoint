package org.dsm.fixpoint.ui.viewmodel.chiefVM

import android.app.Application // Import Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel // Change ViewModel to AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dsm.fixpoint.database.entities.Incidente // Import the Incidente entity

// Change ViewModel() to AndroidViewModel(application: Application)
class AssignIncidentsViewModel(application: Application) : AndroidViewModel(application) {

    // Incidents to be displayed (filtered by codigoTecnico = null)
    private val _incidents = MutableStateFlow<List<Incidente>>(emptyList())
    val incidents: StateFlow<List<Incidente>> = _incidents.asStateFlow()

    // Get an instance of your AppDatabase and then the IncidenteDao

    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        loadUnassignedIncidents()
    }

    // Function to load incidents from the database
    fun loadUnassignedIncidents() {
        viewModelScope.launch {
            try {
                val result = firestore.collection("incidente")
                    .whereEqualTo("codigoTecnico", "") // Busca documentos donde codigoTecnico es una cadena vacía
                    .get()
                    .await()

                val incidentesList = result.documents.mapNotNull { document ->
                    try {
                        Incidente(
                            areaUsuario = document.getString("areaUsuario") ?: "",
                            codigo = document.getString("codigo") ?: "",
                            nombreEquipo = document.getString("nombreEquipo") ?: "",
                            codigoTecnico = document.getString("codigoTecnico"), // Obtiene el valor, puede ser null
                            descripcion = document.getString("descripcion") ?: "",
                            estado = document.getString("estado") ?: "",
                            nombreUsuario = document.getString("nombreUsuario") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("IncidenteViewModel", "Error al mapear documento a Incidente: ${document.id}", e)
                        null
                    }
                }
                _incidents.value = incidentesList
                Log.d("IncidenteViewModel", "Incidentes sin técnico encontrados: ${incidentesList.size}")

            } catch (e: Exception) {
                Log.e("IncidenteViewModel", "Error al obtener incidentes sin técnico: ${e.message}", e)
                _incidents.value = emptyList() // En caso de error, limpiar la lista
            }
        }
    }

    // This function will be called when an "Asignar" button is clicked for a specific incident.
    // It should only pass the incident's code (ID) for further processing.
    fun onAssignClick(incidentCode: Int) {
        // Here, you would typically navigate to another screen where the chief
        // can assign a technician to this specific incident.
        // This function just "sends" the incident's code.
        println("Asignar button clicked for Incident Code: $incidentCode")
        // No modification to the list here, as the assignment will happen on another screen
        // and then loadUnassignedIncidents() will refresh the list.
    }
}