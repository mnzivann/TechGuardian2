package com.example.techguardian2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tickets")
data class TicketEntity(
    @PrimaryKey val id: Int,
    val description: String,
    val image: String,
    val status: String,
    val reporter: String
)

