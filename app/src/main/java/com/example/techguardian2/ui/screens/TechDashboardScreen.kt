package com.example.techguardian2.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.techguardian2.data.remote.TicketResponseDto
import com.example.techguardian2.ui.viewmodels.TechnicianViewModel

@Composable
fun rememberBase64Decoder(base64String: String): Bitmap? {
    return remember(base64String) {
        try {
            val bytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) { null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechDashboardScreen(
    onNavigateToDetail: (Int) -> Unit = {},
    onLogout: () -> Unit = {}, // <-- Agregamos este parámetro para conectar la salida
    viewModel: TechnicianViewModel = hiltViewModel()
) {
    val tickets by viewModel.tickets.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) } // Controla la alerta flotante

    LaunchedEffect(Unit) {
        viewModel.cargarTickets()
    }

    // --- DIÁLOGO DE CONFIRMACIÓN ---
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas salir del panel técnico?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, cerrar")
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
                title = { Text("Mesa de Soporte Técnico") },
                actions = {
                    IconButton(onClick = { mostrarDialogo = true }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                if (tickets.isEmpty()) {
                    item { Text("No hay reportes de falla en el sistema.") }
                }
                items(tickets) { ticket ->
                    CardTicketTecnico(ticket = ticket, onEstadoCambiado = { nuevoEstado ->
                        viewModel.actualizarEstado(ticket.id, nuevoEstado)
                    })
                }
            }
        }
    }
}

@Composable
fun CardTicketTecnico(
    ticket: TicketResponseDto,
    onEstadoCambiado: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Reporte #${ticket.id}", style = MaterialTheme.typography.titleMedium)

                val (txt, color) = when (ticket.status) {
                    "recibido" -> "Recibido" to Color(0xFF2196F3)
                    "en_proceso" -> "En Proceso" to Color(0xFFFF9800)
                    "resuelto" -> "Solucionado" to Color(0xFF4CAF50)
                    else -> "No Solucionable" to Color(0xFFF44336)
                }
                Text(txt, color = color, style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text("Reportado por: ${ticket.reporter}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(ticket.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(12.dp))

            val bitmap = rememberBase64Decoder(ticket.image)
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Foto de la falla",
                    modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            when (ticket.status) {
                "recibido" -> {
                    Button(
                        onClick = { onEstadoCambiado("en_proceso") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("Atender Falla (En Proceso)")
                    }
                }
                "en_proceso" -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onEstadoCambiado("resuelto") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Solucionado")
                        }
                        Button(
                            onClick = { onEstadoCambiado("no_completado") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) {
                            Text("No se pudo")
                        }
                    }
                }
                else -> {
                    Text(
                        text = "Este reporte ya ha sido cerrado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}