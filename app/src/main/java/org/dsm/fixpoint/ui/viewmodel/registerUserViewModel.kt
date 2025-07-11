package org.dsm.fixpoint.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dsm.fixpoint.database.entities.Usuario


class RegisterUserViewModel(application: Application) : AndroidViewModel(application) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _isCorreo = MutableStateFlow(true)
    val isCorreo: StateFlow<Boolean> = _isCorreo
    // Estados para los campos de entrada
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val name = MutableStateFlow("")
    val selectedUserType = MutableStateFlow("comun") // Valor inicial del Spinner
    val areaUsuario = MutableStateFlow("") // Campo de área de usuario

    // Estado para controlar la visibilidad del campo "Área de usuario"
    val isAreaUsuarioVisible: StateFlow<Boolean> = MutableStateFlow(true) // Inicialmente visible si "comun" es el valor por defecto

    init {
        // Observa el tipo de usuario para actualizar la visibilidad del campo de área
        viewModelScope.launch {
            selectedUserType.collect { type ->
                (isAreaUsuarioVisible as MutableStateFlow).value = (type == "comun")
            }
        }
    }

    // Estado para comunicar el resultado del registro a la UI
    private val _registrationResult = MutableStateFlow<String?>(null)
    val registrationResult: StateFlow<String?> = _registrationResult

    fun checkCorreo(newUsername: String):Boolean{
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(newUsername)
    }

    fun onUsernameChange(newUsername: String) {
        _isCorreo.value = checkCorreo(newUsername)
        email.value = newUsername
    }
    /**
     * Registra un nuevo usuario en Firebase Authentication y luego guarda sus datos en Firestore.
     */
    fun registerUser() {
        _registrationResult.value = null // Limpiar el resultado anterior

        val currentEmail = email.value
        val currentPassword = password.value
        val currentName = name.value
        val currentType = selectedUserType.value
        val currentAreaUsuario = areaUsuario.value.takeIf { currentType == "comun" } // Solo si es tipo "comun"

        if (currentEmail.isBlank() || currentPassword.isBlank() || currentName.isBlank() || currentType.isBlank()) {
            _registrationResult.value = "Por favor, complete todos los campos requeridos."
            return
        }

        if (currentType == "comun" && currentAreaUsuario.isNullOrBlank()) {
            _registrationResult.value = "Para usuarios 'comun', el campo 'Área de usuario' es requerido."
            return
        }

        viewModelScope.launch {
            try {
                // 1. Registrar usuario en Firebase Authentication
                val authResult = firebaseAuth.createUserWithEmailAndPassword(currentEmail, currentPassword).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    val uid = firebaseUser.uid // UUID generado por Firebase Auth
                    Log.d("RegisterUserViewModel", "Usuario registrado en Auth con UID: $uid")

                    // 2. Guardar datos del usuario en Firestore
                    val userDocRef = firestore.collection("usuario").document(uid) // UID como ID del documento

                    val firestoreUser: Usuario = if (currentType == "comun") {
                        Usuario(
                            idUsuario = uid,
                            nombre = currentName,
                            tipo = currentType,
                            area = currentAreaUsuario // Solo para tipo "comun"
                        )
                    } else {
                        // Para "jefe" o "tecnico", areaUsuario es null
                        Usuario(
                            idUsuario = uid,
                            nombre = currentName,
                            tipo = currentType,
                            area = null
                        )
                    }

                    userDocRef.set(firestoreUser).await() // Guardar en Firestore

                    _registrationResult.value = "Usuario '$currentName' (${currentType}) registrado exitosamente!"
                    Log.d("RegisterUserViewModel", "Datos de usuario guardados en Firestore para UID: $uid")

                } else {
                    _registrationResult.value = "Error desconocido al registrar en Firebase Auth."
                }

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Ya existe una cuenta registrada con este correo electrónico."
                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "El formato del correo electrónico es inválido."
                    else -> "Error al registrar: ${e.localizedMessage ?: "Error desconocido"}"
                }
                _registrationResult.value = errorMessage
                Log.e("RegisterUserViewModel", "Error durante el registro de usuario: ${e.message}", e)
            }
        }
    }

    /**
     * Limpia el mensaje de resultado de registro después de que la UI lo haya mostrado.
     */
    fun clearRegistrationResult() {
        _registrationResult.value = null
    }
}