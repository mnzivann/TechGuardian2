package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.TicketRequestDto
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Ya no pedimos el 'reporter' desde la pantalla, el ViewModel lo descubre solo
    fun enviarReporte(description: String, fotoBase64: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // El token ahora es algo como "oficina1|usuario"
                val token = tokenManager.token.first() ?: ""

                // Cortamos el texto antes de la línea vertical para sacar el nombre de usuario
                val username = token.substringBefore("|")

                val response = apiService.enviarTicket(token, TicketRequestDto(description, fotoBase64, username))
                onResultado(response.success)
            } catch (e: Exception) {
                e.printStackTrace()
                onResultado(false)
            }
        }
    }
}