package com.example.techguardian2.data.repository

import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.AssetEntity
import com.example.techguardian2.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val apiService: ApiService
) {
    // Variable que observa la base de datos local
    val allAssets: Flow<List<AssetEntity>> = assetDao.getAllAssets()

    // Función para descargar los equipos de Java y guardarlos en el celular
    suspend fun refreshAssets(token: String) {
        try {
            val remoteAssets = apiService.getRemoteAssets(token)

            val entities = remoteAssets.map { dto ->
                AssetEntity(
                    id = dto.id,
                    name = dto.name,
                    type = dto.status,
                    serialNumber = dto.serial_number,
                    lastMaintenance = "Sincronizado hoy"
                )
            }
            assetDao.insertAllAssets(entities)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}