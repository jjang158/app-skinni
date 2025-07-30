package com.skinny.skinnyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.skinny.skinnyapp.navigation.SkinnyNavGraph
import com.skinny.skinnyapp.ui.theme.SkinnyappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ★★★ 테마 설정 (XML 테마명과 일치 확인) ★★★
        setTheme(R.style.Theme_SkinnyApplication)

        // ★★★ 모든 Android 기기 대응 WindowInsets 설정 ★★★
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()

        setContent {
            SkinnyappTheme {
                // ★★★ 범용 적응형 컨텐츠 ★★★
                UniversalAdaptiveContent()
            }
        }
    }
}

/**
 * ★★★ 모든 Android 기기에 대응하는 범용 적응형 컨텐츠 ★★★
 */
@Composable
private fun UniversalAdaptiveContent() {
    // ★★★ 모든 시스템 UI 요소 통합 처리 ★★★
    val universalInsets = WindowInsets.systemBars
        .union(WindowInsets.displayCutout)
        .union(WindowInsets.ime)
        .union(WindowInsets.captionBar)
        .union(WindowInsets.mandatorySystemGestures)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(universalInsets)
    ) {
        SkinnyAppContent()
    }
}

@Composable
private fun SkinnyAppContent() {
    val navController = rememberNavController()
    SkinnyNavGraph(navController = navController)
}
