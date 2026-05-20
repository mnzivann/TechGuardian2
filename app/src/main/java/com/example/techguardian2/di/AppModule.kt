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

    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val baseUrl = "https://oil-doorstep-vitamins.ngrok-free.dev/api/"

        // 1. Creamos un administrador de confianza que acepta TODOS los certificados
        val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(
            object : javax.net.ssl.X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            }
        )

        // 2. Instalamos este administrador en un contexto SSL
        val sslContext = javax.net.ssl.SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // 3. Construimos un cliente HTTP que use nuestro contexto relajado
        val okHttpClient = okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as javax.net.ssl.X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        // 4. Se lo pasamos a Retrofit
        return retrofit2.Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient) // <--- Aquí le decimos que use el cliente sin restricciones
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
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