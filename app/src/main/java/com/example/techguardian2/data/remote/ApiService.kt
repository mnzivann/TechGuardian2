package com.example.techguardian2.data.remote

import retrofit2.http.*

interface ApiService {

    @Headers("ngrok-skip-browser-warning: true")
    @POST("login")
    suspend fun loginUsuario(@Body request: LoginRequestDto): LoginResponseDto

    // --- CRUD DE USUARIOS ---
    @Headers("ngrok-skip-browser-warning: true")
    @GET("users")
    suspend fun getUsers(@Header("Authorization") token: String): List<UserDto>

    @Headers("ngrok-skip-browser-warning: true")
    @POST("users")
    suspend fun createUser(@Header("Authorization") token: String, @Body userRequest: CreateUserDto): GenericResponseDto

    @Headers("ngrok-skip-browser-warning: true")
    @PUT("users/{id}")
    suspend fun updateUser(@Header("Authorization") token: String, @Path("id") id: Int, @Body userRequest: CreateUserDto): GenericResponseDto

    @Headers("ngrok-skip-browser-warning: true")
    @DELETE("users/{id}")
    suspend fun deleteUser(@Header("Authorization") token: String, @Path("id") id: Int): GenericResponseDto

    // --- NUEVAS RUTAS PARA REPORTES (TICKETS) ---
    @Headers("ngrok-skip-browser-warning: true")
    @POST("tickets")
    suspend fun enviarTicket(@Header("Authorization") token: String, @Body ticketRequest: TicketRequestDto): GenericResponseDto

    @Headers("ngrok-skip-browser-warning: true")
    @GET("tickets")
    suspend fun obtenerTickets(@Header("Authorization") token: String): List<TicketResponseDto>
}

// DTOs necesarios
data class LoginRequestDto(val username: String, val password: String)
data class LoginResponseDto(val success: Boolean, val role: String, val token: String)
data class UserDto(val id: Int, val fullName: String, val username: String, val role: String)
data class CreateUserDto(val fullName: String, val username: String, val password: String, val role: String)
data class GenericResponseDto(val success: Boolean, val message: String)
data class AssetDto(val id: Int, val name: String, val status: String, val serial_number: String)

// DTOs de Reportes
data class TicketRequestDto(val description: String, val image: String)
data class TicketResponseDto(val id: Int, val description: String, val image: String, val status: String)