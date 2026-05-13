package com.example.techguardian2.data.repository

import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.AssetEntity
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.TicketDto // Importante agregar esto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val apiService: ApiService
) {
    val allAssets: Flow<List<AssetEntity>> = assetDao.getAllAssets()

    suspend fun refreshAssets(token: String) {
        try {
            val remoteAssets = apiService.getRemoteAssets("Bearer $token")
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

    // --- NUEVA FUNCIÓN PARA ENVIAR TICKETS ---
    suspend fun sendMaintenanceTicket(token: String, assetId: Int, description: String, techId: Int): Boolean {
        return try {
            val ticket = TicketDto(asset_id = assetId, description = description, technician_id = techId)
            val response = apiService.sendMaintenanceTicket("Bearer $token", ticket)
            response.success // Devuelve true si el servidor dice que todo salió bien
        } catch (e: Exception) {
            e.printStackTrace()
            false // Si no hay internet o el puerto 5001 está apagado, devuelve false
        }
    }
}