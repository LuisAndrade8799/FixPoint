package org.dsm.fixpoint.ui.viewmodel

import android.app.Application // Import Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel // Change ViewModel to AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Change ViewModel() to AndroidViewModel(application: Application)
class LoginViewModel(application: Application) : AndroidViewModel(application) {


    private val _loggedInUserId = MutableStateFlow<String?>(null)
    val loggedInUserId: StateFlow<String?> = _loggedInUserId

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _isCorreo = MutableStateFlow(true)
    val isCorreo: StateFlow<Boolean> = _isCorreo

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _loginEnabled = MutableStateFlow(false)
    val loginEnabled: StateFlow<Boolean> = _loginEnabled

    // New StateFlow to communicate login messages (e.g., success/failure) to the UI
    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage: StateFlow<String?> = _loginMessage

    // New StateFlow to communicate the user's role upon successful login
    private val _loggedInUserRole = MutableStateFlow<String?>(null)
    val loggedInUserRole: StateFlow<String?> = _loggedInUserRole

    private val firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        // Observe changes in username and password to enable/disable the login button
        viewModelScope.launch {
            combine(_username, _password) { user, pass ->
                user.isNotBlank() && pass.isNotBlank() && _isCorreo.value
            }.collect { isEnabled ->
                _loginEnabled.value = isEnabled
            }
        }
    }

    fun checkCorreo(newUsername: String):Boolean{
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(newUsername)
    }
    fun onUsernameChange(newUsername: String) {
        _isCorreo.value = checkCorreo(newUsername)
        _username.value = newUsername
        _loginMessage.value = null // Clear message when user starts typing again
        _loggedInUserRole.value = null // Clear role when user starts typing again
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _loginMessage.value = null // Clear message when user starts typing again
        _loggedInUserRole.value = null // Clear role when user starts typing again
    }

    fun onLoginClick() { // Changed return type to Unit as success/error is communicated via StateFlows
        // Clear previous messages and roles
        _loginMessage.value = null
        _loggedInUserRole.value = null

        val currentUsername = _username.value
        val currentPassword = _password.value

        if (currentUsername.isBlank() || currentPassword.isBlank()) {
            _loginMessage.value = "Por favor, ingrese usuario y contraseña."
            return
        }

        viewModelScope.launch {
            try {
                val authResult = firebaseAuth.signInWithEmailAndPassword(currentUsername, currentPassword).await()

                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val firebaseUid = firebaseUser.uid
                    Log.d("LoginViewModel", "Firebase UID: $firebaseUid")

                    // Query Firestore to get user details
                    val userDocRef = firestore.collection("usuario").document(firebaseUid) // Use UID as document ID
                    val documentSnapshot = userDocRef.get().await()

                    if (documentSnapshot.exists()) {
                        val idUsuario = documentSnapshot.getString("idUsuario")
                        val nombre = documentSnapshot.getString("nombre")
                        val tipo = documentSnapshot.getString("tipo")

                        _loggedInUserId.value = idUsuario // Set idUsuario from Firestore
                        _name.value = nombre ?: "" // Set name from Firestore
                        _loggedInUserRole.value = tipo ?: "" // Set role from Firestore
                        _loginMessage.value = "Inicio de sesión exitoso! Bienvenido, $nombre (${tipo ?: "usuario"})"

                        Log.d("LoginViewModel", "Firestore Data: idUsuario=$idUsuario, nombre=$nombre, tipo=$tipo")
                    } else {
                        _loginMessage.value = "Inicio de sesión exitoso, pero no se encontraron detalles de usuario en Firestore. UID: $firebaseUid"
                        // Fallback if no Firestore document is found for the UID
                        _loggedInUserId.value = firebaseUid
                        _name.value = firebaseUser.displayName ?: currentUsername.substringBefore("@")
                        _loggedInUserRole.value = "usuario_firebase_sin_datos_firestore"
                        Log.w("LoginViewModel", "No Firestore document found for UID: $firebaseUid")
                    }
                } else {
                    _loginMessage.value = "Error en el inicio de sesión. Credenciales incorrectas."
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "No hay cuenta registrada con este correo electrónico."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta o correo electrónico inválido."
                    else -> "Error al intentar iniciar sesión: ${e.localizedMessage ?: "Error desconocido"}"
                }
                _loginMessage.value = errorMessage
                Log.e("LoginViewModel", "Login error: ${e.message}", e)
            }
        }
    }
}





















