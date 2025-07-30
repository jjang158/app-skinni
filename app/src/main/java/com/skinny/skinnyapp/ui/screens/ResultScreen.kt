package com.skinny.skinnyapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.skinny.skinnyapp.navigation.Screen
import com.skinny.skinnyapp.viewmodel.AnalysisUiState
import com.skinny.skinnyapp.viewmodel.AnalysisViewModel
import com.skinny.skinnyapp.data.model.SkinScore
import com.skinny.skinnyapp.ui.theme.SkinnyappTheme

/**
 * 실제 앱에서 사용되는 ResultScreen - ViewModel과 연동
 */
@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: AnalysisViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    ResultScreenContent(
        uiState = uiState,
        navController = navController,
        onResetForNewDiagnosis = viewModel::resetForNewDiagnosis,
        onResetAnalysis = viewModel::resetAnalysis,
        onLoadRecommendations = viewModel::loadRecommendationsFromServer
    )
}

/**
 * ★★★ 상태 초기화가 적용된 순수한 UI 컴포넌트 ★★★
 */
@Composable
fun ResultScreenContent(
    uiState: AnalysisUiState,
    navController: NavController,
    onResetForNewDiagnosis: () -> Unit,
    onResetAnalysis: () -> Unit,
    onLoadRecommendations: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is AnalysisUiState.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "AI 피부 분석 결과",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "모델 버전: ${state.modelVersion}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // ★★★ 분석 결과 표시 ★★★
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.scores) { score ->
                                ScoreCard(
                                    title = score.displayName,
                                    percentage = score.percentage,
                                    category = score.category
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // ★★★ 버튼 영역 (상태 초기화 적용) ★★★
                        Column {
                            // 추천 제품 보기 버튼
                            Button(
                                onClick = {
                                    if (!state.hasRecommendations) {
                                        onLoadRecommendations()
                                    }
                                    navController.navigate(Screen.Recommend.route)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "AI 추천 제품 보기")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // ★★★ 다시 진단하기 버튼 (상태 초기화 추가) ★★★
                            Button(
                                onClick = {
                                    // ★★★ 상태 완전 초기화 후 네비게이션 ★★★
                                    onResetForNewDiagnosis()

                                    navController.navigate(Screen.DiagnosisEntry.route) {
                                        // 백스택에서 현재 결과 화면 제거
                                        popUpTo(Screen.Result.route) {
                                            inclusive = true
                                        }
                                        // 단일 인스턴스로 실행
                                        launchSingleTop = true
                                        // 이전 상태 복원 방지
                                        restoreState = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = "다시 진단하기")
                            }
                        }
                    }
                }
                is AnalysisUiState.Loading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("AI가 분석중입니다...")
                    }
                }
                else -> {
                    Text(text = "진단 결과를 불러오는 중입니다...")
                }
            }
        }
    }
}

/**
 * 백분율을 표시하는 스코어 카드 컴포넌트
 */
@Composable
private fun ScoreCard(
    title: String,
    percentage: Double,
    category: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "${percentage.toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = getScoreColor(percentage)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = (percentage / 100).toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = getScoreColor(percentage),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = getScoreDescription(percentage),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun getScoreColor(percentage: Double): androidx.compose.ui.graphics.Color {
    return when {
        percentage >= 80 -> MaterialTheme.colorScheme.primary
        percentage >= 60 -> MaterialTheme.colorScheme.secondary
        percentage >= 40 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }
}

private fun getScoreDescription(percentage: Double): String {
    return when {
        percentage >= 80 -> "매우 좋음"
        percentage >= 60 -> "좋음"
        percentage >= 40 -> "보통"
        else -> "관리 필요"
    }
}

// ★★★ 프리뷰 함수 ★★★
@Preview(showBackground = true)
@Composable
fun ResultScreenSuccessPreview() {
    val mockScores = listOf(
        SkinScore("moisture", 75.0, "수분"),
        SkinScore("elasticity", 68.0, "탄력"),
        SkinScore("wrinkle", 82.0, "주름"),
        SkinScore("pore", 71.0, "모공")
    )

    val mockUiState = AnalysisUiState.Success(
        scores = mockScores,
        averages = mapOf(
            "moisture" to 75.0,
            "elasticity" to 68.0,
            "wrinkle" to 18.0,
            "pore" to 29.0
        ),
        modelVersion = "더미 테스트 v1.0",
        products = emptyList(),
        hasRecommendations = false
    )

    SkinnyappTheme {
        ResultScreenContent(
            uiState = mockUiState,
            navController = rememberNavController(),
            onResetForNewDiagnosis = {},
            onResetAnalysis = {},
            onLoadRecommendations = {}
        )
    }
}
