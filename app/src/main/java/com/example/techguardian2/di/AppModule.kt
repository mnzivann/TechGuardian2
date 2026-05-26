package com.example.techguardian2.di

import android.content.Context
import androidx.room.Room
import com.example.techguardian2.data.local.AssetDao
import com.example.techguardian2.data.local.TechDatabase
import com.example.techguardian2.data.local.TicketDao
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

    // 1. Base de datos (Con migración destructiva para evitar crasheos en desarrollo)
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TechDatabase {
        return Room.databaseBuilder(
            context,
            TechDatabase::class.java,
            "tech_guardian_db"
        )
            .fallbackToDestructiveMigration() // <-- Evita que la app muera al cambiar de versión
            .build()
    }

    // 2. DAO de Equipos (Inventario)
    @Provides
    fun provideAssetDao(db: TechDatabase): AssetDao {
        return db.assetDao()
    }

    // 3. DAO de Tickets (Modo Offline)
    @Provides
    fun provideTicketDao(db: TechDatabase): TicketDao {
        return db.ticketDao()
    }

    // 4. Servicio de API con Ngrok y SSL relajado
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val baseUrl = "https://oil-doorstep-vitamins.ngrok-free.dev/api/"

        // Creamos un administrador de confianza que acepta TODOS los certificados
        val trustAllCerts = arrayOf<javax.net.ssl.TrustManager>(
            object : javax.net.ssl.X509TrustManager {
                override fun checkClientTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<java.security.cert.X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
            }
        )

        // Instalamos este administrador en un contexto SSL
        val sslContext = javax.net.ssl.SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Construimos el cliente HTTP
        val okHttpClient = okhttp3.OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as javax.net.ssl.X509TrustManager)
            .hostnameVerifier { _, _ -> true }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    // 5. Repositorio principal (Ahora inyecta ambos DAOs)
    @Provides
    @Singleton
    fun provideMainRepository(
        assetDao: AssetDao,
        ticketDao: TicketDao,
        apiService: ApiService
    ): MainRepository {
        return MainRepository(assetDao, ticketDao, apiService)
    }
}