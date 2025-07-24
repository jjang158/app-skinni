package com.skinny.skinnyapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CustomColorScheme = lightColorScheme(
    primary = PrimaryGreen,          // 버튼, 주요 액션 색상
    onPrimary = ButtonTextWhite,     // 버튼 텍스트 하얀색

    secondary = PrimaryGreen,        // 필요시 동일한 그린 계열 사용
    onSecondary = TextDarkGray,

    background = BackgroundBeige,    // 앱 전체 배경 베이지
    onBackground = TextDarkGray,    // 배경 위 텍스트 색상

    surface = BackgroundBeige,       // 카드, 표면 배경 등
    onSurface = TextDarkGray,        // 표면 위 텍스트

    error = Color(0xFFD87A77),       // 에러 색상 (기존 유지)
    onError = BackgroundBeige        // 에러 위 텍스트는 베이지
)

@Composable
fun SkinnyappTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CustomColorScheme,
        typography = Typography(),  // 필요시 커스텀 타이포그래피 추가 가능
        content = content
    )
}
