package org.dsm.fixpoint.ui.viewmodel.technicianVM

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel // Change to AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dsm.fixpoint.database.entities.Incidente

class AttendIncidentViewModel(application: Application) : AndroidViewModel(application) {

    // Status message for UI feedback (success/error)
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    // The ID of the incident being attended, will be set via setIncidentId
    private var currentIncidentId: String = ""

    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    // Hold the fetched incident to easily update its status and description
    private val _incident = MutableStateFlow<Incidente?>(null)
    val incident: StateFlow<Incidente?> = _incident.asStateFlow()

    // Method to set incident ID and load details, similar to setIncidentCode in Assign
    fun setIncidentId(id: String) {
        if (currentIncidentId != id) { // Only load if ID changes
            currentIncidentId = id
            loadIncidentDetails(id)
        }
    }

    private fun loadIncidentDetails(id: String) {
        viewModelScope.launch {
            try {
                // Obtiene la referencia al documento directamente por su ID
                val documentSnapshot = firestore.collection("incidente")
                    .document(id)
                    .get()
                    .await()

                if (documentSnapshot.exists()) {
                    // Mapea el documento a tu clase de datos Incidente
                    val incidenteEncontrado = Incidente(
                        areaUsuario = documentSnapshot.getString("areaUsuario") ?: "",
                        // El 'codigo' del documento es el ID del documento mismo
                        codigo = documentSnapshot.getString("codigo") ?: id,
                        nombreEquipo = documentSnapshot.getString("nombreEquipo") ?: "",
                        codigoTecnico = documentSnapshot.getString("codigoTecnico"),
                        descripcion = documentSnapshot.getString("descripcion") ?: "",
                        estado = documentSnapshot.getString("estado") ?: "",
                        nombreUsuario = documentSnapshot.getString("nombreUsuario") ?: ""
                    )
                    _incident.value = incidenteEncontrado

                    Log.d("IncidenteDetailViewModel", "Incidente encontrado: ${incidenteEncontrado.codigo}")
                } else {
                    Log.d("IncidenteDetailViewModel", "Incidente no encontrado con código: $id")
                }

            } catch (e: Exception) {
                val message = "Error al obtener el incidente por código: ${e.localizedMessage ?: "Error desconocido"}"
                Log.e("IncidenteDetailViewModel", message, e)
            }
        }
    }

    fun onDescriptionChange(newDescription: String) {

    }

    fun onSolvedClick() {
        val id = _incident.value?.codigo.toString()
        viewModelScope.launch {
            try {
                // Obtén la referencia al documento del incidente
                val incidenteRef = firestore.collection("incidente").document(id)

                // Crea un mapa con los campos a actualizar
                val updates = hashMapOf<String, Any>(
                    "estado" to "Solucionado"
                )

                // Realiza la actualización del documento
                incidenteRef.update(updates).await()

                Log.d("IncidenteStatusViewModel", "Estado de incidente $id actualizado a Solucionado")

            } catch (e: Exception) {
                Log.e("IncidenteStatusViewModel", "Error al actualizar estado de incidente $id", e)
            }
        }
    }

    fun onPendingClick() {
        val id = _incident.value?.codigo.toString()
        viewModelScope.launch {
            try {
                // Obtén la referencia al documento del incidente

                val incidenteRef = firestore.collection("incidente").document(id)

                // Crea un mapa con los campos a actualizar
                val updates = hashMapOf<String, Any>(
                    "estado" to "Pendiente"
                )

                // Realiza la actualización del documento
                incidenteRef.update(updates).await()

                Log.d("IncidenteStatusViewModel", "Estado de incidente $id actualizado a Pendiente ")

            } catch (e: Exception) {
                Log.e("IncidenteStatusViewModel", "Error al actualizar estado de incidente $id", e)
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    private fun clearIncidentDetails() {
        _incident.value = null
    }
}