package com.example.techguardian2.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.techguardian2.ui.viewmodels.TicketViewModel
import java.io.ByteArrayOutputStream
import java.io.File

// Generar archivo temporal para la cámara
fun Context.createImageUri(): Uri {
    val imageFile = File(cacheDir, "ticket_img_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
}

// Comprimir y convertir foto a Base64 para el backend
fun encodeImageToBase64(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
    val bytes = outputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    onNavigateBack: () -> Unit,
    viewModel: TicketViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success -> if (success) imageUri = tempUri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportar Falla") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !cargando) {
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
                label = { Text("Describe el problema de forma detallada") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                enabled = !cargando
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val newUri = context.createImageUri()
                    tempUri = newUri
                    cameraLauncher.launch(newUri)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                Text(if (imageUri == null) "Tomar Fotografía" else "Cambiar fotografía")
            }

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Evidencia",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    cargando = true
                    val fotoEnTexto = encodeImageToBase64(context, imageUri!!)

                    // Solo enviamos descripción y foto, el ViewModel pone el nombre
                    viewModel.enviarReporte(description, fotoEnTexto) { exito ->
                        cargando = false
                        if (exito) {
                            Toast.makeText(context, "Reporte enviado exitosamente", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = description.isNotBlank() && imageUri != null && !cargando
            ) {
                Text("Enviar Reporte a Técnicos")
            }
        }
    }
}