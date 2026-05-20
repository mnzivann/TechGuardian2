package com.example.techguardian2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techguardian2.ui.screens.*

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    val destination = when (role) {
                        "admin" -> "admin_panel"
                        "tecnico" -> "tech_dashboard"
                        else -> "user_form"
                    }
                    navController.navigate(destination) {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        // NUEVO: Rutas actualizadas para el AdminPanelScreen
        composable("admin_panel") {
            AdminPanelScreen(
                onLogout = {
                    // Navega a login y limpia el historial de navegación por seguridad
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("tech_dashboard") {
            TechDashboardScreen(onNavigateToDetail = { /* Próxima fase */ })
        }

        composable("user_form") {
            AssetListScreen(onNavigateToTicket = { navController.navigate("new_ticket") })
        }

        composable("new_ticket") {
            TicketScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}