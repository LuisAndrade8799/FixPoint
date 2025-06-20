package org.dsm.fixpoint.ui.viewmodel

import android.app.Application // Import Application
import androidx.lifecycle.AndroidViewModel // Change ViewModel to AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.dsm.fixpoint.database.AppDatabase

// Change ViewModel() to AndroidViewModel(application: Application)
class LoginViewModel(application: Application) : AndroidViewModel(application) {


    private val _loggedInUserId = MutableStateFlow<Int?>(null)
    val loggedInUserId: StateFlow<Int?> = _loggedInUserId

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username

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

    fun onUsernameChange(newUsername: String) {
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
                // Use the login method from your UsuarioDao
                val user = userDao.login(currentUsername, currentPassword)

                if (user != null) {
                    _loginMessage.value = "Inicio de sesión exitoso como ${user.tipo}!"
                    _loggedInUserRole.value = user.tipo // Set the user's role
                    _loggedInUserId.value = user.codigo
                    _name.value = user.nombre

                } else {
                    _loginMessage.value = "Usuario o contraseña incorrectos."
                }
            } catch (e: Exception) {
                // Handle potential database errors (e.g., database not found, connection issues)
                _loginMessage.value = "Error al intentar iniciar sesión: ${e.localizedMessage ?: "Error desconocido"}"
                e.printStackTrace() // Log the exception for debugging
            }
        }
    }
}





















