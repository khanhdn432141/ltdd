package com.example.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.rememberNavController
// 1. Import Theme của App
import com.example.flashcardapp.ui.theme.FlashcardAppTheme
// 2. Import ViewModel và Factory để quản lý dữ liệu
import com.example.flashcardapp.viewmodel.CardViewModel
import com.example.flashcardapp.viewmodel.CardViewModelFactory

class MainActivity : ComponentActivity() {

    /**
     * Khởi tạo CardViewModel thông qua Factory.
     * Đây là "bộ não" điều khiển dữ liệu cho toàn bộ các màn hình.
     */
    private val viewModel: CardViewModel by viewModels {
        CardViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Tối ưu hiển thị tràn viền
        enableEdgeToEdge()

        setContent {
            FlashcardAppTheme {
                // Khởi tạo bộ điều khiển điều hướng
                val navController = rememberNavController()

                /**
                 * Gọi hàm NavGraph.
                 * QUAN TRỌNG: File NavGraph.kt của bạn phải nằm cùng package com.example.flashcardapp
                 * và phải chứa hàm @Composable fun NavGraph(...)
                 */
                NavGraph(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}