package com.skinny.skinnyapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.skinny.skinnyapp.ui.screen.*

@Composable
fun SkinnyNavGraph(
    navController: NavHostController,
    onLaunchCamera: () -> Unit,
    onPickGallery: () -> Unit
) {
    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(navController)
        }

        composable("main") {
            MainScreen(navController)
        }

        composable("diagnosis") {
            DiagnosisScreen(
                navController = navController,
                onLaunchCamera = onLaunchCamera,
                onPickGallery = onPickGallery
            )
        }

        composable("camera") {
            CameraScreen(navController)
        }

        // 나중에 추가될 화면
        // composable("result") { ResultScreen() }
        // composable("recommend") { RecommendScreen() }
    }
}
