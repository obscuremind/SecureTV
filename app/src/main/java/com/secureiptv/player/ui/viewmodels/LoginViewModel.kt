package com.secureiptv.player.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.secureiptv.player.data.repository.IPTVRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the login screen
 */
class LoginViewModel : ViewModel() {

    private val repository = IPTVRepository()
    
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    /**
     * Attempts to login with the provided credentials
     */
    fun login(dns: String, username: String, password: String) {
        if (dns.isBlank() || username.isBlank() || password.isBlank()) {
            _loginState.value = LoginState.Error("Please fill in all fields")
            return
        }
        
        _loginState.value = LoginState.Loading
        
        viewModelScope.launch {
            val result = repository.authenticate(dns, username, password)
            
            result.fold(
                onSuccess = {
                    _loginState.value = LoginState.Success
                },
                onFailure = { error ->
                    _loginState.value = LoginState.Error(error.message ?: "Authentication failed")
                }
            )
        }
    }
    
    /**
     * Checks if the user is already logged in
     */
    fun checkLoginStatus(): Boolean {
        return repository.isLoggedIn()
    }
    
    /**
     * Resets the login state to idle
     */
    fun resetState() {
        _loginState.value = LoginState.Idle
    }
    
    /**
     * Represents the state of the login process
     */
    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        object Success : LoginState()
        data class Error(val message: String) : LoginState()
    }
}