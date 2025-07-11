package org.dsm.fixpoint.ui.viewmodel.dashboards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Clase que representa el estado de la UI del gráfico de incidentes
data class IncidentesChartUiState(
    val incidentesPorArea: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class charScreenViewModel : ViewModel() {

    private val db = Firebase.firestore

    // StateFlow para exponer el estado de la UI
    private val _uiState = MutableStateFlow(IncidentesChartUiState())
    val uiState: StateFlow<IncidentesChartUiState> = _uiState

    init {
        fetchIncidentesData()
    }

    private fun fetchIncidentesData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        // Usamos addSnapshotListener para escuchar cambios en tiempo real
        // Este listener se limpia automáticamente cuando el ViewModel se destruye si se usa lifecycle-aware observers.
        // Sin embargo, para un ViewModel, es mejor gestionarlo manualmente o usar un Flow adecuado de una capa de repositorio.
        // Para simplificar aquí, lo hacemos directamente, pero en una app más grande, un Flow sería preferible.
        db.collection("incidente")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar datos: ${e.message}"
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val tempMap = mutableMapOf<String, Int>()
                    for (document in snapshot.documents) {
                        val area = document.getString("areaUsuario") ?: "Área Desconocida"
                        tempMap[area] = (tempMap[area] ?: 0) + 1
                    }
                    _uiState.value = _uiState.value.copy(
                        incidentesPorArea = tempMap,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "No se encontraron incidentes.",
                        incidentesPorArea = emptyMap(),
                        isLoading = false
                    )
                }
            }
    }
}