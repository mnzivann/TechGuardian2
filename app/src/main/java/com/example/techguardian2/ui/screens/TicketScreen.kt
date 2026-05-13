package com.example.techguardian2.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.techguardian2.ui.viewmodels.TicketViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    onNavigateBack: () -> Unit,
    viewModel: TicketViewModel = hiltViewModel()
) {
    var assetId by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Herramienta para mostrar mensajitos flotantes en Android
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Ticket") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Reportar falla de equipo", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = assetId,
                onValueChange = { assetId = it },
                label = { Text("ID del Equipo (Ej: 101)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción del problema") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 5
            )

            Button(
                onClick = {
                    // Llamamos al ViewModel para que intente enviarlo a la API
                    viewModel.submitTicket(assetId, description) { success ->
                        if (success) {
                            Toast.makeText(context, "¡Ticket enviado al servidor!", Toast.LENGTH_SHORT).show()
                            onNavigateBack() // Regresamos a la lista
                        } else {
                            Toast.makeText(context, "Modo Offline: No se pudo conectar al servidor 5001", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Enviar Reporte")
            }
        }
    }
}