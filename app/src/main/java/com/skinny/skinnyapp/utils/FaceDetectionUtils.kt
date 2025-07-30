package com.skinny.skinnyapp.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.tasks.await
import kotlin.math.max
import kotlin.math.min

/**
 * ★★★ 2단계 크롭 알고리즘으로 개선된 FaceDetectionUtils ★★★
 */
object FaceDetectionUtils {
    private val faceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.1f)
        .build()

    private val detector = FaceDetection.getClient(faceDetectorOptions)

    /**
     * ★★★ 2단계 크롭 방식: 1차 크롭 후 재중앙정렬 ★★★
     */
    suspend fun detectAndCenterFaceWithResult(bitmap: Bitmap): FaceDetectionResult {
        return try {
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            val faces = detector.process(inputImage).await()

            if (faces.isNotEmpty()) {
                val primaryFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
                val faceBounds = primaryFace?.boundingBox

                Log.d("FaceDetection", "Face detected at: $faceBounds, Count: ${faces.size}")
                Log.d("FaceDetection", "Original image size: ${bitmap.width}x${bitmap.height}")

                val processedBitmap = if (faceBounds != null) {
                    // ★★★ 2단계 크롭 알고리즘 적용 ★★★
                    cropFaceWithTwoStageAlgorithm(bitmap, faceBounds)
                } else {
                    cropCenterPortion(bitmap)
                }

                FaceDetectionResult(
                    bitmap = processedBitmap,
                    faceDetected = true,
                    confidence = 0.95f,
                    faceCount = faces.size,
                    faceBounds = faceBounds
                )
            } else {
                Log.d("FaceDetection", "No face detected, returning center crop")
                FaceDetectionResult(
                    bitmap = cropCenterPortion(bitmap),
                    faceDetected = false,
                    confidence = 0f,
                    faceCount = 0,
                    faceBounds = null
                )
            }
        } catch (e: Exception) {
            Log.e("FaceDetection", "Face detection failed", e)
            FaceDetectionResult(
                bitmap = cropCenterPortion(bitmap),
                faceDetected = false,
                confidence = 0f,
                faceCount = 0,
                faceBounds = null
            )
        }
    }

    /**
     * ★★★ 2단계 크롭 알고리즘: 크롭 → 재중앙정렬 ★★★
     */
    private suspend fun cropFaceWithTwoStageAlgorithm(
        bitmap: Bitmap,
        faceBounds: android.graphics.Rect
    ): Bitmap {
        Log.d("FaceDetection", "=== 2단계 크롭 알고리즘 시작 ===")

        // ★★★ 1단계: 얼굴 주변 넓은 영역을 1차 크롭 ★★★
        val firstCropBitmap = performFirstStageCrop(bitmap, faceBounds)
        Log.d("FaceDetection", "1단계 크롭 완료: ${firstCropBitmap.width}x${firstCropBitmap.height}")

        // ★★★ 2단계: 1차 크롭된 이미지에서 얼굴 재감지 후 정중앙 정렬 ★★★
        val finalBitmap = performSecondStageCenterAlignment(firstCropBitmap)
        Log.d("FaceDetection", "2단계 중앙정렬 완료: ${finalBitmap.width}x${finalBitmap.height}")

        return finalBitmap
    }

    /**
     * ★★★ 1단계: 얼굴 주변 넓은 영역 크롭 ★★★
     */
    private fun performFirstStageCrop(bitmap: Bitmap, faceBounds: android.graphics.Rect): Bitmap {
        val imageWidth = bitmap.width.toFloat()
        val imageHeight = bitmap.height.toFloat()

        val faceCenterX = faceBounds.centerX().toFloat()
        val faceCenterY = faceBounds.centerY().toFloat()
        val faceWidth = faceBounds.width().toFloat()
        val faceHeight = faceBounds.height().toFloat()
        val maxFaceSize = max(faceWidth, faceHeight)

        // ★★★ 1단계에서는 충분히 넓은 영역을 크롭 (얼굴 크기의 3배) ★★★
        val firstCropSize = min(min(imageWidth, imageHeight), maxFaceSize * 3.0f)

        Log.d("FaceDetection", "1단계 크롭 사이즈: $firstCropSize (얼굴 크기의 3배)")

        // 1단계 크롭 영역 계산 (넉넉하게)
        val cropLeft = (faceCenterX - firstCropSize / 2).coerceIn(0f, imageWidth - firstCropSize)
        val cropTop = (faceCenterY - firstCropSize / 2).coerceIn(0f, imageHeight - firstCropSize)

        val finalLeft = cropLeft.toInt()
        val finalTop = cropTop.toInt()
        val finalSize = firstCropSize.toInt()

        Log.d("FaceDetection", "1단계 크롭 영역: ($finalLeft, $finalTop, $finalSize, $finalSize)")

        return Bitmap.createBitmap(bitmap, finalLeft, finalTop, finalSize, finalSize)
    }

    /**
     * ★★★ 2단계: 1차 크롭된 이미지에서 얼굴 재감지 후 정중앙 정렬 ★★★
     */
    private suspend fun performSecondStageCenterAlignment(firstCropBitmap: Bitmap): Bitmap {
        return try {
            // 1차 크롭된 이미지에서 얼굴 재감지
            val inputImage = InputImage.fromBitmap(firstCropBitmap, 0)
            val faces = detector.process(inputImage).await()

            if (faces.isNotEmpty()) {
                val face = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }
                val faceBounds = face?.boundingBox

                if (faceBounds != null) {
                    Log.d("FaceDetection", "2단계에서 얼굴 재감지 성공: $faceBounds")

                    // ★★★ 이제 얼굴이 정확히 중앙에 오도록 최종 크롭 ★★★
                    return performPreciseCenterCrop(firstCropBitmap, faceBounds)
                }
            }

            // 얼굴 재감지 실패 시 중앙 크롭
            Log.d("FaceDetection", "2단계 얼굴 재감지 실패, 중앙 크롭 적용")
            return cropCenterPortion(firstCropBitmap)

        } catch (e: Exception) {
            Log.e("FaceDetection", "2단계 처리 실패", e)
            return cropCenterPortion(firstCropBitmap)
        }
    }

    /**
     * ★★★ 정밀한 중앙 크롭: 얼굴이 정확히 중앙에 오도록 ★★★
     */
    private fun performPreciseCenterCrop(bitmap: Bitmap, faceBounds: android.graphics.Rect): Bitmap {
        val imageSize = bitmap.width // 이미 정사각형이므로 width == height
        val faceCenterX = faceBounds.centerX().toFloat()
        val faceCenterY = faceBounds.centerY().toFloat()

        Log.d("FaceDetection", "정밀 크롭 - 이미지 크기: ${imageSize}x$imageSize")
        Log.d("FaceDetection", "정밀 크롭 - 얼굴 중심: ($faceCenterX, $faceCenterY)")

        // ★★★ 최종 크롭 사이즈 결정 (이미지의 80%) ★★★
        val finalCropSize = (imageSize * 0.8f).toInt()

        // ★★★ 얼굴 중심이 크롭 영역의 정중앙에 오도록 계산 ★★★
        val cropLeft = (faceCenterX - finalCropSize / 2f).toInt()
        val cropTop = (faceCenterY - finalCropSize / 2f).toInt()

        // 경계 조정
        val adjustedLeft = cropLeft.coerceIn(0, imageSize - finalCropSize)
        val adjustedTop = cropTop.coerceIn(0, imageSize - finalCropSize)

        Log.d("FaceDetection", "최종 크롭 영역: ($adjustedLeft, $adjustedTop, $finalCropSize, $finalCropSize)")

        // ★★★ 최종 결과에서 얼굴이 중앙에 위치하는지 검증 ★★★
        val cropCenterX = adjustedLeft + finalCropSize / 2f
        val cropCenterY = adjustedTop + finalCropSize / 2f
        val distanceFromCenter = kotlin.math.sqrt(
            (faceCenterX - cropCenterX) * (faceCenterX - cropCenterX) +
                    (faceCenterY - cropCenterY) * (faceCenterY - cropCenterY)
        )

        Log.d("FaceDetection", "얼굴 중심과 크롭 중심 거리: $distanceFromCenter (작을수록 정확)")

        return Bitmap.createBitmap(bitmap, adjustedLeft, adjustedTop, finalCropSize, finalCropSize)
    }

    /**
     * ★★★ 기존 호환성을 위한 간단한 버전 ★★★
     */
    suspend fun detectAndCenterFace(bitmap: Bitmap): Bitmap {
        return detectAndCenterFaceWithResult(bitmap).bitmap
    }

    /**
     * ★★★ 중심 크롭 함수 (백업용) ★★★
     */
    private fun cropCenterPortion(bitmap: Bitmap): Bitmap {
        val size = min(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        Log.d("FaceDetection", "백업 중심 크롭: ($x, $y, $size, $size)")

        return Bitmap.createBitmap(bitmap, x, y, size, size)
    }

    /**
     * ★★★ 디버깅용 얼굴 위치 분석 ★★★
     */
    fun analyzeFacePositionInCrop(bitmap: Bitmap, faceBounds: android.graphics.Rect): String {
        val imageCenter = bitmap.width / 2f to bitmap.height / 2f
        val faceCenter = faceBounds.centerX().toFloat() to faceBounds.centerY().toFloat()

        val distanceFromCenter = kotlin.math.sqrt(
            (faceCenter.first - imageCenter.first) * (faceCenter.first - imageCenter.first) +
                    (faceCenter.second - imageCenter.second) * (faceCenter.second - imageCenter.second)
        )

        val accuracy = if (distanceFromCenter < bitmap.width * 0.05) {
            "매우 정확"
        } else if (distanceFromCenter < bitmap.width * 0.1) {
            "정확"
        } else if (distanceFromCenter < bitmap.width * 0.15) {
            "약간 어긋남"
        } else {
            "크게 어긋남"
        }

        return "중앙정렬 정확도: $accuracy (거리: ${distanceFromCenter.toInt()}px)"
    }

    /**
     * 리소스 정리
     */
    fun cleanup() {
        detector.close()
    }
}

/**
 * ★★★ 얼굴 감지 결과 데이터 클래스 ★★★
 */
data class FaceDetectionResult(
    val bitmap: Bitmap,
    val faceDetected: Boolean,
    val confidence: Float,
    val faceCount: Int,
    val faceBounds: android.graphics.Rect? = null
)
