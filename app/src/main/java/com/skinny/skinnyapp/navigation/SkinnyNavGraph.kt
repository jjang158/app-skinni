package com.skinny.skinnyapp.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.skinny.skinnyapp.ui.screens.DiagnosisEntryScreen // ★★★ 새로 추가 ★★★
import com.skinny.skinnyapp.ui.screens.MainScreen
import com.skinny.skinnyapp.ui.screens.OnboardingScreen
import com.skinny.skinnyapp.ui.screens.RecommendScreen
import com.skinny.skinnyapp.ui.screens.ResultScreen
import com.skinny.skinnyapp.ui.screens.SplashScreen
import com.skinny.skinnyapp.viewmodel.AnalysisViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
    object DiagnosisEntry : Screen("diagnosis_entry?openCamera={openCamera}") // ★★★ 새로 추가 ★★★
    object Result : Screen("result")
    object Recommend : Screen("recommend")
}

@Composable
fun SkinnyNavGraph(navController: NavHostController) {
    val viewModel: AnalysisViewModel = viewModel()

    NavHost(navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            SplashScreen(navController)
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController)
        }

        composable(Screen.Main.route) {
            MainScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // ★★★ 새로 추가: DiagnosisEntryScreen ★★★
        composable(
            route = Screen.DiagnosisEntry.route,
            arguments = listOf(
                navArgument("openCamera") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val openCamera = backStackEntry.arguments?.getBoolean("openCamera") ?: false
            DiagnosisEntryScreen(
                navController = navController,
                viewModel = viewModel,
                openCamera = openCamera
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(Screen.Recommend.route) {
            RecommendScreen(
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}
