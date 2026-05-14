package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.LoginRequestDto
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    fun performLogin(username: String, password: String, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Hablamos con la API de Java a través de Ngrok
                val response = apiService.loginUsuario(LoginRequestDto(username, password))

                if (response.success) {
                    // Guardamos el token para futuras peticiones
                    tokenManager.saveToken(response.token)

                    // Le pasamos el ROL a la pantalla para saber a dónde navegar
                    onComplete(response.role)
                }
            } catch (e: Exception) {
                // Manejo de errores en caso de fallo de red
                e.printStackTrace()
            }
        }
    }
}