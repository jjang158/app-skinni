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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.skinny.skinnyapp.navigation.Screen
import com.skinny.skinnyapp.viewmodel.AnalysisUiState
import com.skinny.skinnyapp.viewmodel.AnalysisViewModel
import com.skinny.skinnyapp.data.model.RecommendedProduct
import com.skinny.skinnyapp.data.model.Company
import com.skinny.skinnyapp.ui.theme.SkinnyappTheme

/**
 * 실제 앱에서 사용되는 RecommendScreen - ViewModel과 연동
 */
@Composable
fun RecommendScreen(
    navController: NavController,
    viewModel: AnalysisViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    RecommendScreenContent(
        uiState = uiState,
        navController = navController,
        onResetForNewDiagnosis = viewModel::resetForNewDiagnosis,
        onResetAnalysis = viewModel::resetAnalysis
    )
}

/**
 * ★★★ 상태 초기화가 적용된 순수한 UI 컴포넌트 ★★★
 */
@Composable
fun RecommendScreenContent(
    uiState: AnalysisUiState,
    navController: NavController,
    onResetForNewDiagnosis: () -> Unit,
    onResetAnalysis: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val uriHandler = LocalUriHandler.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AI 맞춤 제품 추천",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is AnalysisUiState.Success -> {
                    if (state.products.isNotEmpty()) {
                        // ★★★ 서버에서 받은 실제 추천 제품 표시 ★★★
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.products) { product ->
                                ServerProductCard(
                                    product = product,
                                    onProductClick = { uriHandler.openUri(product.commerceUrl) },
                                    onCompanyClick = { uriHandler.openUri(product.company.url) }
                                )
                            }
                        }
                    } else {
                        // ★★★ 추천 데이터 로딩 중 표시 ★★★
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "맞춤 제품을 찾고 있습니다...",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                is AnalysisUiState.Error -> {
                    // ★★★ 에러 상태 표시 ★★★
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "추천 제품을 불러올 수 없습니다",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = state.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "추천 제품 정보를 불러오는 중입니다...")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ★★★ 하단 버튼들 (상태 초기화 적용) ★★★
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // ★★★ 처음으로 돌아가기 버튼 (상태 초기화 적용) ★★★
                Button(
                    onClick = {
                        // ★★★ 상태 완전 초기화 후 처음 화면으로 ★★★
                        onResetForNewDiagnosis()

                        navController.navigate(Screen.Onboarding.route) {
                            // 모든 백스택 정리
                            popUpTo(navController.graph.id) {
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("처음으로 돌아가기")
                }

                Spacer(modifier = Modifier.width(8.dp))

                // ★★★ 다시 진단하기 버튼 (상태 초기화 적용) ★★★
                Button(
                    onClick = {
                        // ★★★ 상태 초기화 후 진단 화면으로 ★★★
                        onResetForNewDiagnosis()

                        navController.navigate(Screen.DiagnosisEntry.route) {
                            popUpTo(Screen.DiagnosisEntry.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("다시 진단하기")
                }
            }
        }
    }
}

/**
 * ★★★ 서버에서 받은 실제 제품 데이터를 표시하는 카드 ★★★
 */
@Composable
private fun ServerProductCard(
    product: RecommendedProduct,
    onProductClick: () -> Unit,
    onCompanyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        onClick = onProductClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ★★★ 서버에서 받은 실제 제품 이미지 ★★★
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = "${product.name} 이미지",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                // ★★★ 서버에서 받은 제품 정보 ★★★
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // ★★★ 서버에서 받은 회사 정보 ★★★
                    TextButton(
                        onClick = onCompanyClick,
                        modifier = Modifier.padding(0.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "by ${product.company.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }

                // ★★★ 서버에서 받은 가격 정보 ★★★
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${product.price}원",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ★★★ 서버에서 받은 제품 효능 점수 ★★★
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                EffectChip("주름", product.wrinkle)
                EffectChip("모공", product.pore)
                EffectChip("탄력", product.elasticity)
                EffectChip("수분", product.moisture)
            }
        }
    }
}

@Composable
private fun EffectChip(label: String, score: Int) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = "$label $score 점",
                style = MaterialTheme.typography.bodySmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = when {
                score >= 4 -> MaterialTheme.colorScheme.primaryContainer
                score >= 3 -> MaterialTheme.colorScheme.secondaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        )
    )
}

// ★★★ 프리뷰 함수 ★★★
@Preview(showBackground = true)
@Composable
fun RecommendScreenWithServerDataPreview() {
    val mockProducts = listOf(
        RecommendedProduct(
            id = "P_0004",
            name = "토리든 솔리드인 크림 70ml",
            price = "17900",
            description = "건조해진 피부 장벽을 탄탄하게 채워주는 고농축 크림",
            wrinkle = 2,
            pore = 4,
            elasticity = 5,
            moisture = 3,
            imageUrl = "http://43.202.92.248/images/P_4.png",
            commerceUrl = "https://smartstore.naver.com/narsha_mall/products/11466719832",
            company = Company(
                name = "토리든",
                url = "https://www.torriden.com"
            )
        )
    )

    val mockUiState = AnalysisUiState.Success(
        scores = emptyList(),
        averages = emptyMap(),
        modelVersion = "테스트",
        products = mockProducts,
        hasRecommendations = true
    )

    SkinnyappTheme {
        RecommendScreenContent(
            uiState = mockUiState,
            navController = rememberNavController(),
            onResetForNewDiagnosis = {},
            onResetAnalysis = {}
        )
    }
}
