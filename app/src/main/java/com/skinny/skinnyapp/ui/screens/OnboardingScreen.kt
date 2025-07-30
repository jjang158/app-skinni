package com.skinny.skinnyapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.skinny.skinnyapp.R
import com.skinny.skinnyapp.navigation.Screen

@Composable
fun OnboardingScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // 로고 이미지
            Image(
                painter = painterResource(id = R.drawable.ic_skinni_logo),
                contentDescription = "Skinni App Logo",
                modifier = Modifier.weight(2f).fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            // ★★★ 수정: MainScreen 대신 DiagnosisEntryScreen으로 직접 이동 ★★★
            Button(
                onClick = {
                    navController.navigate("diagnosis_entry") {
                        popUpTo(Screen.Onboarding.route) { inclusive = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("분석하기", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
