package com.skinny.skinnyapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.skinny.skinnyapp.ui.theme.SkinnyappTheme
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate("main") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4E9D8)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Skinni",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9DB8A5)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SkinnyappTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF9F5)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Skinni",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3E4651)
            )
        }
    }
}
