package com.example.flashcardapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.flashcardapp.viewmodel.CardViewModel
import com.google.firebase.auth.FirebaseAuth

// IMPORT ĐÍCH DANH ĐỂ TRÁNH XUNG ĐỘT ĐƯỜNG DẪN
import com.example.flashcardapp.ui.theme.AuthScreen
import com.example.flashcardapp.ui.theme.HomeScreen
import com.example.flashcardapp.ui.theme.SettingsScreen
import com.example.flashcardapp.ui.theme.DeckScreen
import com.example.flashcardapp.ui.theme.AddCardScreen
import com.example.flashcardapp.ui.theme.StudyScreen
import com.example.flashcardapp.ui.theme.StatsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: CardViewModel
) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "auth"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 1. Màn hình Đăng nhập
        composable("auth") {
            AuthScreen(viewModel, onAuthSuccess = {
                navController.navigate("home") { popUpTo("auth") { inclusive = true } }
            })
        }

        // 2. Màn hình Chính
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onDeckClick = { id, name -> navController.navigate("deck/$id/$name") },
                onSettingsClick = { navController.navigate("settings") },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("auth") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // 3. Màn hình Cài đặt
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        // 4. Màn hình Chi tiết bộ thẻ
        composable(
            route = "deck/{deckId}/{deckName}",
            arguments = listOf(
                navArgument("deckId") { type = NavType.LongType },
                navArgument("deckName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: 0L
            val deckName = backStackEntry.arguments?.getString("deckName") ?: "Bộ thẻ"

            DeckScreen(
                deckId = deckId,
                deckName = deckName,
                viewModel = viewModel,
                onStudyClick = { navController.navigate("study/$deckId") },
                onStatsClick = { navController.navigate("stats/$deckId") },
                onAddCard = { navController.navigate("add_card/$deckId") },
                onBack = { navController.popBackStack() }
            )
        }

        // 5. Thêm thẻ, Học tập và Thống kê
        composable("add_card/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: 0L
            AddCardScreen(deckId, viewModel, onBack = { navController.popBackStack() })
        }

        composable("study/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: 0L
            StudyScreen(deckId, viewModel, onBack = { navController.popBackStack() })
        }

        composable("stats/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: 0L
            StatsScreen(deckId = deckId, viewModel = viewModel, onBack = { navController.popBackStack() })
        }
    }
}