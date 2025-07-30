package com.skinny.skinnyapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skinny.skinnyapp.utils.DeviceInfo
import com.skinny.skinnyapp.utils.ScreenCategory
import com.skinny.skinnyapp.utils.rememberDeviceInfo

/**
 * ★★★ 기존 호환성 유지하면서 범용 기능 추가된 적응형 버튼 ★★★
 */
@Composable
fun AdaptiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    // ★★★ 기존 DeviceInfo 시스템과 새로운 범용 시스템 병행 사용 ★★★
    val deviceInfo = rememberDeviceInfo()
    val universalDeviceInfo = rememberUniversalDeviceInfo()

    // ★★★ 범용 시스템 우선, 기존 시스템으로 폴백 ★★★
    val buttonHeight = when (universalDeviceInfo.sizeCategory) {
        DeviceSizeCategory.COMPACT -> when {
            universalDeviceInfo.densityDpi >= 480 -> 56.dp
            universalDeviceInfo.densityDpi >= 320 -> 52.dp
            else -> when (deviceInfo.screenCategory) {
                ScreenCategory.COMPACT -> if (deviceInfo.screenHeight < 700.dp) 48.dp else 56.dp
                else -> 48.dp
            }
        }
        DeviceSizeCategory.MEDIUM -> 60.dp
        DeviceSizeCategory.EXPANDED -> 64.dp
        DeviceSizeCategory.LARGE -> 68.dp
    }

    val horizontalPadding = when (universalDeviceInfo.sizeCategory) {
        DeviceSizeCategory.COMPACT -> 16.dp
        DeviceSizeCategory.MEDIUM -> 24.dp
        DeviceSizeCategory.EXPANDED -> 32.dp
        DeviceSizeCategory.LARGE -> 40.dp
    }

    val textStyle = when (universalDeviceInfo.sizeCategory) {
        DeviceSizeCategory.COMPACT -> MaterialTheme.typography.bodyLarge
        DeviceSizeCategory.MEDIUM -> MaterialTheme.typography.titleMedium
        DeviceSizeCategory.EXPANDED -> MaterialTheme.typography.titleLarge
        DeviceSizeCategory.LARGE -> MaterialTheme.typography.headlineSmall
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .height(buttonHeight),
        contentPadding = PaddingValues(
            horizontal = when (universalDeviceInfo.formFactor) {
                FormFactor.PHONE -> 24.dp
                FormFactor.TABLET -> 32.dp
                FormFactor.DESKTOP -> 40.dp
                FormFactor.FOLDABLE_UNFOLDED -> 28.dp
            },
            vertical = 12.dp
        )
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

/**
 * ★★★ 모든 기기에 대응하는 범용 적응형 버튼 (새로운 함수) ★★★
 */
@Composable
fun UniversalAdaptiveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    val deviceInfo = rememberUniversalDeviceInfo()

    // ★★★ 기기별 동적 크기 계산 ★★★
    val buttonHeight = when (deviceInfo.sizeCategory) {
        DeviceSizeCategory.COMPACT -> when {
            deviceInfo.densityDpi >= 480 -> 56.dp
            deviceInfo.densityDpi >= 320 -> 52.dp
            else -> 48.dp
        }
        DeviceSizeCategory.MEDIUM -> 60.dp
        DeviceSizeCategory.EXPANDED -> 64.dp
        DeviceSizeCategory.LARGE -> 68.dp
    }

    val textStyle = when (deviceInfo.sizeCategory) {
        DeviceSizeCategory.COMPACT -> MaterialTheme.typography.bodyLarge
        DeviceSizeCategory.MEDIUM -> MaterialTheme.typography.titleMedium
        DeviceSizeCategory.EXPANDED -> MaterialTheme.typography.titleLarge
        DeviceSizeCategory.LARGE -> MaterialTheme.typography.headlineSmall
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight),
        contentPadding = PaddingValues(
            horizontal = when (deviceInfo.formFactor) {
                FormFactor.PHONE -> 16.dp
                FormFactor.TABLET -> 24.dp
                FormFactor.DESKTOP -> 32.dp
                FormFactor.FOLDABLE_UNFOLDED -> 20.dp
            },
            vertical = 12.dp
        )
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

/**
 * ★★★ 기존 호환성 유지하면서 개선된 버튼 그룹 ★★★
 */
@Composable
fun AdaptiveButtonGroup(
    modifier: Modifier = Modifier,
    buttons: List<@Composable () -> Unit>
) {
    // ★★★ 기존 시스템과 새로운 시스템 병행 사용 ★★★
    val deviceInfo = rememberDeviceInfo()
    val universalDeviceInfo = rememberUniversalDeviceInfo()

    // ★★★ 범용 시스템 우선 판단, 기존 시스템으로 폴백 ★★★
    val useVerticalLayout = when {
        universalDeviceInfo.sizeCategory == DeviceSizeCategory.COMPACT && !universalDeviceInfo.isLandscape -> true
        deviceInfo.screenCategory == ScreenCategory.COMPACT && !deviceInfo.isLandscape -> true
        else -> false
    }

    if (useVerticalLayout) {
        // 세로 배치 (작은 화면)
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(
                when (universalDeviceInfo.sizeCategory) {
                    DeviceSizeCategory.COMPACT -> 8.dp
                    DeviceSizeCategory.MEDIUM -> 12.dp
                    else -> 16.dp
                }
            )
        ) {
            buttons.forEach { button -> button() }
        }
    } else {
        // 가로 배치 (큰 화면 또는 가로모드)
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                when (universalDeviceInfo.sizeCategory) {
                    DeviceSizeCategory.COMPACT -> 12.dp
                    DeviceSizeCategory.MEDIUM -> 16.dp
                    else -> 20.dp
                }
            )
        ) {
            buttons.forEach { button ->
                Box(modifier = Modifier.weight(1f)) {
                    button()
                }
            }
        }
    }
}

/**
 * ★★★ 완전한 범용 버튼 그룹 (새로운 함수) ★★★
 */
@Composable
fun UniversalAdaptiveButtonGroup(
    modifier: Modifier = Modifier,
    buttons: List<@Composable () -> Unit>
) {
    val deviceInfo = rememberUniversalDeviceInfo()

    // ★★★ 화면 크기에 따른 배치 결정 ★★★
    val isCompact = deviceInfo.sizeCategory == DeviceSizeCategory.COMPACT && !deviceInfo.isLandscape

    if (isCompact) {
        // 세로 배치 (작은 화면)
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            buttons.forEach { button -> button() }
        }
    } else {
        // 가로 배치 (큰 화면)
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            buttons.forEach { button ->
                Box(modifier = Modifier.weight(1f)) {
                    button()
                }
            }
        }
    }
}
