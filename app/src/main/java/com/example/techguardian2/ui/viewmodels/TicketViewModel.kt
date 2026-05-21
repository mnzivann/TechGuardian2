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

    // Ahora la función acepta el parámetro 'reporter'
    fun enviarReporte(description: String, fotoBase64: String, reporter: String, onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val token = tokenManager.token.first() ?: ""
                // Le pasamos los 3 datos exactos que pide tu API
                val response = apiService.enviarTicket(token, TicketRequestDto(description, fotoBase64, reporter))
                onResultado(response.success)
            } catch (e: Exception) {
                e.printStackTrace()
                onResultado(false)
            }
        }
    }
}