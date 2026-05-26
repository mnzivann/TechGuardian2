package com.example.techguardian2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AssetEntity::class, TicketEntity::class], version = 2, exportSchema = false)
abstract class TechDatabase : RoomDatabase() {

    abstract fun assetDao(): AssetDao

    // Aquí registramos la nueva tabla de tickets para el modo offline
    abstract fun ticketDao(): TicketDao
}