package com.example.techguardian2.di

import android.content.Context
import androidx.room.Room
import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.TechDatabase
import com.example.techguardian2.data.remote.ApiService
import com.example.techguardian2.data.repository.MainRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. Base de datos
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TechDatabase {
        return Room.databaseBuilder(
            context,
            TechDatabase::class.java,
            "tech_guardian_db"
        ).build()
    }

    // 2. DAO (Aquí es donde marcaba el error porque estaba fuera de estas llaves)
    @Provides
    fun provideAssetDao(db: TechDatabase): AssetDao {
        return db.assetDao()
    }

    // 3. Conexión al servidor (Retrofit)
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val baseUrl = "http://10.0.2.2:5001/api/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // 4. Repositorio principal
    @Provides
    @Singleton
    fun provideMainRepository(
        assetDao: AssetDao,
        apiService: ApiService
    ): MainRepository {
        return MainRepository(assetDao, apiService)
    }
}