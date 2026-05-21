package com.example.techguardian2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techguardian2.ui.screens.AdminPanelScreen
import com.example.techguardian2.ui.screens.AssetListScreen
import com.example.techguardian2.ui.screens.LoginScreen
import com.example.techguardian2.ui.screens.TechDashboardScreen
import com.example.techguardian2.ui.screens.TechDashboardScreen
import com.example.techguardian2.ui.screens.TicketScreen

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "login"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // 1. PANTALLA DE LOGIN
        composable("login") {
            LoginScreen(
                onLoginSuccess = { role ->
                    // El servidor Java nos responde con un rol, y aquí decidimos a dónde mandarlo
                    when (role) {
                        "admin" -> navController.navigate("admin_panel") {
                            popUpTo("login") { inclusive = true } // Borra el login del historial
                        }
                        "tecnico" -> navController.navigate("tecnico_panel") {
                            popUpTo("login") { inclusive = true }
                        }
                        else -> navController.navigate("oficina_panel") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
            )
        }

        // 2. PANTALLA DEL ADMINISTRADOR (Gestión de Personal)
        composable("admin_panel") {
            AdminPanelScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) // Borra todo el historial al cerrar sesión
                    }
                }
            )
        }

        // 3. PANTALLA DE OFICINA (Inventario y Mis Reportes)
        composable("oficina_panel") {
            AssetListScreen(
                onNavigateToTicket = {
                    // Al tocar el botón flotante (+), abre la cámara
                    navController.navigate("ticket_screen")
                }
            )
        }

        // 4. PANTALLA PARA REPORTAR FALLA (Cámara y Formulario)
        composable("ticket_screen") {
            TicketScreen(
                onNavigateBack = {
                    // Al darle al botón de atrás o enviar con éxito, regresa a la oficina
                    navController.popBackStack()
                }
            )
        }

        // 5. PANTALLA DEL TÉCNICO (Mesa de Soporte)
        composable("tecnico_panel") {
            TechDashboardScreen()
        }

    }
}