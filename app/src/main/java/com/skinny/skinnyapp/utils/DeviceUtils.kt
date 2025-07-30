package com.skinny.skinnyapp.utils

import android.content.Context
import android.content.res.Configuration
import android.util.DisplayMetrics
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ★★★ 디바이스 정보 및 화면 크기 감지 유틸리티 ★★★
 */
data class DeviceInfo(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val screenWidthPx: Int,
    val screenHeightPx: Int,
    val density: Float,
    val isTablet: Boolean,
    val isLandscape: Boolean,
    val screenCategory: ScreenCategory
)

enum class ScreenCategory {
    COMPACT,    // < 600dp width
    MEDIUM,     // 600dp - 840dp width
    EXPANDED    // > 840dp width
}

@Composable
fun rememberDeviceInfo(): DeviceInfo {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    return remember(configuration) {
        val displayMetrics = context.resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val screenHeightPx = displayMetrics.heightPixels
        val screenWidthDp = (screenWidthPx / displayMetrics.density).dp
        val screenHeightDp = (screenHeightPx / displayMetrics.density).dp

        val isTablet = screenWidthDp >= 600.dp
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val screenCategory = when {
            screenWidthDp < 600.dp -> ScreenCategory.COMPACT
            screenWidthDp < 840.dp -> ScreenCategory.MEDIUM
            else -> ScreenCategory.EXPANDED
        }

        DeviceInfo(
            screenWidth = screenWidthDp,
            screenHeight = screenHeightDp,
            screenWidthPx = screenWidthPx,
            screenHeightPx = screenHeightPx,
            density = displayMetrics.density,
            isTablet = isTablet,
            isLandscape = isLandscape,
            screenCategory = screenCategory
        )
    }
}
