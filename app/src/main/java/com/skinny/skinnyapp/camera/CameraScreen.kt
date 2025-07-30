package com.skinny.skinnyapp.camera

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.net.Uri

@Composable
fun CameraScreen(navController: NavController, onImageCaptured: (Uri) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraPreview(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Button(
            onClick = {
                // TODO: 이미지 캡처 후 결과 처리 및 화면 이동 구현
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(16.dp)
        ) {
            Text(text = "사진 찍기")
        }
    }
}
