package org.dsm.fixpoint.ui.viewmodel

import android.app.Application // Import Application
import androidx.lifecycle.AndroidViewModel // Change ViewModel to AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.dsm.fixpoint.database.AppDatabase

// Change ViewModel() to AndroidViewModel(application: Application)
class LoginViewModel(application: Application) : AndroidViewModel(application) {


    private val _loggedInUserId = MutableStateFlow<Int?>(null)
    val loggedInUserId: StateFlow<Int?> = _loggedInUserId

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

    private val _isCorreo = MutableStateFlow(false)
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
    // Get an instance of your AppDatabase and then the UsuarioDao
    private val userDao = AppDatabase.getDatabase(application).usuarioDao()

    init {
        // Observe changes in username and password to enable/disable the login button
        viewModelScope.launch {
            combine(_username, _password) { user, pass ->
                user.isNotBlank() && pass.isNotBlank()
            }.collect { isEnabled ->
                _loginEnabled.value = isEnabled
            }
        }
    }

    private fun checkCorreo(newUsername: String):Boolean{
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$".toRegex()
        return emailRegex.matches(newUsername)
    }
    fun onUsernameChange(newUsername: String) {
        if (checkCorreo(newUsername)){
            _username.value = newUsername
            _loginMessage.value = null // Clear message when user starts typing again
            _loggedInUserRole.value = null // Clear role when user starts typing again
        }else{
            _username.value = ""
        }

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
                // Use Firebase Auth to sign in with email and password
                val result = firebaseAuth.signInWithEmailAndPassword(currentUsername, currentPassword).await()

                if (result.user != null) {
                    _loginMessage.value = "Inicio de sesión exitoso!"
                    // You might fetch additional user data (like role) from Firestore or a Realtime Database here
                    // For now, setting a placeholder role or fetching from a custom claim if you've set them up.
                    _loggedInUserRole.value = "usuario_firebase" // Placeholder role
                    _loggedInUserId.value = result.user?.uid.hashCode() // Using UID hash as an example ID
                    _name.value = result.user?.displayName ?: currentUsername.substringBefore("@") // Use display name or part of email
                } else {
                    _loginMessage.value = "Error en el inicio de sesión. Credenciales incorrectas."
                }
            } catch (e: Exception) {
                // Handle Firebase Authentication specific errors
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "No hay cuenta registrada con este correo electrónico."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta o correo electrónico inválido."
                    else -> "Error al intentar iniciar sesión: ${e.localizedMessage ?: "Error desconocido"}"
                }
                _loginMessage.value = errorMessage
                e.printStackTrace()
            }
        }
    }
}





















