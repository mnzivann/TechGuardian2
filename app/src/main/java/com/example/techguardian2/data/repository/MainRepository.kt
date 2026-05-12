package com.example.techguardian2.data.repository

import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.AssetEntity
import com.example.techguardian2.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val apiService: ApiService
) {
    // 1. Obtener los activos locales (se actualiza automáticamente gracias a Flow)
    val allAssets: Flow<List<AssetEntity>> = assetDao.getAllAssets()

    // 2. Función para sincronizar con el servidor
    suspend fun refreshAssets(token: String) {
        try {
            // Intentamos bajar los datos del servidor (puerto 5001)
            val remoteAssets = apiService.getRemoteAssets("Bearer $token")

            // Convertimos los DTOs de la API a Entidades de Room
            val entities = remoteAssets.map { dto ->
                AssetEntity(
                    id = dto.id,
                    name = dto.name,
                    type = dto.status, // O el mapeo que prefieras
                    serialNumber = dto.serial_number,
                    lastMaintenance = "Sincronizado hoy"
                )
            }

            // Guardamos todo en la base de datos local (Room)
            assetDao.insertAllAssets(entities)

        } catch (e: Exception) {
            // Si no hay internet o el servidor falla, no hacemos nada.
            // La UI seguirá mostrando los datos guardados en Room.
            e.printStackTrace()
        }
    }
}