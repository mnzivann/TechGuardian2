package com.example.techguardian2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.techguardian2.data.security.TokenManager
import com.example.techguardian2.ui.NavGraph
import com.example.techguardian2.ui.theme.TechGuardianTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificamos si hay un usuario guardado antes de dibujar la UI
        val token = runBlocking { tokenManager.token.first() }

        val startDest = if (token.isNullOrEmpty()) {
            "login" // Si no hay token, exige Login
        } else {
            // El token tiene formato "oficina1|usuario", extraemos el rol
            val role = token.substringAfter("|")
            when (role) {
                "admin" -> "admin_panel"
                "tecnico" -> "tecnico_panel"
                else -> "oficina_panel"
            }
        }

        setContent {
            TechGuardianTheme {
                NavGraph(startDestination = startDest)
            }
        }
    }
}