package com.example.movie.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MovieTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicPrimaryColor: Color = Color(0xFF6750A4), // Thêm tham số nhận màu tùy chỉnh
    content: @Composable () -> Unit
) {
    // Tạo bảng màu dựa trên màu người dùng chọn
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = dynamicPrimaryColor,
            onPrimary = Color.White,
            background = Color(0xFF1C1B1F),
            surface = Color(0xFF1C1B1F),
            primaryContainer = dynamicPrimaryColor.copy(alpha = 0.3f)
        )
    } else {
        lightColorScheme(
            primary = dynamicPrimaryColor,
            onPrimary = Color.White,
            background = Color(0xFFFFFBFE),
            surface = Color(0xFFFFFBFE),
            primaryContainer = dynamicPrimaryColor.copy(alpha = 0.12f)
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
