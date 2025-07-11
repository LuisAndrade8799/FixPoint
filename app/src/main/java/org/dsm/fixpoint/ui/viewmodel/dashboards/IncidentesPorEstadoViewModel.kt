package org.dsm.fixpoint.ui.viewmodel.dashboards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Necesario para kotlinx.coroutines.flow
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Clase para encapsular el estado de la UI del gráfico circular
data class IncidentesEstadoUiState(
    val incidentesPorEstado: Map<String, Int> = emptyMap(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class IncidentesPorEstadoViewModel : ViewModel() {

    private val db = Firebase.firestore

    // MutableStateFlow para que el ViewModel pueda modificar el estado
    private val _uiState = MutableStateFlow(IncidentesEstadoUiState())
    // StateFlow para exponer el estado de manera inmutable a la UI
    val uiState: StateFlow<IncidentesEstadoUiState> = _uiState.asStateFlow()

    init {
        // Al inicializar el ViewModel, comenzamos a escuchar los datos de Firestore
        fetchIncidentesEstadoData()
    }

    private fun fetchIncidentesEstadoData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        // Usamos addSnapshotListener para obtener actualizaciones en tiempo real
        db.collection("incidente")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar datos de incidentes: ${e.message}"
                    )
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val tempMap = mutableMapOf<String, Int>()
                    for (document in snapshot.documents) {
                        val estado = document.getString("estado") ?: "Estado Desconocido"
                        tempMap[estado] = (tempMap[estado] ?: 0) + 1
                    }
                    _uiState.value = _uiState.value.copy(
                        incidentesPorEstado = tempMap,
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "No se encontraron incidentes para el gráfico.",
                        incidentesPorEstado = emptyMap(),
                        isLoading = false
                    )
                }
            }
    }
}