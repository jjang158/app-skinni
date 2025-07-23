package com.skinny.skinnyapp.ui.theme

class LoadingScreen {
}

@Composable
fun LoadingScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF6A4C93))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "분석 중...",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFF6A4C93),
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
