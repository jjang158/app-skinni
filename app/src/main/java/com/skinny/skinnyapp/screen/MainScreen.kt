package com.skinny.skinnyapp.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.skinny.skinnyapp.R

@Composable
fun MainScreen(navController: NavController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4E9D8))   // 배경 베이지 하드코딩
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4E9D8))  // 컬럼에 배경 지정
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_skinni_logo),
                contentDescription = "Skinni Logo",
                modifier = Modifier.size(360.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { navController.navigate("diagnosis") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9DB8A5)   // 버튼 그린 하드코딩
                )
            ) {
                Text("분석하기", fontSize = 18.sp)
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4E9D8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF4E9D8))  // 컬럼에 배경 지정
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(30.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_skinni_logo),
                contentDescription = "Skinni Logo",
                modifier = Modifier.size(360.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { /* 미리보기에서 클릭없음 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9DB8A5)
                )
            ) {
                Text("분석하기", fontSize = 18.sp)
            }
        }
    }
}
