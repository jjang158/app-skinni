package com.skinny.skinnyapp.api

import com.skinny.skinnyapp.api.request.ProductRecommendRequest
import com.skinny.skinnyapp.api.request.SkinAnalysisRequest
import com.skinny.skinnyapp.api.response.ProductRecommendResponse
import com.skinny.skinnyapp.api.response.SkinAnalysisResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("skin-analysis/")
    suspend fun postSkinAnalysis(
        @Body request: SkinAnalysisRequest
    ): Response<SkinAnalysisResponse>

    @POST("recommend/")
    suspend fun postProductRecommend(
        @Body request: ProductRecommendRequest
    ): Response<ProductRecommendResponse>
}
