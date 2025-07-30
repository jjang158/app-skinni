package com.skinny.skinnyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * ★★★ 모든 Android 기기에 대응하는 범용 적응형 레이아웃 ★★★
 */
@Composable
fun AdaptiveScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    UniversalAdaptiveScaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        content = content
    )
}

@Composable
fun UniversalAdaptiveScaffold(
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass? = null,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val deviceInfo = rememberUniversalDeviceInfo()
    val layoutInfo = rememberAdaptiveLayoutInfo(windowSizeClass)

    // ★★★ 모든 시스템 UI 요소에 대한 안전 영역 처리 ★★★
    val safeAreaInsets = WindowInsets.systemBars
        .union(WindowInsets.displayCutout)
        .union(WindowInsets.ime)
        .union(WindowInsets.captionBar)
        .union(WindowInsets.mandatorySystemGestures)

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(safeAreaInsets)
    ) {
        // ★★★ 상단 영역 ★★★
        if (layoutInfo.showTopBar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = layoutInfo.horizontalPadding)
            ) {
                topBar()
            }
        }

        // ★★★ 메인 컨텐츠 영역 ★★★
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // ★★★ 수정된 부분: layoutInfo.contentPadding 직접 전달 ★★★
            content(layoutInfo.contentPadding)
        }

        // ★★★ 하단 영역 ★★★
        if (layoutInfo.showBottomBar) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = layoutInfo.horizontalPadding)
            ) {
                bottomBar()
            }
        }
    }
}

/**
 * ★★★ 범용 기기 정보 감지 ★★★
 */
@Composable
fun rememberUniversalDeviceInfo(): UniversalDeviceInfo {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    return remember(configuration, density) {
        val screenWidth = configuration.screenWidthDp.dp
        val screenHeight = configuration.screenHeightDp.dp
        val densityDpi = configuration.densityDpi
        val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

        val sizeCategory = when {
            screenWidth < 360.dp -> DeviceSizeCategory.COMPACT
            screenWidth < 600.dp -> DeviceSizeCategory.MEDIUM
            screenWidth < 840.dp -> DeviceSizeCategory.EXPANDED
            else -> DeviceSizeCategory.LARGE
        }

        val formFactor = when {
            screenWidth >= 600.dp && screenHeight >= 600.dp -> FormFactor.TABLET
            screenWidth >= 840.dp -> FormFactor.DESKTOP
            screenWidth.value / screenHeight.value > 2.0f -> FormFactor.FOLDABLE_UNFOLDED
            else -> FormFactor.PHONE
        }

        UniversalDeviceInfo(
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            densityDpi = densityDpi,
            isLandscape = isLandscape,
            sizeCategory = sizeCategory,
            formFactor = formFactor
        )
    }
}

/**
 * ★★★ 수정된 적응형 레이아웃 정보 계산 ★★★
 */
@Composable
fun rememberAdaptiveLayoutInfo(windowSizeClass: WindowSizeClass?): AdaptiveLayoutInfo {
    val deviceInfo = rememberUniversalDeviceInfo()

    return remember(deviceInfo, windowSizeClass) {
        val widthSizeClass = windowSizeClass?.widthSizeClass ?: when (deviceInfo.sizeCategory) {
            DeviceSizeCategory.COMPACT -> WindowWidthSizeClass.Compact
            DeviceSizeCategory.MEDIUM -> WindowWidthSizeClass.Medium
            DeviceSizeCategory.EXPANDED -> WindowWidthSizeClass.Expanded
            DeviceSizeCategory.LARGE -> WindowWidthSizeClass.Expanded
        }

        // ★★★ 화면 크기별 패딩 설정 ★★★
        val (horizontalPadding, contentPadding) = when (widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                16.dp to PaddingValues(16.dp, 8.dp, 16.dp, 16.dp)
            }
            WindowWidthSizeClass.Medium -> {
                24.dp to PaddingValues(24.dp, 12.dp, 24.dp, 20.dp)
            }
            WindowWidthSizeClass.Expanded -> {
                32.dp to PaddingValues(32.dp, 16.dp, 32.dp, 24.dp)
            }
            else -> {
                16.dp to PaddingValues(16.dp, 8.dp, 16.dp, 16.dp)
            }
        }

        AdaptiveLayoutInfo(
            horizontalPadding = horizontalPadding,
            contentPadding = contentPadding, // ★★★ 이미 PaddingValues 타입 ★★★
            showTopBar = deviceInfo.formFactor != FormFactor.PHONE || deviceInfo.isLandscape,
            showBottomBar = true,
            widthSizeClass = widthSizeClass
        )
    }
}

/**
 * ★★★ 수정된 데이터 클래스들 ★★★
 */
data class UniversalDeviceInfo(
    val screenWidth: Dp,
    val screenHeight: Dp,
    val densityDpi: Int,
    val isLandscape: Boolean,
    val sizeCategory: DeviceSizeCategory,
    val formFactor: FormFactor
)

data class AdaptiveLayoutInfo(
    val horizontalPadding: Dp,
    val contentPadding: PaddingValues, // ★★★ 이미 PaddingValues 타입 ★★★
    val showTopBar: Boolean,
    val showBottomBar: Boolean,
    val widthSizeClass: WindowWidthSizeClass
)

enum class DeviceSizeCategory {
    COMPACT, MEDIUM, EXPANDED, LARGE
}

enum class FormFactor {
    PHONE, TABLET, DESKTOP, FOLDABLE_UNFOLDED
}
