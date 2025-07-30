package com.skinny.skinnyapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skinny.skinnyapp.data.SkinResult
import kotlin.math.cos
import kotlin.math.sin

/**
 * 피부 진단 결과를 시각화하는 레이더 차트 컴포저블입니다.
 * @param modifier Modifier
 * @param data SkinResult 데이터 리스트
 * @param gridColor 차트 배경 격자 색상
 * @param dataColor 데이터 영역 색상
 * @since 2025-07-23
 */
@Composable
fun RadarChart(
    modifier: Modifier = Modifier,
    data: List<SkinResult>,
    gridColor: Color = Color.Gray.copy(alpha = 0.5f),
    dataColor: Color = MaterialTheme.colorScheme.primary
) {
    if (data.isEmpty()) return

    val textPaint = android.graphics.Paint().apply {
        color = MaterialTheme.colorScheme.onSurface.hashCode()
        textSize = 14.sp.value
        textAlign = android.graphics.Paint.Align.CENTER
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2 * 0.8f // 차트 반지름
        val angleStep = 2 * Math.PI / data.size // 각 축 사이의 각도

        // 1. 배경 격자(거미줄) 그리기
        (1..5).forEach { i ->
            val r = radius * i / 5
            drawCircle(color = gridColor, radius = r, style = Stroke(width = 1.dp.toPx()))
        }
        data.indices.forEach { i ->
            val angle = i * angleStep
            val x = centerX + (radius * cos(angle)).toFloat()
            val y = centerY + (radius * sin(angle)).toFloat()
            drawLine(start = Offset(centerX, centerY), end = Offset(x, y), color = gridColor)
        }

        // 2. 각 축의 라벨(텍스트) 그리기
        data.forEachIndexed { i, result ->
            val angle = i * angleStep
            val labelRadius = radius * 1.15f // 라벨을 차트 바깥쪽에 표시
            val x = centerX + (labelRadius * cos(angle)).toFloat()
            val y = centerY + (labelRadius * sin(angle)).toFloat()
            drawContext.canvas.nativeCanvas.drawText(result.label, x, y, textPaint)
        }

        // 3. 데이터 영역 그리기
        val path = Path()
        data.forEachIndexed { i, result ->
            val scoreRatio = result.score / 100f
            val angle = i * angleStep
            val x = centerX + (radius * scoreRatio * cos(angle)).toFloat()
            val y = centerY + (radius * scoreRatio * sin(angle)).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        path.close()

        // 데이터 영역 채우기
        drawPath(path = path, color = dataColor.copy(alpha = 0.4f))
        // 데이터 영역 테두리 그리기
        drawPath(path = path, color = dataColor, style = Stroke(width = 2.dp.toPx()))
    }
}