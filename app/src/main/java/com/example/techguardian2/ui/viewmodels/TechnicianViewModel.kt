package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.TicketResponseDto
import com.example.techguardian2.data.remote.UpdateStatusDto
import com.example.techguardian2.data.repository.MainRepository
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TechnicianViewModel @Inject constructor(
    private val repository: MainRepository,
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    val tickets: StateFlow<List<TicketResponseDto>> = repository.offlineTickets
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun cargarTickets() {
        viewModelScope.launch {
            val token = tokenManager.token.first() ?: ""
            repository.syncTickets(token)
        }
    }

    fun actualizarEstado(ticketId: Int, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                val token = tokenManager.token.first() ?: ""
                val response = apiService.actualizarEstadoTicket(token, ticketId, UpdateStatusDto(nuevoEstado))
                if (response.success) {
                    repository.syncTickets(token)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Offline: no se puede actualizar
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch { tokenManager.saveToken("") }
    }
}