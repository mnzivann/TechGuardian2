package com.example.techguardian2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.techguardian2.data.local.AssetEntity
import com.example.techguardian2.ui.viewmodels.AssetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetListScreen(
    viewModel: AssetViewModel = hiltViewModel()
) {
    val assetsList by viewModel.assets.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inventario TechGuardian") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        // ¡AQUÍ ESTÁ EL BOTÓN NUEVO!
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.addDummyAsset() }) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar Prueba")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(assetsList) { asset ->
                AssetCard(asset = asset)
            }
        }
    }
}

@Composable
fun AssetCard(asset: AssetEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = asset.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Tipo: ${asset.type}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "No. Serie: ${asset.serialNumber}", style = MaterialTheme.typography.bodySmall)
        }
    }
}