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
import coil.compose.rememberAsyncImagePainter
import java.io.ByteArrayOutputStream
import java.io.File

// 1. Generar archivo temporal para la cámara
fun Context.createImageUri(): Uri {
    val imageFile = File(cacheDir, "ticket_img_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
}

// 2. Comprimir y convertir foto a Base64 para el backend
fun encodeImageToBase64(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
    val bytes = outputStream.toByteArray()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketScreen(
    onNavigateBack: () -> Unit
    // viewModel: TicketViewModel = hiltViewModel() -> Descomentar al integrar con Retrofit
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempUri
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

            Button(
                onClick = {
                    val fotoEnTexto = encodeImageToBase64(context, imageUri!!)

                    // TODO: Reemplazar por la llamada a tu API Retrofit
                    // viewModel.enviarTicket(description, fotoEnTexto)

                    Toast.makeText(context, "Preparando envío a Java...", Toast.LENGTH_SHORT).show()
                    println("¡Foto comprimida y lista para volar a Java!")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = description.isNotBlank() && imageUri != null
            ) {
                Text("Enviar Reporte a Técnicos")
            }
        }
    }
}