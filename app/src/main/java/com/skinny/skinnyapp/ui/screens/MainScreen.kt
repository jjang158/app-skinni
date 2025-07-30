package com.skinny.skinnyapp.ui.screens

import android.Manifest
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skinny.skinnyapp.navigation.Screen
import com.skinny.skinnyapp.viewmodel.AnalysisViewModel
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ★★★ 수정된 MainScreen - 이미지 선택만 담당하고 DiagnosisEntryScreen으로 이동 ★★★
 */
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: AnalysisViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ★★★ 카메라 프리뷰 상태 제거 (DiagnosisEntryScreen에서 처리) ★★★

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                scope.launch {
                    try {
                        val bitmap = uriToBitmap(context, uri)
                        if (bitmap != null) {
                            // ★★★ 이미지 설정 후 DiagnosisEntryScreen으로 이동 ★★★
                            viewModel.setImageBitmap(bitmap)
                            navController.navigate(Screen.DiagnosisEntry.route)
                        } else {
                            Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Log.e("MainScreen", "Gallery image processing failed", e)
                        Toast.makeText(context, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // ★★★ 권한 승인 시 DiagnosisEntryScreen으로 이동하여 카메라 촬영 ★★★
                navController.navigate("${Screen.DiagnosisEntry.route}?openCamera=true")
            } else {
                Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // ★★★ 단순한 선택 화면만 표시 ★★★
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SelectionContent(
            onLaunchCamera = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
            onPickGallery = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }
}

/**
 * 초기 선택 화면 UI (카메라/갤러리 버튼)
 */
@Composable
private fun SelectionContent(onLaunchCamera: () -> Unit, onPickGallery: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("피부 분석 방식을 선택하세요.")
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onLaunchCamera) {
            Text("카메라")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onPickGallery) {
            Text("갤러리")
        }
    }
}

// ★★★ URI to Bitmap 변환 유틸리티 함수 ★★★
private suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        Log.e("MainScreen", "Failed to convert URI to Bitmap", e)
        null
    }
}
