package org.dsm.fixpoint.ui.viewmodel


import org.dsm.fixpoint.database.AppDatabase
import org.dsm.fixpoint.database.entities.Incidente
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.dsm.fixpoint.database.entities.Usuario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegisterIncidentViewModel(application: Application) : AndroidViewModel(application) {

    private val _equipmentCode = MutableStateFlow("")
    val equipmentCode: StateFlow<String> = _equipmentCode

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _userArea = MutableStateFlow("")
    val userArea: StateFlow<String> = _userArea

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _sendButtonEnabled = MutableStateFlow(false)
    val sendButtonEnabled: StateFlow<Boolean> = _sendButtonEnabled

    private val _incidentMessage = MutableStateFlow<String?>(null)
    val incidentMessage: StateFlow<String?> = _incidentMessage

    private val _technicians = MutableStateFlow<List<Usuario>>(emptyList())
    val technicians: StateFlow<List<Usuario>> = _technicians.asStateFlow()

    private val incidenteDao = AppDatabase.getDatabase(application).incidenteDao()
    private val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()

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
                usuarioDao.getUsersByType("comun").collect { techList ->
                    _technicians.value = techList
                }
            } catch (e: Exception) {
                println("Error loading technicians: ${e.localizedMessage}")
                _technicians.value = emptyList()
            }
        }
    }

    fun onTechnicianCodeChange(newCode: String) {
        _username.value = newCode
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
                val newIncident = Incidente(
                    nombreUsuario = _username.value,
                    areaDeUsuario = _userArea.value,
                    descripcion = _description.value,
                    estado = "Sin atender",
                    codigoTecnico = null,
                    codigoEquipo = _equipmentCode.value // Pass the equipment code
                )

                val incidentId = incidenteDao.insertIncidente(newIncident)

                if (incidentId > 0) {
                    _incidentMessage.value = "Incidencia registrada con éxito! Código: $incidentId"
                    clearFields()
                } else {
                    _incidentMessage.value = "Error al registrar la incidencia."
                }
            } catch (e: Exception) {
                _incidentMessage.value = "Error de base de datos al registrar: ${e.localizedMessage ?: "Error desconocido"}"
                e.printStackTrace()
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



