package com.skinny.skinnyapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val LightBeige = Color(0xFFFFF9F5)
private val CustomColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE),            // bg-primary
    onPrimary = Color.White,                // text-primary-content
    secondary = Color(0xFF03DAC6),          // bg-accent-1
    onSecondary = Color(0xFF333333),        // text-dark
    tertiary = Color(0xFFB7AFC9),           // bg-accent-2
    error = Color(0xFFD87A77),              // bg-error
    onError = LightBeige,           // ✅ 에러 대비용 텍스트 색상도 베이지
    background = LightBeige,        // ✅ 배경색
    onBackground = Color(0xFF333333),
    surface = LightBeige,
    onSurface = Color(0xFF333333)
)

@Composable
fun SkinnyappTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CustomColorScheme,
        typography = Typography(),
        content = content
    )
}
