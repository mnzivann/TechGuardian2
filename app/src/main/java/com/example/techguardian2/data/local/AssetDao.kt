package com.example.techguardian2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAssets(assets: List<AssetEntity>): List<Long>

    @Query("SELECT * FROM assets ORDER BY id DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Int): AssetEntity?

    // Retornamos Int o Long para evitar errores de firma JVM en KSP con Kotlin 2.x
    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun deleteAssetById(id: Int): Int
}