package com.skinny.skinnyapp.viewmodel

// ★★★ ViewModelScope 관련 import ★★★
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// ★★★ Android 관련 import ★★★
import android.graphics.Bitmap
import android.util.Log

// ★★★ 프로젝트 data model import ★★★
import com.skinny.skinnyapp.data.model.SkinAnalysisRequest
import com.skinny.skinnyapp.data.model.SkinAnalysisResult
import com.skinny.skinnyapp.data.model.RecommendRequest
import com.skinny.skinnyapp.data.model.RecommendedProduct
import com.skinny.skinnyapp.data.model.SkinScore

// ★★★ 네트워크 서비스 import ★★★
import com.skinny.skinnyapp.network.SkinAnalysisApiService

// ★★★ 유틸리티 import ★★★
import com.skinny.skinnyapp.utils.ImageUtils

/**
 * ★★★ 상태 초기화 로깅이 개선된 AnalysisViewModel ★★★
 */
class AnalysisViewModel : ViewModel() {

    // API 서비스 인스턴스
    private val apiService = SkinAnalysisApiService()

    // UI 상태 관리
    private val _uiState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Initial)
    val uiState: StateFlow<AnalysisUiState> = _uiState.asStateFlow()

    // 현재 이미지 상태 관리
    private val _currentImage = MutableStateFlow<Bitmap?>(null)
    val currentImage: StateFlow<Bitmap?> = _currentImage.asStateFlow()

    /**
     * ★★★ 피부분석 API 호출 함수 ★★★
     */
    fun analyzeSkin(
        base64Image: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AnalysisUiState.Loading

                // 서버 API 호출
                val result = apiService.analyzeSkin(
                    SkinAnalysisRequest(file = base64Image)
                )

                if (result.isSuccess) {
                    val response = result.getOrThrow()

                    // 서버 응답을 UI 상태로 변환
                    val scores = convertServerResponseToSkinScores(response.result)
                    val averages = response.result.averages

                    _uiState.value = AnalysisUiState.Success(
                        scores = scores,
                        averages = averages,
                        modelVersion = response.result.model_version ?: "AI 분석 v1.0",
                        products = emptyList(),
                        hasRecommendations = false
                    )

                    onSuccess()
                } else {
                    _uiState.value = AnalysisUiState.Error("서버 분석 실패")
                    onError("서버에서 분석에 실패했습니다.")
                }

            } catch (e: Exception) {
                Log.e("AnalysisViewModel", "analyzeSkin failed", e)
                _uiState.value = AnalysisUiState.Error(e.message ?: "알 수 없는 오류")
                onError(e.message ?: "네트워크 연결을 확인해주세요.")
            }
        }
    }

    /**
     * 서버 응답을 SkinScore 리스트로 변환
     */
    private fun convertServerResponseToSkinScores(result: SkinAnalysisResult): List<SkinScore> {
        return listOf(
            SkinScore("moisture", result.averages["moisture"] ?: 0.0, "수분"),
            SkinScore("elasticity", result.averages["elasticity"] ?: 0.0, "탄력"),
            SkinScore("wrinkle", 100.0 - (result.averages["wrinkle"] ?: 0.0), "주름"),
            SkinScore("pore", 100.0 - (result.averages["pore"] ?: 0.0), "모공")
        )
    }

    /**
     * 기존 함수들 유지
     */
    fun setImageBitmap(bitmap: Bitmap) {
        _currentImage.value = bitmap
        Log.d("AnalysisViewModel", "이미지 비트맵 설정 완료")
    }

    fun hasImage(): Boolean {
        return _currentImage.value != null
    }

    /**
     * ★★★ 새로운 진단을 위한 완전한 상태 초기화 (로깅 개선) ★★★
     */
    fun resetForNewDiagnosis() {
        val previousState = getCurrentState()

        // 이미지 상태 초기화
        _currentImage.value = null

        // UI 상태 초기화
        _uiState.value = AnalysisUiState.Initial

        // ★★★ 상세한 로깅 (디버깅용) ★★★
        Log.d("AnalysisViewModel", "=== 새로운 진단을 위한 상태 초기화 ===")
        Log.d("AnalysisViewModel", "이전 상태: $previousState")
        Log.d("AnalysisViewModel", "현재 상태: ${getCurrentState()}")
        Log.d("AnalysisViewModel", "상태 초기화 완료")
    }

    /**
     * ★★★ 분석 결과만 초기화 (기존 이미지는 유지) ★★★
     */
    fun resetAnalysis() {
        _uiState.value = AnalysisUiState.Initial
        Log.d("AnalysisViewModel", "분석 결과 초기화 완료 (이미지 유지)")
    }

    /**
     * ★★★ 현재 상태 확인 함수 (디버깅용) ★★★
     */
    fun getCurrentState(): String {
        val hasImage = _currentImage.value != null
        val uiStateType = when (_uiState.value) {
            is AnalysisUiState.Initial -> "Initial"
            is AnalysisUiState.Loading -> "Loading"
            is AnalysisUiState.Success -> "Success"
            is AnalysisUiState.Error -> "Error"
        }
        return "이미지: $hasImage, UI상태: $uiStateType"
    }

    // 기존 더미 데이터 분석 함수 (필요시 사용)
    fun startAnalysis() {
        viewModelScope.launch {
            try {
                _uiState.value = AnalysisUiState.Loading

                // 더미 데이터로 분석 결과 시뮬레이션
                kotlinx.coroutines.delay(2000) // 2초 딜레이

                val mockScores = listOf(
                    SkinScore("moisture", 72.5, "수분"),
                    SkinScore("elasticity", 68.3, "탄력"),
                    SkinScore("wrinkle", 82.1, "주름"),
                    SkinScore("pore", 76.8, "모공")
                )

                val mockAverages = mapOf(
                    "moisture" to 72.5,
                    "elasticity" to 68.3,
                    "wrinkle" to 17.9, // 100 - 82.1
                    "pore" to 23.2     // 100 - 76.8
                )

                _uiState.value = AnalysisUiState.Success(
                    scores = mockScores,
                    averages = mockAverages,
                    modelVersion = "더미 테스트 v1.0",
                    products = emptyList(),
                    hasRecommendations = false
                )

            } catch (e: Exception) {
                _uiState.value = AnalysisUiState.Error("분석 중 오류가 발생했습니다.")
            }
        }
    }

    /**
     * 제품 추천 로드 (기존 함수)
     */
    fun loadRecommendationsFromServer() {
        viewModelScope.launch {
            try {
                val currentState = _uiState.value
                if (currentState !is AnalysisUiState.Success) return@launch

                val recommendRequest = RecommendRequest(
                    wrinkle = currentState.averages["wrinkle"]!!.toInt(),
                    pore = currentState.averages["pore"]!!.toInt(),
                    elasticity = currentState.averages["elasticity"]!!.toInt(),
                    moisture = currentState.averages["moisture"]!!.toInt()
                )

                val recommendResult = apiService.getRecommendations(recommendRequest)

                if (recommendResult.isSuccess) {
                    val response = recommendResult.getOrThrow()
                    val products = response.result

                    _uiState.value = currentState.copy(
                        products = products,
                        hasRecommendations = true
                    )

                    Log.d("AnalysisViewModel", "제품 추천 로드 완료: ${products.size}개 제품")
                }

            } catch (e: Exception) {
                Log.e("AnalysisViewModel", "추천 로드 실패", e)
            }
        }
    }
}

/**
 * UI 상태 정의
 */
sealed class AnalysisUiState {
    object Initial : AnalysisUiState()
    object Loading : AnalysisUiState()
    data class Success(
        val scores: List<SkinScore>,
        val averages: Map<String, Double>,
        val modelVersion: String,
        val products: List<RecommendedProduct>,
        val hasRecommendations: Boolean
    ) : AnalysisUiState()
    data class Error(val message: String) : AnalysisUiState()
}
