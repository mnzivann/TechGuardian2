package com.example.techguardian2.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.io.File

// Función para generar un archivo temporal seguro para la cámara
fun Context.createImageUri(): Uri {
    val imageFile = File(cacheDir, "ticket_img_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }

    // Variables para manejar la imagen
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador nativo de la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempUri // Si el usuario aceptó la foto, la guardamos para mostrarla
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Falla") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Detalles del equipo dañado", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe el problema") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para abrir la cámara
            Button(
                onClick = {
                    val newUri = context.createImageUri()
                    tempUri = newUri
                    cameraLauncher.launch(newUri)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (imageUri == null) "Tomar Fotografía del Equipo" else "Volver a tomar foto")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Previsualización de la foto usando Coil
            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Evidencia fotográfica",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón final (La conexión a Java la haremos en el siguiente paso)
            Button(
                onClick = { /* Aquí enviaremos el ticket y la foto al servidor Java */ },
                modifier = Modifier.fillMaxWidth(),
                enabled = description.isNotBlank() && imageUri != null
            ) {
                Text("Enviar Reporte a Técnicos")
            }
        }
    }
}