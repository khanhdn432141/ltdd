package com.example.flashcardapp.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 1. Định nghĩa bảng màu Tươi & Nhẹ Nhàng theo tham khảo
private val FreshLightColorScheme = lightColorScheme(
    // Xanh Cyan sáng nhẹ - cho các nút hoặc header
    primary = Color(0xFF1ECBCF),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F7F9),

    // Tím sáng - tạo điểm nhấn hiện đại
    secondary = Color(0xFFAA7FF9),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF1EAFF),

    // Nền trắng tinh khôi, cực kỳ tươi sáng
    background = Color(0xFFFFFFFF),
    surface = Color.White,
    onBackground = Color(0xFF1D2939),
    onSurface = Color(0xFF1D2939),

    // Màu cho các ô nhập liệu hoặc viền thẻ
    surfaceVariant = Color(0xFFF2F4F7),
    onSurfaceVariant = Color(0xFF667085),

    outline = Color(0xFFD0D5DD)
)

@Composable
fun FlashcardAppTheme(
    darkTheme: Boolean = false, // Luôn tắt chế độ tối
    dynamicColor: Boolean = false, // Tắt màu hệ thống để giữ đúng màu Cyan-Purple
    content: @Composable () -> Unit
) {
    // Sử dụng bảng màu tươi nhẹ vừa định nghĩa
    val colorScheme = FreshLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Đặt màu thanh trạng thái tiệp màu với app
            window.statusBarColor = colorScheme.primary.toArgb()

            // Ép icon thanh trạng thái luôn màu tối (đen) để nổi bật trên nền sáng
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Đảm bảo Typography.kt của bạn đã được cấu hình
        content = content
    )
}
