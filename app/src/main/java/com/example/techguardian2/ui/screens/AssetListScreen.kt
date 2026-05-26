package com.example.techguardian2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.TicketResponseDto
import com.example.techguardian2.data.repository.MainRepository
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- 1. EL VIEWMODEL QUE FALTABA ---

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Leemos SIEMPRE del celular. Es instantáneo y funciona offline.
    val tickets: StateFlow<List<TicketResponseDto>> = repository.offlineTickets
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun cargarDatos() {
        viewModelScope.launch {
            val token = tokenManager.token.first() ?: ""
            repository.syncTickets(token) // Intenta actualizar en silencio
        }
    }

    // Nueva función para borrar la sesión
    fun cerrarSesion() {
        viewModelScope.launch {
            tokenManager.saveToken("") // Vacía el token
        }
    }
}

// --- 2. LA INTERFAZ DE OFICINA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    onNavigateToTicket: () -> Unit,
    onLogout: () -> Unit,
    viewModel: InventarioViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    val ticketsList by viewModel.tickets.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas salir de TechGuardian?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, salir")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel TechGuardian") },
                actions = {
                    IconButton(onClick = { mostrarDialogo = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTicket) {
                Icon(Icons.Default.Add, contentDescription = "Reportar Falla")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Equipos") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Mis Reportes") })
            }

            if (selectedTab == 0) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Impresora HP Laser", style = MaterialTheme.typography.titleMedium)
                            Text("S/N: HP-99211A - Estado: Activo", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    if (ticketsList.isEmpty()) {
                        item { Text("No tienes reportes de falla activos.", modifier = Modifier.padding(16.dp)) }
                    }
                    items(ticketsList) { ticket ->
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Reporte #${ticket.id}", style = MaterialTheme.typography.titleMedium)
                                    Text(ticket.description, style = MaterialTheme.typography.bodyMedium)
                                }

                                val (estadoTexto, colorEstado) = when (ticket.status) {
                                    "recibido" -> "Recibido por Técnico" to Color(0xFF2196F3)
                                    "en_proceso" -> "En Reparación" to Color(0xFFFF9800)
                                    "resuelto" -> "Solucionado" to Color(0xFF4CAF50)
                                    else -> "No completado" to Color(0xFFF44336)
                                }

                                Surface(
                                    color = colorEstado.copy(alpha = 0.15f),
                                    contentColor = colorEstado,
                                    shape = CircleShape,
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(
                                        text = estadoTexto,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}