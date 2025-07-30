package com.skinny.skinnyapp.api.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinny.skinnyapp.api.ApiClient
import com.skinny.skinnyapp.api.request.SkinAnalysisRequest
import com.skinny.skinnyapp.api.response.SkinAnalysisResponse
import kotlinx.coroutines.launch

class SkinViewModel : ViewModel() {

    var result by mutableStateOf<SkinAnalysisResponse?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun analyzeSkin(
        base64Image: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = SkinAnalysisRequest(file = base64Image)
                val response = ApiClient.apiService.postSkinAnalysis(request)

                if (response.isSuccessful && response.body()?.status == 200) {
                    result = response.body()
                    onSuccess()
                } else {
                    error = response.body()?.message ?: "분석 실패"
                    onError(error!!)
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "네트워크 오류")
            }
        }
    }
}
