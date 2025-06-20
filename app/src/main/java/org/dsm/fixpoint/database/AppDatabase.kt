package org.dsm.fixpoint.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dsm.fixpoint.database.dao.IncidenteDao
import org.dsm.fixpoint.database.dao.UsuarioDao
import org.dsm.fixpoint.database.entities.Incidente
import org.dsm.fixpoint.database.entities.Usuario

@Database(entities = [Usuario::class, Incidente::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun incidenteDao(): IncidenteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "incident_management_db" // Nombre de tu archivo de base de datos
                )
                    .addCallback(AppDatabaseCallback(context)) // Opcional: para pre-poblar o ejecutar migraciones
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val context: Context // Necesitas el contexto para los recursos si pre-poblas
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Lógica para pre-poblar la base de datos la primera vez que se crea
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val usuarioDao = database.usuarioDao()
                    // Ejemplo de pre-población de usuarios
                    usuarioDao.insertUsuario(Usuario(usuario = "jefe1", nombre = "Jefe Area 1", contrasena = "123", tipo = "jefe"))

                    usuarioDao.insertUsuario(Usuario(usuario = "jefe2", nombre = "Jefe Area 2", contrasena = "123", tipo = "jefe"))
                    usuarioDao.insertUsuario(Usuario(usuario = "jefe3", nombre = "Jefe Area 3", contrasena = "123", tipo = "jefe"))


                    usuarioDao.insertUsuario(Usuario(usuario = "tec1", nombre = "Tecnico Uno", contrasena = "123", tipo = "tecnico"))
                    usuarioDao.insertUsuario(Usuario(usuario = "tec2", nombre = "Tecnico Dos", contrasena = "123", tipo = "tecnico"))

                    usuarioDao.insertUsuario(Usuario(usuario = "tec3", nombre = "Tecnico Tres", contrasena = "123", tipo = "tecnico"))
                    usuarioDao.insertUsuario(Usuario(usuario = "tec4", nombre = "Tecnico Cuatro", contrasena = "123", tipo = "tecnico"))
                    usuarioDao.insertUsuario(Usuario(usuario = "tec5", nombre = "Tecnico Cinco", contrasena = "123", tipo = "tecnico"))
                    usuarioDao.insertUsuario(Usuario(usuario = "tec6", nombre = "Tecnico Seis", contrasena = "123", tipo = "tecnico"))
                    usuarioDao.insertUsuario(Usuario(usuario = "tec7", nombre = "Tecnico Siete", contrasena = "123", tipo = "tecnico"))


                    usuarioDao.insertUsuario(Usuario(usuario = "user1", nombre = "Usuario General", contrasena = "123", tipo = "comun"))

                    usuarioDao.insertUsuario(Usuario(usuario = "user2", nombre = "Usuario General 2", contrasena = "123", tipo = "comun"))
                    usuarioDao.insertUsuario(Usuario(usuario = "user3", nombre = "Usuario General 3", contrasena = "123", tipo = "comun"))
                    usuarioDao.insertUsuario(Usuario(usuario = "user4", nombre = "Usuario General 4", contrasena = "123", tipo = "comun"))
                    usuarioDao.insertUsuario(Usuario(usuario = "user5", nombre = "Usuario General 5", contrasena = "123", tipo = "comun"))
                    usuarioDao.insertUsuario(Usuario(usuario = "user6", nombre = "Usuario General 6", contrasena = "123", tipo = "comun"))

                    val incidenteDao = database.incidenteDao()
                    // Add initial incidents here
                    val incidente1 = Incidente(
                        nombreUsuario = "Usuario1",
                        areaDeUsuario = "Area1",
                        descripcion = "Descripción de la incidencia 1",
                        estado = "Sin atender",
                        codigoTecnico = null,
                        codigoEquipo = "Equipo1"
                    )
                    val incidente2 = Incidente(
                        nombreUsuario = "Usuario2",
                        areaDeUsuario = "Area2",
                        descripcion = "Descripción de la incidencia 2",
                        estado = "Sin atender",
                        codigoTecnico = null,
                        codigoEquipo = "Equipo2"
                    )

                    val incidente3 = Incidente(
                        nombreUsuario = "Usuario3",
                        areaDeUsuario = "Area3",
                        descripcion = "Descripción de la incidencia 3",
                        estado = "Sin atender",
                        codigoTecnico = null,
                        codigoEquipo = "Equipo3"
                    )

                    val incidente4 = Incidente(
                        nombreUsuario = "Usuario4",
                        areaDeUsuario = "Area4",
                        descripcion = "Descripción de la incidencia 4",
                        estado = "Sin atender",
                        codigoTecnico = null,
                        codigoEquipo = "Equipo4"
                    )

                    val incidente5 = Incidente(
                        nombreUsuario = "Usuario5",
                        areaDeUsuario = "Area5",
                        descripcion = "Descripción de la incidencia 5",
                        estado = "Sin atender",
                        codigoTecnico = null,
                        codigoEquipo = "Equipo5"
                    )

                    val incidente6 = Incidente(
                        nombreUsuario = "Usuario6",
                        areaDeUsuario = "Area6",
                        descripcion = "Descripción de la incidencia 6",
                        estado = "Sin atender",
                        codigoTecnico = null,
                        codigoEquipo = "Equipo6"
                    )

                    val incidente7 = Incidente(
                        nombreUsuario = "Usuario7",
                        areaDeUsuario = "Area7",
                        descripcion = "Descripción de la incidencia 7",
                        estado = "Sin atender",
                        codigoTecnico = null,
                        codigoEquipo = "Equipo7"
                    )

                    incidenteDao.insertIncidente(incidente1)
                    incidenteDao.insertIncidente(incidente2)
                    incidenteDao.insertIncidente(incidente3)
                    incidenteDao.insertIncidente(incidente4)
                    incidenteDao.insertIncidente(incidente5)
                    incidenteDao.insertIncidente(incidente6)
                    incidenteDao.insertIncidente(incidente7)

                }
            }
        }
    }
}







