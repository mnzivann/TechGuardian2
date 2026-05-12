package com.example.techguardian2.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {

    // Inserta un solo equipo
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)

    // ¡Esta es la línea que falta! Inserta una lista completa
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAssets(assets: List<AssetEntity>)

    @Query("SELECT * FROM assets ORDER BY id DESC")
    fun getAllAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id")
    suspend fun getAssetById(id: Int): AssetEntity?

    @Query("DELETE FROM assets WHERE id = :id")
    suspend fun deleteAssetById(id: Int)
}