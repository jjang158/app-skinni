// app/src/main/java/com/skinny/skinnyapp/data/model/ApiModels.kt

package com.skinny.skinnyapp.data.model

import com.google.gson.annotations.SerializedName

// ★★★ Base64 JSON 형식의 피부 분석 API 요청 모델 (신규 추가) ★★★
data class SkinAnalysisRequest(
    val file: String // base64 인코딩된 이미지 (data:image/jpeg;base64,... 형식)
)

// ★★★ 피부 분석 API 응답 모델 (기존 수정) ★★★
data class SkinAnalysisResponse(
    val status: Int,
    val message: String? = null,
    val result: SkinAnalysisResult
)

// ★★★ 피부 분석 결과 모델 (기존 수정 - 더 유연한 구조) ★★★
data class SkinAnalysisResult(
    val success: Boolean,
    @SerializedName("model_version") val model_version: String?,
    val parts: Map<String, Map<String, Double>>?, // 부위별 상세 결과 (유연한 구조)
    val averages: Map<String, Double> // 평균값들 (유연한 구조)
)

// ★★★ 기존 상세 구조체들 (하위 호환성을 위해 유지) ★★★
data class SkinParts(
    val forehead: ForeheadData?,
    @SerializedName("l_cheek") val leftCheek: CheekData?,
    @SerializedName("r_cheek") val rightCheek: CheekData?,
    @SerializedName("l_perocular") val leftPeriocular: PeriocularData?,
    @SerializedName("r_perocular") val rightPeriocular: PeriocularData?,
    val chin: ChinData?
)

data class SkinAverages(
    val moisture: Double,
    val elasticity: Double,
    val wrinkle: Double,
    val pore: Double
)

data class ForeheadData(
    val moisture: Double,
    val elasticity: Double
)

data class CheekData(
    val moisture: Double,
    val elasticity: Double,
    val pore: Double
)

data class PeriocularData(
    val wrinkle: Double
)

data class ChinData(
    val moisture: Double,
    val elasticity: Double
)

// ★★★ 제품 추천 API 모델 (기존 유지) ★★★
data class RecommendRequest(
    val wrinkle: Int,
    val pore: Int,
    val elasticity: Int,
    val moisture: Int
)

data class RecommendResponse(
    val status: Int,
    val message: String? = null,
    val result: List<RecommendedProduct>
)

// ★★★ 실제 서버 응답에 맞는 RecommendedProduct (기존 유지) ★★★
data class RecommendedProduct(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    @SerializedName("commerce_url") val commerceUrl: String,
    val wrinkle: Int,
    val pore: Int,
    val elasticity: Int,
    val moisture: Int,
    @SerializedName("image_url") val imageUrl: String,
    val company: Company
)

data class Company(
    val name: String,
    val url: String
)

// UI에서 사용할 백분율 데이터 모델 (기존 유지)
data class SkinScore(
    val category: String,
    val percentage: Double,
    val displayName: String
)
