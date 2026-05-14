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

        composable("admin_panel") {
            AdminPanelScreen(onNavigateBack = { navController.popBackStack() })
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