package com.example.flashcardapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.flashcardapp.viewmodel.CardViewModel
import com.google.firebase.auth.FirebaseAuth
// Import các màn hình giao diện
import com.example.flashcardapp.ui.theme.*
import androidx.compose.runtime.remember
@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: CardViewModel
) {
    // Kiểm tra trạng thái đăng nhập để chọn màn hình khởi đầu
//    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "auth"
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    val startDestination = if (currentUser != null) "home" else "auth"
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Màn hình Đăng nhập
        // PHẢI MỞ COMMENT CHO MÀN HÌNH AUTH
        composable("auth") {
            AuthScreen(
                viewModel = viewModel,
                onAuthSuccess = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        // PHẢI MỞ COMMENT CHO MÀN HÌNH HOME
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onDeckClick = { id, name ->
                    navController.navigate("deck/$id/$name")
                },
                onSettingsClick = { navController.navigate("settings") },
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("auth") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Màn hình Settings (Thêm vào nếu chưa có)
        composable("settings") {
            SettingsScreen(onBack = { navController.popBackStack() })

        }
        // Màn hình Chi tiết bộ thẻ
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
                onAddCard = { navController.navigate("add_card/$deckId") },
                onStatsClick = { navController.navigate("stats/$deckId") },
                onBack = { navController.popBackStack() }
            )
        }

        // Màn hình Thêm thẻ mới
        composable("add_card/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: 0L
            AddCardScreen(
                deckId = deckId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Màn hình Ôn tập (Study)
        composable("study/{deckId}") { backStackEntry ->
            val deckId = backStackEntry.arguments?.getString("deckId")?.toLongOrNull() ?: 0L
            StudyScreen(
                deckId = deckId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

//        // Màn hình Thống kê
//        composable("stats/{deckId}") {
//            StatsScreen(
//                viewModel = viewModel,
//                onBack = { navController.popBackStack() }
//            )
//        }
        // Màn hình Thống kê - Fix lỗi đỏ deckId
        composable(
            route = "stats/{deckId}",
            arguments = listOf(navArgument("deckId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Lấy deckId từ đường dẫn (URL) của route
            val deckId = backStackEntry.arguments?.getLong("deckId") ?: 0L

            StatsScreen(
                deckId = deckId, // Truyền deckId vào màn hình
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}