package org.dsm.fixpoint.ui.viewmodel.technicianVM

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.dsm.fixpoint.database.AppDatabase
import org.dsm.fixpoint.database.dao.IncidenteDao
import org.dsm.fixpoint.database.entities.Incidente
import org.dsm.fixpoint.model.Incident

class AttendPendingIncidentViewModel(application: Application
) : AndroidViewModel(application){

    private val incidenteDao: IncidenteDao = AppDatabase.getDatabase(application).incidenteDao()

    // State for UI fields
    private val _incidentCode = MutableStateFlow("")
    val incidentCode: StateFlow<String> = _incidentCode.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _userArea = MutableStateFlow("")
    val userArea: StateFlow<String> = _userArea.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    // Status message for UI feedback (success/error)
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // The ID of the incident being attended, will be set via setIncidentId
    private var currentIncidentId: Int? = null

    // Hold the fetched incident to easily update its status and description
    private var incident: Incidente? = null

    // Method to set incident ID and load details, similar to setIncidentCode in Assign
    fun setIncidentId(id: Int?) {
        if (currentIncidentId != id) { // Only load if ID changes
            currentIncidentId = id
            id?.let {
                loadIncidentDetails(it)
            } ?: run {
                clearIncidentDetails() // Clear if ID is null
            }
        }
    }

    private fun loadIncidentDetails(id: Int) {
        viewModelScope.launch {
            try {
                incident = incidenteDao.getIncidentById(id)
                if (incident != null) {
                    _incidentCode.value = incident!!.codigo.toString()
                    _username.value = incident!!.nombreUsuario
                    _userArea.value = incident!!.areaDeUsuario
                    _description.value = incident!!.descripcion
                } else {
                    _statusMessage.value = "Incidente con código $id no encontrado."
                    clearIncidentDetails()
                }
            } catch (e: Exception) {
                _statusMessage.value = "Error al cargar incidente: ${e.localizedMessage}"
                println("Error loading incident: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
        // Update the in-memory incident object as well for immediate reflection
        incident = incident?.copy(descripcion = newDescription)
    }

    fun onSolvedClick() {
        viewModelScope.launch {
            incident?.let { currentIncident ->
                try {
                    val updatedIncident = currentIncident.copy(
                        estado = "Solucionado",
                        descripcion = _description.value // Use the current (potentially edited) description
                    )
                    incidenteDao.updateIncidente(updatedIncident)
                    _statusMessage.value = "Incidente ${updatedIncident.codigo} marcado como SOLUCIONADO."
                    println("Incident ${updatedIncident.codigo} marked as SOLVED! Final Description: ${updatedIncident.descripcion}")
                    // Optionally, clear fields or navigate back here
                    // clearIncidentDetails() // Uncomment if you want to clear after action
                } catch (e: Exception) {
                    _statusMessage.value = "Error al marcar como solucionado: ${e.localizedMessage}"
                    println("Error marking as solved: ${e.localizedMessage}")
                    e.printStackTrace()
                }
            } ?: run {
                _statusMessage.value = "Error: No hay incidente cargado para marcar como solucionado."
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    private fun clearIncidentDetails() {
        _incidentCode.value = ""
        _username.value = ""
        _userArea.value = ""
        _description.value = ""
        incident = null
    }

    // No onPendingClick method as per image_1ac341.png
}



