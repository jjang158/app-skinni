package com.skinny.skinnyapp.api.response

data class SkinAnalysisResponse(
    val status: Int,
    val message: String?,
    val result: ResultData?
)

data class ResultData(
    val success: Boolean,
    val model_version: String,
    val parts: Parts,
    val averages: Averages
)

data class Parts(
    val forehead: SkinPart?,
    val l_cheek: SkinPartWithPore?,
    val r_cheek: SkinPartWithPore?,
    val l_perocular: WrinklePart?,
    val r_perocular: WrinklePart?,
    val chin: SkinPart?
)

data class SkinPart(
    val moisture: Double,
    val elasticity: Double
)

data class SkinPartWithPore(
    val moisture: Double,
    val elasticity: Double,
    val pore: Double
)

data class WrinklePart(
    val wrinkle: Double
)

data class Averages(
    val moisture: Double,
    val elasticity: Double,
    val wrinkle: Double,
    val pore: Double
)
