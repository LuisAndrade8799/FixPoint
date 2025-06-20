package org.dsm.fixpoint.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Codigo") val codigo: Int = 0, // Auto-generado para PRIMARY KEY
    @ColumnInfo(name = "Usuario") val usuario: String,
    @ColumnInfo(name = "Nombre") val nombre: String,
    @ColumnInfo(name = "Contrasena") val contrasena: String, // ¡Considera encriptar esto en una app real!
    @ColumnInfo(name = "Tipo") val tipo: String // "jefe", "tecnico", "comun"
)








































