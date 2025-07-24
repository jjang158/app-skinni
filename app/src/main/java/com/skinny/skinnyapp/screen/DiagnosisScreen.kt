package com.skinny.skinnyapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun DiagnosisScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4E9D8))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 카메라 프레임
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RectangleShape)
                .background(Color.LightGray)
                .border(2.dp, Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Camera Icon",
                modifier = Modifier.size(64.dp),
                tint = Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 버튼: 카메라 / 이미지 등록
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { /* TODO: 카메라 구현 */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9DB8A5)
                )
            ) {
                Text("카메라")
            }

            Button(
                onClick = { /* TODO: 이미지 등록 구현 */ },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9DB8A5)
                )
            ) {
                Text("이미지등록")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 분석하기 버튼
        Button(
            onClick = {
                // TODO: 서버 연동 후 결과 화면으로 이동
                navController.navigate("result")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF9DB8A5)
            )
        ) {
            Text("분석하기")
        }
    }
}
