package com.example.techguardian2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.techguardian2.data.remote.TicketResponseDto
import com.example.techguardian2.data.repository.MainRepository
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// --- 1. EL VIEWMODEL EN MODO DEPURACIÓN (FILTRO APAGADO) ---

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // Dejamos pasar TODOS los tickets para ver qué llega realmente del servidor
    val misTickets: StateFlow<List<TicketResponseDto>> = repository.offlineTickets
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun cargarDatos() {
        viewModelScope.launch {
            val token = tokenManager.token.first() ?: ""
            repository.syncTickets(token)
        }
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            tokenManager.saveToken("")
            repository.limpiarSesion()
            onSuccess()
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
    val ticketsList by viewModel.misTickets.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas salir de TechGuardian?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        viewModel.cerrarSesion {
                            onLogout()
                        }
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
                    IconButton(onClick = { viewModel.cargarDatos() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar Reportes")
                    }
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

                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        viewModel.cargarDatos()
                    },
                    text = { Text("Mis Reportes") }
                )
            }

            if (selectedTab == 0) {
                // La misma lista maestra para que coincida perfectamente
                val equiposDisponibles = listOf("Impresora HP Laser", "MacBook Air M1", "Router Cisco RT-500", "Monitor Dell 27\"")

                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                    items(equiposDisponibles) { equipo ->

                        // --- ESTADO DINÁMICO ---
                        // Buscamos si existe un reporte activo para este equipo en específico
                        val tieneFallaActiva = ticketsList.any { ticket ->
                            ticket.description.startsWith("[$equipo]") && ticket.status != "resuelto"
                        }

                        // Cambiamos texto y color en tiempo real sin tocar bases de datos extra
                        val estadoTexto = if (tieneFallaActiva) "Inactivo (Reporte Abierto)" else "Activo"
                        val colorEstado = if (tieneFallaActiva) Color(0xFFF44336) else Color(0xFF4CAF50)

                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(equipo, style = MaterialTheme.typography.titleMedium)
                                    Text("Estado actual: $estadoTexto", style = MaterialTheme.typography.bodyMedium, color = colorEstado)
                                }
                            }
                        }
                    }
                }
            }
            else {
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

                                    // --- LÍNEA CHISMOSA PARA VER QUÉ MANDA JAVA ---
                                    Text("Dueño según el servidor: ${ticket.reporter}", color = Color.Red, style = MaterialTheme.typography.bodySmall)

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