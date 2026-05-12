package com.example.techguardian2.di

import android.content.Context
import androidx.room.Room
import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.TechDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TechDatabase {
        return Room.databaseBuilder(
            context,
            TechDatabase::class.java,
            "tech_guardian_db"
        ).build()
    }

    @Provides
    fun provideAssetDao(db: TechDatabase): AssetDao {
        return db.assetDao()
    }
}