package com.example.techguardian2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.techguardian2.data.remote.UserDto
import com.example.techguardian2.ui.viewmodels.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val usersList by viewModel.usersList.collectAsState()

    // Control de pantallas internas
    var showForm by remember { mutableStateOf(false) }
    var editingUser by remember { mutableStateOf<UserDto?>(null) }

    // Variables del formulario
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("tecnico") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showForm) "Datos de Usuario" else "Gestión de Personal") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showForm) showForm = false else onNavigateBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            if (!showForm) {
                FloatingActionButton(onClick = {
                    editingUser = null
                    fullName = ""; username = ""; password = ""; selectedRole = "tecnico"
                    showForm = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Nuevo")
                }
            }
        }
    ) { padding ->
        if (showForm) {
            // --- VISTA DEL FORMULARIO ---
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Text(if (editingUser == null) "Registrar Nuevo Usuario" else "Modificar Usuario", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Nombre Completo") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("Usuario de acceso") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Contraseña provisional") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))
                Text("Rol del usuario:")
                Row {
                    RadioButton(selected = selectedRole == "tecnico", onClick = { selectedRole = "tecnico" })
                    Text("Técnico", modifier = Modifier.padding(top = 12.dp))
                    Spacer(modifier = Modifier.width(20.dp))
                    RadioButton(selected = selectedRole == "usuario", onClick = { selectedRole = "usuario" })
                    Text("Oficina", modifier = Modifier.padding(top = 12.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.saveUser(editingUser?.id, fullName, username, password, selectedRole) {
                            showForm = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (editingUser == null) "Crear Usuario" else "Guardar Cambios")
                }
            }
        } else {
            // --- VISTA DE LA LISTA ---
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(usersList) { user ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(user.fullName, style = MaterialTheme.typography.titleMedium)
                                Text("@${user.username} - ${user.role.uppercase()}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                            }
                            Row {
                                IconButton(onClick = {
                                    editingUser = user
                                    fullName = user.fullName; username = user.username; password = "" // Deja en blanco para no mostrarla
                                    selectedRole = user.role
                                    showForm = true
                                }) { Icon(Icons.Default.Edit, contentDescription = "Editar") }

                                IconButton(onClick = { viewModel.deleteUser(user.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}