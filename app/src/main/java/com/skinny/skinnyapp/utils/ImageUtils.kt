package com.skinny.skinnyapp.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlin.math.min

/**
 * ★★★ 이미지 처리 관련 유틸리티 클래스 (회전 교정 기능 포함) ★★★
 */
object ImageUtils {

    /**
     * ★★★ EXIF 데이터 기반 이미지 회전 교정 (핵심 해결책) ★★★
     */
    fun correctImageOrientation(bitmap: Bitmap, imagePath: String): Bitmap {
        return try {
            // EXIF 데이터에서 회전 정보 읽기
            val exif = ExifInterface(imagePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            // 회전 각도 결정
            val rotationAngle = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }

            Log.d("ImageUtils", "EXIF orientation: $orientation, rotation: $rotationAngle")

            // 회전이 필요한 경우에만 처리
            if (rotationAngle != 0f) {
                rotateImage(bitmap, rotationAngle)
            } else {
                bitmap
            }

        } catch (e: Exception) {
            Log.e("ImageUtils", "Failed to correct image orientation", e)
            bitmap // 오류 시 원본 반환
        }
    }

    /**
     * ★★★ 이미지 회전 처리 함수 ★★★
     */
    private fun rotateImage(bitmap: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix().apply {
            postRotate(angle)
        }

        return Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height,
            matrix, true
        ).also {
            // 메모리 최적화: 원본 비트맵 해제 (필요시)
            if (bitmap != it && !bitmap.isRecycled) {
                bitmap.recycle()
            }
        }
    }

    /**
     * ★★★ 디바이스 방향 기반 회전 각도 계산 (대안 방법) ★★★
     */
    fun getRotationAngleFromDeviceOrientation(context: android.content.Context): Float {
        val windowManager = context.getSystemService(android.content.Context.WINDOW_SERVICE)
                as android.view.WindowManager

        return when (windowManager.defaultDisplay.rotation) {
            android.view.Surface.ROTATION_90 -> 90f
            android.view.Surface.ROTATION_180 -> 180f
            android.view.Surface.ROTATION_270 -> 270f
            else -> 0f
        }
    }

    /**
     * ★★★ 서버 모델에 최적화된 이미지 전처리 및 Base64 변환 (회전 교정 포함) ★★★
     */
    fun bitmapToOptimizedBase64ForSkinAnalysis(bitmap: Bitmap, imagePath: String? = null): String {
        // 1. 회전 교정 (경로가 있는 경우)
        val correctedBitmap = if (imagePath != null) {
            correctImageOrientation(bitmap, imagePath)
        } else {
            bitmap
        }

        // 2. 서버 모델에 최적화된 리사이즈
        val optimizedBitmap = resizeForSkinAnalysisModel(correctedBitmap)

        // 3. 압축 품질 최적화
        val base64String = compressAndEncodeToBase64(optimizedBitmap)

        Log.d("ImageUtils", "이미지 처리 완료 - 회전 교정 → 224x224 리사이즈 → Base64 변환")
        Log.d("ImageUtils", "Base64 길이: ${base64String.length} characters")

        return base64String
    }

    /**
     * ★★★ 서버 모델 전용 224x224 리사이즈 (고품질 보간) ★★★
     */
    private fun resizeForSkinAnalysisModel(bitmap: Bitmap): Bitmap {
        // 서버가 224x224로 다시 리사이즈하므로, 클라이언트에서 미리 처리
        return Bitmap.createScaledBitmap(
            bitmap,
            224,
            224,
            true  // 고품질 bilinear 필터링 사용
        )
    }

    /**
     * ★★★ 압축 품질 최적화된 Base64 인코딩 ★★★
     */
    private fun compressAndEncodeToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()

        // ★★★ 피부 분석에 최적화된 JPEG 압축 설정 ★★★
        // 품질 88: 피부 텍스처 디테일 보존 + 적절한 용량 (테스트 결과 최적값)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 88, outputStream)
        val byteArray = outputStream.toByteArray()

        // Base64 인코딩
        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)

        // 용량 정보 로깅
        val sizeKB = byteArray.size / 1024
        Log.d("ImageUtils", "압축된 이미지 크기: ${sizeKB}KB (품질: 88)")

        // 서버 요구 형식에 맞춘 data URL 형태로 반환
        return "data:image/jpeg;base64,$base64String"
    }

    /**
     * ★★★ 품질별 압축 테스트 함수 (개발/디버깅용) ★★★
     */
    fun testCompressionQualities(bitmap: Bitmap): Map<Int, Pair<Int, String>> {
        val results = mutableMapOf<Int, Pair<Int, String>>()
        val qualities = listOf(70, 80, 85, 88, 90, 95)

        val resizedBitmap = resizeForSkinAnalysisModel(bitmap)

        qualities.forEach { quality ->
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val size = outputStream.size()
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            results[quality] = Pair(size / 1024, base64)  // KB 단위
            Log.d("ImageUtils", "품질 $quality: ${size / 1024}KB")
        }

        return results
    }

    /**
     * ★★★ 기존 호환성을 위한 범용 함수 ★★★
     */
    fun bitmapToBase64(bitmap: Bitmap, quality: Int = 85): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        val byteArray = outputStream.toByteArray()
        val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
        return "data:image/jpeg;base64,$base64String"
    }

    /**
     * ★★★ 이미지 크기 최적화 (기존 함수 개선) ★★★
     */
    fun optimizeBitmapForUpload(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
        // 서버 모델용이라면 224x224가 최적이므로 해당 크기로 조정
        return if (maxSize <= 224) {
            resizeForSkinAnalysisModel(bitmap)
        } else {
            // 일반적인 크기 최적화
            val ratio = minOf(
                maxSize.toFloat() / bitmap.width,
                maxSize.toFloat() / bitmap.height
            )

            if (ratio < 1.0f) {
                val newWidth = (bitmap.width * ratio).toInt()
                val newHeight = (bitmap.height * ratio).toInt()
                Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
            } else {
                bitmap
            }
        }
    }
}
