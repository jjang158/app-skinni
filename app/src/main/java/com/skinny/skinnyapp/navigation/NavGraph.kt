package com.skinny.skinnyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.skinny.skinnyapp.ui.screen.*

@Composable
fun SkinnyNavGraph(navController: NavHostController) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") { SplashScreen(navController) }
        composable("main") { MainScreen(navController) }
        composable("diagnosis") { DiagnosisScreen(navController) }
        // composable("result") { ResultScreen() }  // 다른 팀원이 구성
        // composable("recommend") { RecommendScreen() }  // 다른 팀원이 구성
    }
}
