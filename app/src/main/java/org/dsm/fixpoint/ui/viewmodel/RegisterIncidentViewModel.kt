package org.dsm.fixpoint.ui.viewmodel

import org.dsm.fixpoint.database.entities.Incidente
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
import org.dsm.fixpoint.database.entities.Usuario

class RegisterIncidentViewModel(application: Application) : AndroidViewModel(application) {

    private val _equipmentCode = MutableStateFlow("")
    val equipmentCode: StateFlow<String> = _equipmentCode

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _userArea = MutableStateFlow("")
    val userArea: StateFlow<String> = _userArea.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _sendButtonEnabled = MutableStateFlow(false)
    val sendButtonEnabled: StateFlow<Boolean> = _sendButtonEnabled

    private val _incidentMessage = MutableStateFlow<String?>(null)
    val incidentMessage: StateFlow<String?> = _incidentMessage

    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _technicians = MutableStateFlow<List<Usuario>>(emptyList())
    val technicians: StateFlow<List<Usuario>> = _technicians.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _equipmentCode,
                _username,
                _userArea,
                _description
            ) { code, user, area, desc ->
                code.isNotBlank() && user.isNotBlank() && area.isNotBlank() && desc.isNotBlank()
            }.collect { isEnabled ->
                _sendButtonEnabled.value = isEnabled
            }
        }
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            try {
                val result = firestore.collection("usuario")
                    .whereEqualTo("tipo", "comun") // Busca documentos donde el campo 'tipo' es 'tecnico'
                    .get()
                    .await()

                val userList = result.documents.mapNotNull { document ->
                    // Mapea el documento a tu clase de datos Usuario
                    try {
                        Usuario(
                            idUsuario = document.getString("idUsuario") ?: "",
                            nombre = document.getString("nombre") ?: "",
                            tipo = document.getString("tipo") ?: "",
                            area = document.getString("area") ?: ""
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
        val selectedTechnician = _technicians.value.find { it.idUsuario == newCode }
        _username.value = selectedTechnician?.nombre ?: ""

        _userArea.value = selectedTechnician?.area ?: ""
        // Auto-complete technician name when code changes
    }

    fun onEquipmentCodeChange(newCode: String) {
        _equipmentCode.value = newCode
        _incidentMessage.value = null
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
        _incidentMessage.value = null
    }

    fun onUserAreaChange(newUserArea: String) {
        _userArea.value = newUserArea
        _incidentMessage.value = null
    }


    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
        _incidentMessage.value = null
    }

    fun onSendClick() {
        _incidentMessage.value = null

        if (!_sendButtonEnabled.value) {
            _incidentMessage.value = "Por favor, complete todos los campos."
            return
        }
        viewModelScope.launch {
            try {
                // Generar un nuevo ID de documento automáticamente por Firestore
                val newDocRef = firestore.collection("incidente").document()
                val incidenteId = newDocRef.id // Obtener el ID generado automáticamente

                // Crear el objeto Incidente con los valores predeterminados y proporcionados
                val newIncidente = Incidente(
                    areaUsuario = _userArea.value,
                    codigo = incidenteId, // El campo 'codigo' se llenará con el ID del documento
                    nombreEquipo = _equipmentCode.value,
                    codigoTecnico = "", // Por defecto: vacío
                    descripcion = _description.value,
                    estado = "Sin atender", // Por defecto: "Sin atender"
                    nombreUsuario = _username.value
                )

                // Guardar el nuevo incidente en Firestore
                newDocRef.set(newIncidente).await()
                Log.d("IncidenteRegViewModel", "Nueva incidencia registrada: $incidenteId")

            } catch (e: Exception) {
                Log.e("IncidenteRegViewModel", "Error al registrar nueva incidencia", e)
            }
        }
    }

    private fun clearFields() {
        _equipmentCode.value = ""
        _username.value = ""
        _userArea.value = ""
        _description.value = ""
    }
}



