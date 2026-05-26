package com.example.techguardian2.data.repository

import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.AssetEntity
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
    // --- 1. LÓGICA DE INVENTARIO (EQUIPOS) ---
    val allAssets: Flow<List<AssetEntity>> = assetDao.getAllAssets()

    // --- 2. LÓGICA DE TICKETS (OFFLINE-FIRST) ---
    val offlineTickets: Flow<List<TicketResponseDto>> = ticketDao.getAllTickets().map { list ->
        list.map { TicketResponseDto(it.id, it.description, it.image, it.status, it.reporter) }
    }

    suspend fun syncTickets(token: String) {
        try {
            val remote = apiService.obtenerTickets(token)
            val entities = remote.map { TicketEntity(it.id, it.description, it.image, it.status, it.reporter) }
            ticketDao.clearTickets()
            ticketDao.insertAll(entities)
        } catch (e: Exception) {
            // Modo Offline: Si entra aquí no hay internet. No crashea, solo usa los datos locales.
        }
    }

    // --- 3. LIMPIEZA TOTAL PARA EL CIERRE DE SESIÓN ---
    suspend fun limpiarSesion() {
        ticketDao.clearTickets()
    }
}