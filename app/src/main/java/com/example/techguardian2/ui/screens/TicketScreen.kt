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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.techguardian2.ui.viewmodels.TicketViewModel
import java.io.ByteArrayOutputStream
import java.io.File

fun Context.createImageUri(): Uri {
    val imageFile = File(cacheDir, "ticket_img_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(this, "$packageName.provider", imageFile)
}

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

    // --- LISTA MAESTRA DE EQUIPOS ---
    val equiposDisponibles = listOf("Impresora HP Laser", "MacBook Air M1", "Router Cisco RT-500", "Monitor Dell 27\"")

    var selectedEquipo by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
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

            // --- SELECTOR DE EQUIPO OBLIGATORIO ---
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedEquipo.ifEmpty { "Selecciona el equipo dañado..." },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Equipo afectado (Obligatorio)") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Desplegar") },
                    enabled = !cargando
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { if (!cargando) expanded = true }
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    equiposDisponibles.forEach { equipo ->
                        DropdownMenuItem(
                            text = { Text(equipo) },
                            onClick = {
                                selectedEquipo = equipo
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- DESCRIPCIÓN ---
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe el problema de forma detallada") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                enabled = !cargando
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- FOTO OPCIONAL ---
            Button(
                onClick = {
                    val newUri = context.createImageUri()
                    tempUri = newUri
                    cameraLauncher.launch(newUri)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                Text(if (imageUri == null) "Tomar Fotografía (Opcional)" else "Cambiar fotografía")
            }

            Spacer(modifier = Modifier.height(16.dp))

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "Evidencia",
                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (cargando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }

            // --- BOTÓN DE ENVIAR (Validación Estricta) ---
            Button(
                onClick = {
                    cargando = true
                    val fotoEnTexto = if (imageUri != null) encodeImageToBase64(context, imageUri!!) else ""

                    // Unimos el equipo y la descripción para que la pestaña de Equipos lo pueda leer
                    val reporteFinal = "[$selectedEquipo] $description"

                    viewModel.enviarReporte(reporteFinal, fotoEnTexto) { exito ->
                        cargando = false
                        if (exito) {
                            Toast.makeText(context, "Reporte enviado a técnicos", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        } else {
                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                // MAGIA: El botón solo prende si seleccionó equipo Y escribió problema
                enabled = selectedEquipo.isNotBlank() && description.isNotBlank() && !cargando
            ) {
                Text("Enviar Reporte a Técnicos")
            }
        }
    }
}