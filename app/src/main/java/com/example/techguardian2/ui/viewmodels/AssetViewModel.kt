package com.example.techguardian2.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.AssetEntity
import com.example.techguardian2.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AssetViewModel @Inject constructor(
    private val repository: MainRepository,
    private val assetDao: AssetDao // Inyectamos el DAO para la prueba rápida
) : ViewModel() {

    private val _assets = MutableStateFlow<List<AssetEntity>>(emptyList())
    val assets: StateFlow<List<AssetEntity>> = _assets.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allAssets.collect { lista ->
                _assets.value = lista
            }
        }
    }

    // Función para crear un equipo de prueba
    fun addDummyAsset() {
        viewModelScope.launch {
            val randomId = Random.nextInt(1000, 9999)
            val newAsset = AssetEntity(
                name = "MacBook Air M1 (Prueba)",
                type = "Laptop",
                serialNumber = "MAC-$randomId",
                lastMaintenance = "Hoy"
            )
            assetDao.insertAsset(newAsset)
        }
    }
}