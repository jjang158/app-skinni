// utils 패키지에 새 파일 생성: NetworkUtils.kt
package com.skinny.skinnyapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {
    private const val TAG = "NetworkUtils"

    /**
     * 인터넷 연결 상태 확인
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.d(TAG, "WiFi 연결됨")
                true
            }
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.d(TAG, "모바일 데이터 연결됨")
                true
            }
            else -> {
                Log.w(TAG, "네트워크 연결 없음")
                false
            }
        }
    }

    /**
     * 특정 서버 연결 테스트
     */
    suspend fun testServerConnection(serverUrl: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "서버 연결 테스트 시작: $serverUrl")

            val url = URL(serverUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.connect()

            val responseCode = connection.responseCode
            Log.d(TAG, "서버 응답 코드: $responseCode")

            val isConnected = responseCode == HttpURLConnection.HTTP_OK
            Log.d(TAG, "서버 연결 결과: $isConnected")

            connection.disconnect()
            isConnected
        } catch (e: Exception) {
            Log.e(TAG, "서버 연결 실패: ${e.message}", e)
            false
        }
    }
}
