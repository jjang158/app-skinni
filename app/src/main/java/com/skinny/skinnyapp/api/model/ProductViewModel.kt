package com.skinny.skinnyapp.api.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skinny.skinnyapp.api.ApiClient
import com.skinny.skinnyapp.api.request.ProductRecommendRequest
import com.skinny.skinnyapp.api.response.ProductRecommendResponse
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    var result by mutableStateOf<ProductRecommendResponse?>(null)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun recommendProduct(
        wrinkle: Int,
        pore: Int,
        elasticity: Int,
        moisture: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = ProductRecommendRequest(wrinkle, pore, elasticity, moisture)
                val response = ApiClient.apiService.postProductRecommend(request)

                if (response.isSuccessful && response.body()?.status == 200) {
                    result = response.body()
                    onSuccess()
                } else {
                    error = response.body()?.message ?: "추천 실패"
                    onError(error!!)
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "네트워크 오류")
            }
        }
    }
}
