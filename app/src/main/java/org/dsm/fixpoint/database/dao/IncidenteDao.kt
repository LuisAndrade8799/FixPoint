package org.dsm.fixpoint.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.dsm.fixpoint.database.entities.Incidente

@Dao
interface IncidenteDao {
    @Insert
    suspend fun insertIncidente(incidente: Incidente): Long

    @Update
    suspend fun updateIncidente(incidente: Incidente)

    @Query("SELECT * FROM Incidente")
    fun getAllIncidentes(): Flow<List<Incidente>>

    @Query("SELECT * FROM Incidente WHERE estado = 'Sin atender'")
    fun getIncidentesSinAtender(): Flow<List<Incidente>>

    @Query("SELECT * FROM Incidente WHERE estado = 'Asignado' AND codigotecnico = :tecnicoCodigo")
    fun getIncidentesAsignados(tecnicoCodigo: Int): Flow<List<Incidente>>

    @Query("SELECT * FROM Incidente WHERE estado = 'Pendiente' AND codigotecnico = :tecnicoCodigo")
    fun getIncidentesPendientes(tecnicoCodigo: Int): Flow<List<Incidente>>

    @Query("SELECT * FROM Incidente WHERE codigo = :incidenteCodigo")
    suspend fun getIncidentById(incidenteCodigo: Int): Incidente?

    // Para la pantalla de Estado de Incidencias (Usuario común)
    @Query("SELECT * FROM Incidente WHERE nombreUsuario = :username")
    fun getIncidentesByUsuario(username: String): Flow<List<Incidente>>

    @Query("SELECT * FROM Incidente")
    fun getAllIncidents(): Flow<List<Incidente>> // Use Flow for observing changes

    @Query("SELECT * FROM Incidente WHERE codigoTecnico = :technicianId")
    fun getIncidentsByTechnician(technicianId: Int): Flow<List<Incidente>>

    @Query("SELECT * FROM Incidente WHERE codigoTecnico = :technicianCode AND estado = :status")
    suspend fun getAssignedUnattendedIncidents(technicianCode: Int, status: String): List<Incidente>
}





















































