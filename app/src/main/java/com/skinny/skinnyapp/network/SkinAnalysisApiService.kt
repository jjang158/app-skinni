package com.skinny.skinnyapp.network

import android.util.Log
import com.skinny.skinnyapp.data.model.RecommendRequest
import com.skinny.skinnyapp.data.model.RecommendResponse
// ★★★ 중복 제거 - data.model 패키지의 클래스들을 import로 사용 ★★★
import com.skinny.skinnyapp.data.model.SkinAnalysisRequest
import com.skinny.skinnyapp.data.model.SkinAnalysisResponse
import com.skinny.skinnyapp.data.model.SkinAnalysisResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

/**
 * ★★★ 중복 제거된 API 인터페이스 ★★★
 */
interface SkinAnalysisApi {
    /**
     * 피부 분석 API - JSON + Base64 형식
     */
    @POST("api/skin-analysis/")
    suspend fun analyzeSkin(
        @Body request: SkinAnalysisRequest
    ): Response<SkinAnalysisResponse>

    /**
     * 제품 추천 API - 기존 유지
     */
    @POST("api/recommend/")
    suspend fun getRecommendations(
        @Body request: RecommendRequest
    ): Response<RecommendResponse>
}

/**
 * ★★★ 중복 데이터 클래스 제거 - import로 대체 ★★★
 * 이제 다음 클래스들은 com.skinny.skinnyapp.data.model 패키지에서 import:
 * - SkinAnalysisRequest
 * - SkinAnalysisResponse
 * - SkinAnalysisResult
 */

/**
 * ★★★ API 서비스 클래스 (수정 없음) ★★★
 */
class SkinAnalysisApiService {
    private val api: SkinAnalysisApi

    companion object {
        private const val TAG = "SkinAnalysisAPI"
    }

    init {
        // ★★★ 상세한 HTTP 로깅 설정 ★★★
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(TAG, message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(NetworkConfig.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(NetworkConfig.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(NetworkConfig.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(SkinAnalysisApi::class.java)

        // ★★★ 초기화 시 설정 확인 로그 ★★★
        Log.d(TAG, "API Service initialized with BASE_URL: ${NetworkConfig.BASE_URL}")
    }

    /**
     * ★★★ 피부 분석 API 호출 (data.model 클래스 사용) ★★★
     */
    suspend fun analyzeSkin(request: SkinAnalysisRequest): Result<SkinAnalysisResponse> {
        return try {
            // ★★★ 요청 데이터 로깅 ★★★
            Log.d(TAG, "=== 피부 분석 API 요청 시작 ===")
            Log.d(TAG, "Request URL: ${NetworkConfig.BASE_URL}api/skin-analysis/")
            Log.d(TAG, "Image data length: ${request.file.length} characters")
            Log.d(TAG, "Image format: ${if (request.file.startsWith("data:image/")) "Data URL format" else "Raw base64"}")

            val response = api.analyzeSkin(request)

            // ★★★ 응답 상태 로깅 ★★★
            Log.d(TAG, "Response Code: ${response.code()}")
            Log.d(TAG, "Response Message: ${response.message()}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // ★★★ 성공 응답 상세 로깅 ★★★
                Log.d(TAG, "=== 피부 분석 API 응답 성공 ===")
                Log.d(TAG, "Status: ${body.status}")
                Log.d(TAG, "Message: ${body.message}")
                Log.d(TAG, "Success: ${body.result.success}")
                Log.d(TAG, "Model Version: ${body.result.model_version}")

                // 평균값 로깅
                Log.d(TAG, "=== 분석 결과 평균값 ===")
                body.result.averages.forEach { (key, value) ->
                    Log.d(TAG, "$key: $value")
                }

                // 부위별 상세 결과 로깅 (있다면)
                body.result.parts?.let { parts ->
                    Log.d(TAG, "=== 부위별 상세 결과 ===")
                    parts.forEach { (partName, scores) ->
                        Log.d(TAG, "$partName:")
                        scores.forEach { (scoreName, value) ->
                            Log.d(TAG, "  $scoreName: $value")
                        }
                    }
                }

                Result.success(body)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (response.body()?.message != null) {
                    response.body()!!.message
                } else {
                    "피부 분석 요청 실패: ${response.code()} - ${response.message()}"
                }

                Log.e(TAG, "=== 피부 분석 API 응답 실패 ===")
                Log.e(TAG, "Error Code: ${response.code()}")
                Log.e(TAG, "Error Message: $errorMessage")
                Log.e(TAG, "Error Body: $errorBody")

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "=== 피부 분석 API 요청 예외 ===")
            Log.e(TAG, "Exception: ${e.javaClass.simpleName}")
            Log.e(TAG, "Message: ${e.message}")
            Log.e(TAG, "Stack trace:", e)

            Result.failure(e)
        }
    }

    /**
     * ★★★ 제품 추천 API 호출 (기존 유지) ★★★
     */
    suspend fun getRecommendations(request: RecommendRequest): Result<RecommendResponse> {
        return try {
            // ★★★ 요청 데이터 로깅 ★★★
            Log.d(TAG, "=== 제품 추천 API 요청 시작 ===")
            Log.d(TAG, "Request URL: ${NetworkConfig.BASE_URL}api/recommend/")
            Log.d(TAG, "Request Body: wrinkle=${request.wrinkle}, pore=${request.pore}, elasticity=${request.elasticity}, moisture=${request.moisture}")

            val response = api.getRecommendations(request)

            // ★★★ 응답 상태 로깅 ★★★
            Log.d(TAG, "Response Code: ${response.code()}")
            Log.d(TAG, "Response Message: ${response.message()}")

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // ★★★ 성공 응답 상세 로깅 ★★★
                Log.d(TAG, "=== 제품 추천 API 응답 성공 ===")
                Log.d(TAG, "Status: ${body.status}")
                Log.d(TAG, "Message: ${body.message}")
                Log.d(TAG, "Products Count: ${body.result.size}")

                body.result.forEachIndexed { index, product ->
                    Log.d(TAG, "Product $index: ${product.name} (${product.price}원)")
                    Log.d(TAG, "  - Company: ${product.company.name}")
                    Log.d(TAG, "  - Image URL: ${product.imageUrl}")
                    Log.d(TAG, "  - Scores: 주름=${product.wrinkle}, 모공=${product.pore}, 탄력=${product.elasticity}, 수분=${product.moisture}")
                }

                Result.success(body)
            } else {
                val errorMessage = if (response.body()?.message != null) {
                    response.body()!!.message!!
                } else {
                    "추천 요청 실패: ${response.code()} - ${response.message()}"
                }

                Log.e(TAG, "=== 제품 추천 API 응답 실패 ===")
                Log.e(TAG, "Error: $errorMessage")

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e(TAG, "=== 제품 추천 API 요청 예외 ===")
            Log.e(TAG, "Exception: ${e.javaClass.simpleName}")
            Log.e(TAG, "Message: ${e.message}")
            Log.e(TAG, "Stack trace:", e)

            Result.failure(e)
        }
    }
}
