package com.skinny.skinnyapp.data

/**
 * AI 피부 진단 결과의 상세 항목을 나타내는 데이터 클래스입니다.
 *
 * @property label 진단 항목의 이름 (예: "수분", "모공")
 * @property score 0부터 100까지의 점수
 */
data class SkinResult(
    val label: String,
    val score: Int
)

///**
// * 진단 결과에 따라 추천되는 제품 정보를 나타내는 데이터 클래스입니다.
// *
// * @property name 제품명
// * @property description 제품에 대한 간단한 설명
// * @property imageUrl 제품 이미지 URL (또는 리소스 ID)
// */
//data class RecommendedProduct(
//    val name: String,
//    val description: String,
//    val imageUrl: String
//)