package com.example.techguardian2.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val type: String, // Ejemplo: Laptop, Consola, Smartphone
    val serialNumber: String,
    val lastMaintenance: String
)