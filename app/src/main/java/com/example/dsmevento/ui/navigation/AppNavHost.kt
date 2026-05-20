package com.example.dsmevento.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.dsmevento.ui.screens.*
import com.example.dsmevento.ui.screens.HomeScreen
import com.example.dsmevento.ui.screens.LoginScreen
import com.example.dsmevento.ui.screens.RegisterScreen
import com.example.dsmevento.util.Routes

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onGoToRegister = { navController.navigate(Routes.REGISTER) },
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onGoToLogin = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onCreateEvent = { navController.navigate(Routes.createEditEventRoute()) },
                onOpenEvent = { eventId -> navController.navigate(Routes.eventDetailRoute(eventId)) },
                onOpenHistory = { navController.navigate(Routes.HISTORY) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.CREATE_EDIT_EVENT}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: "new"
            CreateEditEventScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.EVENT_DETAIL}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Routes.createEditEventRoute(eventId)) },
                onOpenReviews = { navController.navigate(Routes.reviewsRoute(eventId)) }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.REVIEWS}/{eventId}",
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            ReviewsScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}