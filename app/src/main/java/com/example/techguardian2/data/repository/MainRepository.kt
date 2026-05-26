package com.example.techguardian2.data.repository

import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.TicketDao
import com.example.techguardian2.data.local.TicketEntity
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.remote.TicketResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val assetDao: AssetDao,
    private val ticketDao: TicketDao,
    private val apiService: ApiService
) {
    // 1. Exponemos los datos LOCALES (Offline) listos para la pantalla
    val offlineTickets: Flow<List<TicketResponseDto>> = ticketDao.getAllTickets().map { list ->
        list.map { TicketResponseDto(it.id, it.description, it.image, it.status, it.reporter) }
    }

    // 2. Función para descargar y guardar en el teléfono
    suspend fun syncTickets(token: String) {
        try {
            val remote = apiService.obtenerTickets(token)
            val entities = remote.map { TicketEntity(it.id, it.description, it.image, it.status, it.reporter) }
            ticketDao.clearTickets()
            ticketDao.insertAll(entities)
        } catch (e: Exception) {
            // Modo Offline: Si entra aquí no hay internet. No crashea, solo usa Room.
        }
    }
}