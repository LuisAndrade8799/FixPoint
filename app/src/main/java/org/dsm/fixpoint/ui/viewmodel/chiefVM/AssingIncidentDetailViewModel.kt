package org.dsm.fixpoint.ui.viewmodel.chiefVM

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dsm.fixpoint.database.entities.Incidente
import org.dsm.fixpoint.database.entities.Usuario // Import Usuario entity

class AssignIncidentDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val _technicianCode = MutableStateFlow("")
    val technicianCode: StateFlow<String> = _technicianCode.asStateFlow()

    private val _technicianName = MutableStateFlow("")
    val technicianName: StateFlow<String> = _technicianName.asStateFlow()

    private val _incidentCode = MutableStateFlow("")
    val incidentCode: StateFlow<String> = _incidentCode.asStateFlow()

    private val _assignButtonEnabled = MutableStateFlow(false)
    val assignButtonEnabled: StateFlow<Boolean> = _assignButtonEnabled.asStateFlow()

    // New StateFlow for list of technicians
    private val _technicians = MutableStateFlow<List<Usuario>>(emptyList())
    val technicians: StateFlow<List<Usuario>> = _technicians.asStateFlow()

    // New StateFlow to indicate assignment success (for navigation)
    private val _assignmentSuccess = MutableStateFlow(false)
    val assignmentSuccess: StateFlow<Boolean> = _assignmentSuccess.asStateFlow()

    // Database DAOs
    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        // Observe changes in all fields to enable/disable the assign button
        viewModelScope.launch {
            combine(
                _technicianCode,
                _technicianName,
                _incidentCode
            ) { techCode, techName, incCode ->
                techCode.isNotBlank() && techName.isNotBlank() && incCode.isNotBlank() && incCode != "0" // Ensure incidentCode is not "0"
            }.collect { isEnabled ->
                _assignButtonEnabled.value = isEnabled
            }
        }
        loadTechnicians() // Load technicians when ViewModel is created
    }

    // Function to load only users of type "tecnico"
    private fun loadTechnicians() {
        viewModelScope.launch {
            try {
                val result = firestore.collection("usuario")
                    .whereEqualTo("tipo", "tecnico") // Busca documentos donde el campo 'tipo' es 'tecnico'
                    .get()
                    .await()

                val userList = result.documents.mapNotNull { document ->
                    // Mapea el documento a tu clase de datos Usuario
                    try {
                        Usuario(
                            idUsuario = document.getString("idUsuario") ?: "",
                            nombre = document.getString("nombre") ?: "",
                            tipo = document.getString("tipo") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("UserViewModel", "Error al mapear documento a Usuario: ${document.id}", e)
                        null
                    }
                }
                _technicians.value = userList
                Log.d("UserViewModel", "Técnicos encontrados: ${userList.size}")

            } catch (e: Exception) {
                Log.e("UserViewModel", "Error al obtener usuarios técnicos: ${e.message}", e)
                _technicians.value = emptyList() // En caso de error, limpiar la lista
            }
        }
    }

    fun onTechnicianCodeChange(newCode: String) {
        _technicianCode.value = newCode
        // Auto-complete technician name when code changes
        val selectedTechnician = _technicians.value.find { it.idUsuario == newCode }
        _technicianName.value = selectedTechnician?.nombre ?: ""
    }

    fun onTechnicianNameChange(newName: String) {
        // This function might be less used now if name is auto-completed
        _technicianName.value = newName
    }

    // This is called from the NavGraph to set the incident code received
    fun setIncidentCode(code: String?) {
        _incidentCode.value = code ?: ""
    }


    fun onAssignClick() {
        // Here you would implement your logic to assign the incident to the technician:
        _assignmentSuccess.value = false // Reset success flag

        val currentIncidentCode = _incidentCode.value
        val currentTechnicianCode = _technicianCode.value

        if (currentIncidentCode == "" || currentTechnicianCode == "") {
            println("Assignment failed: Invalid incident or technician code.")
            return
        }

        viewModelScope.launch {
            try {
                // Obtén la referencia al documento del incidente
                val incidenteRef = firestore.collection("incidente").document(currentIncidentCode)

                // Crea un mapa con los campos a actualizar
                val updates = hashMapOf<String, Any>(
                    "codigoTecnico" to currentTechnicianCode
                )

                // Realiza la actualización del documento
                incidenteRef.update(updates).await()

            } catch (e: Exception) {
                Log.e("IncidenteUpdateViewModel", "Error al actualizar código técnico para $currentIncidentCode", e)
            }
        }
    }

    private fun clearFields() {
        _technicianCode.value = ""
        _technicianName.value = ""
        // _incidentCode.value = "" // Don't clear incident code if staying on the same detail screen
        // or if it's set once from navigation args.
    }

    fun resetAssignmentSuccess() {
        _assignmentSuccess.value = false
    }
}