package com.example.techguardian2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.TicketResponseDto
import com.example.techguardian2.data.security.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.hilt.navigation.compose.hiltViewModel

// Mini-ViewModel interno para actualizar el inventario automáticamente
@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _tickets = MutableStateFlow<List<TicketResponseDto>>(emptyList())
    val tickets: StateFlow<List<TicketResponseDto>> = _tickets

    fun cargarDatos() {
        viewModelScope.launch {
            try {
                val token = tokenManager.token.first() ?: ""
                _tickets.value = apiService.obtenerTickets(token)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    onNavigateToTicket: () -> Unit,
    viewModel: InventarioViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    val ticketsList by viewModel.tickets.collectAsState()

    // Cada vez que el usuario vuelve a abrir esta vista, recarga los estados desde Java
    LaunchedEffect(Unit) {
        viewModel.cargarDatos()
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Panel Oficina") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToTicket) {
                Icon(Icons.Default.Add, contentDescription = "Reportar Falla")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Selector de Pestañas
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Equipos") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Mis Reportes") })
            }

            if (selectedTab == 0) {
                // PESTAÑA INVENTARIO BASE
                Column(modifier = Modifier.padding(16.dp)) {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Impresora HP Laser", style = MaterialTheme.typography.titleMedium)
                            Text("S/N: HP-99211A - Estado: Activo", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            } else {
                // PESTAÑA DE REPORTES CON SUS 3 ESTADOS
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

                                // PINTAR EL ESTADO (Los 3 Estados visuales solicitados)
                                val (estadoTexto, colorEstado) = when (ticket.status) {
                                    "recibido" -> "Recibido por Técnico" to Color(0xFF2196F3) // Azul
                                    "en_proceso" -> "En Reparación" to Color(0xFFFF9800) // Naranja
                                    "resuelto" -> "Solucionado" to Color(0xFF4CAF50) // Verde
                                    else -> "No completado" to Color(0xFFF44336) // Rojo
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