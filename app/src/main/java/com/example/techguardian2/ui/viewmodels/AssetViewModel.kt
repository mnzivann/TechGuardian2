package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.local.AssetEntity
import com.example.techguardian2.data.remote.TicketResponseDto
import com.example.techguardian2.data.repository.MainRepository
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // --- 1. INVENTARIO (EQUIPOS) ---
    private val _assets = MutableStateFlow<List<AssetEntity>>(emptyList())
    val assets: StateFlow<List<AssetEntity>> = _assets.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allAssets.collect { lista ->
                _assets.value = lista
            }
        }
    }

    // --- 2. MAGIA: FILTRO DE PRIVACIDAD PARA TICKETS ---
    val misTickets: StateFlow<List<TicketResponseDto>> = repository.offlineTickets
        .combine(tokenManager.token) { listaTickets, token ->
            val miUsuario = token?.substringBefore("|") ?: "" // Saca "oficina1" u "oficina2"

            // Filtramos para que solo salgan TUS reportes
            listaTickets.filter { it.reporter == miUsuario }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun cargarDatos() {
        viewModelScope.launch {
            val token = tokenManager.token.first() ?: ""
            repository.syncTickets(token)
        }
    }

    // --- 3. CIERRE DE SESIÓN SEGURO ---
    fun cerrarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.saveToken("") // Vacía la llave
            repository.limpiarSesion() // Borra memoria local
            onSuccess()
        }
    }
}