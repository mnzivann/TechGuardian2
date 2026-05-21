package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.TicketResponseDto
import com.example.techguardian2.data.remote.UpdateStatusDto
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TechnicianViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _tickets = MutableStateFlow<List<TicketResponseDto>>(emptyList())
    val tickets: StateFlow<List<TicketResponseDto>> = _tickets

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    fun cargarTickets() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val token = tokenManager.token.first() ?: ""
                _tickets.value = apiService.obtenerTickets(token)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _cargando.value = false
            }
        }
    }

    fun actualizarEstado(ticketId: Int, nuevoEstado: String) {
        viewModelScope.launch {
            try {
                val token = tokenManager.token.first() ?: ""
                val response = apiService.actualizarEstadoTicket(token, ticketId, UpdateStatusDto(nuevoEstado))
                if (response.success) {
                    cargarTickets() // Recargar lista automáticamente al mutar
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}