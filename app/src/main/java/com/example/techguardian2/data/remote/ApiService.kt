package com.example.techguardian2.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header

interface ApiService {

    // Obtener todos los activos desde el servidor
    @GET("assets")
    suspend fun getRemoteAssets(
        @Header("Authorization") token: String
    ): List<AssetDto>

    // Enviar un nuevo ticket de mantenimiento
    @POST("maintenance/tickets")
    suspend fun sendMaintenanceTicket(
        @Header("Authorization") token: String,
        @Body ticket: TicketDto
    ): ApiResponse
}

// Modelos de datos para el servidor (Data Transfer Objects)
data class AssetDto(
    val id: Int,
    val name: String,
    val serial_number: String,
    val status: String
)

data class TicketDto(
    val asset_id: Int,
    val description: String,
    val technician_id: Int
)

data class ApiResponse(
    val success: Boolean,
    val message: String
)