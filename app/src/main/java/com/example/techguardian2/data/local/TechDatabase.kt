package com.example.techguardian2.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AssetEntity::class], version = 1, exportSchema = false)
abstract class TechDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
}