package com.example.techguardian2.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.techguardian2.ui.screens.AssetListScreen
import com.example.techguardian2.ui.screens.LoginScreen
import com.example.techguardian2.ui.screens.TicketScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("asset_list") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("asset_list") {
            AssetListScreen(
                onNavigateToTicket = {
                    navController.navigate("new_ticket")
                }
            )
        }

        composable("new_ticket") {
            TicketScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}