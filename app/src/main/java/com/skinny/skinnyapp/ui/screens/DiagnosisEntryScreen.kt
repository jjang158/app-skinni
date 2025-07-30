package com.skinny.skinnyapp.ui.screens

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.skinny.skinnyapp.navigation.Screen
import com.skinny.skinnyapp.viewmodel.AnalysisUiState
import com.skinny.skinnyapp.viewmodel.AnalysisViewModel
import com.skinny.skinnyapp.utils.FaceDetectionUtils
import com.skinny.skinnyapp.utils.FaceDetectionResult
import com.skinny.skinnyapp.utils.ImageUtils
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * ★★★ 이미지 회전 교정 및 상태 초기화가 적용된 DiagnosisEntryScreen ★★★
 */
@Composable
fun DiagnosisEntryScreen(
    navController: NavController,
    viewModel: AnalysisViewModel,
    openCamera: Boolean = false
) {
    // ★★★ 화면 진입 시 이전 상태가 남아있다면 초기화 ★★★
    LaunchedEffect(Unit) {
        // 이전 분석 결과가 남아있다면 초기화
        if (viewModel.uiState.value is AnalysisUiState.Success) {
            Log.d("DiagnosisEntry", "이전 분석 결과 감지됨 - 상태 초기화 실행")
            viewModel.resetForNewDiagnosis()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val uiState by viewModel.uiState.collectAsState()
        val currentImage by viewModel.currentImage.collectAsState()
        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        var showCameraPreview by remember { mutableStateOf(openCamera) }
        var analysisStarted by remember { mutableStateOf(false) }

        // ★★★ 얼굴 인식 처리 상태 및 결과 추가 ★★★
        var isProcessingFace by remember { mutableStateOf(false) }
        var faceDetectionResult by remember { mutableStateOf<FaceDetectionResult?>(null) }

        // ★★★ 갤러리에서 이미지 선택 (회전 교정 적용) ★★★
        val photoPickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                if (uri != null) {
                    scope.launch {
                        try {
                            isProcessingFace = true
                            val bitmap = uriToBitmap(context, uri)
                            if (bitmap != null) {
                                // ★★★ 이미지 경로 추출 (EXIF 데이터 읽기용) ★★★
                                val imagePath = getPathFromUri(context, uri)

                                // ★★★ 회전 교정 적용 ★★★
                                val correctedBitmap = if (imagePath != null) {
                                    ImageUtils.correctImageOrientation(bitmap, imagePath)
                                } else {
                                    bitmap
                                }

                                // 얼굴 감지 및 크롭
                                val result = FaceDetectionUtils.detectAndCenterFaceWithResult(correctedBitmap)
                                faceDetectionResult = result
                                viewModel.setImageBitmap(result.bitmap)
                                showCameraPreview = false

                                Toast.makeText(context, "이미지 회전이 교정되었습니다", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Log.e("DiagnosisEntryScreen", "Gallery image processing failed", e)
                            Toast.makeText(context, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                        } finally {
                            isProcessingFace = false
                        }
                    }
                }
            }
        )

        val cameraPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { isGranted ->
                if (isGranted) {
                    showCameraPreview = true
                } else {
                    Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // ★★★ 분석 시작 함수 - API 호출 연동 (회전 교정 적용) ★★★
        val startAnalysisWithAPI = {
            currentImage?.let { bitmap ->
                // 1. 이미지 최적화 (회전 교정 포함)
                val optimizedBase64 = ImageUtils.bitmapToOptimizedBase64ForSkinAnalysis(bitmap)

                // 2. 용량 정보 표시
                val sizeKB = (optimizedBase64.length * 3 / 4) / 1024
                Log.d("DiagnosisEntry", "회전 교정된 이미지 전송 크기: 약 ${sizeKB}KB")

                // 3. API 호출
                viewModel.analyzeSkin(
                    base64Image = optimizedBase64,
                    onSuccess = {
                        Toast.makeText(context, "피부 분석이 완료되었습니다!", Toast.LENGTH_SHORT).show()
                        analysisStarted = true
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, "분석 실패: $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                )
            } ?: run {
                Toast.makeText(context, "먼저 사진을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(uiState, analysisStarted) {
            if (uiState is AnalysisUiState.Success && analysisStarted) {
                navController.navigate(Screen.Result.route) {
                    popUpTo(Screen.DiagnosisEntry.route) { inclusive = false }
                }
                analysisStarted = false
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                showCameraPreview -> {
                    // ★★★ 개선된 카메라 프리뷰 - 얼굴 가이드라인 포함 ★★★
                    EnhancedCameraPreviewContent(
                        onImageCaptured = { uri ->
                            scope.launch {
                                try {
                                    isProcessingFace = true
                                    val bitmap = uriToBitmap(context, uri)
                                    if (bitmap != null) {
                                        // ★★★ 카메라 촬영 이미지도 회전 교정 적용 ★★★
                                        val imagePath = getPathFromUri(context, uri)
                                        val correctedBitmap = if (imagePath != null) {
                                            ImageUtils.correctImageOrientation(bitmap, imagePath)
                                        } else {
                                            bitmap
                                        }

                                        val result = FaceDetectionUtils.detectAndCenterFaceWithResult(correctedBitmap)
                                        faceDetectionResult = result
                                        viewModel.setImageBitmap(result.bitmap)
                                        showCameraPreview = false

                                        Toast.makeText(context, "카메라 이미지 회전이 교정되었습니다", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "촬영된 이미지를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Log.e("DiagnosisEntryScreen", "Camera image processing failed", e)
                                    Toast.makeText(context, "이미지 처리 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isProcessingFace = false
                                }
                            }
                        },
                        onBack = { showCameraPreview = false }
                    )
                }
                uiState is AnalysisUiState.Loading -> {
                    LoadingView()
                }
                else -> {
                    EnhancedDiagnosisEntryContent(
                        currentImage = currentImage,
                        faceDetectionResult = faceDetectionResult,
                        onLaunchCamera = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                        onPickGallery = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onStartAnalysis = startAnalysisWithAPI,
                        hasImage = viewModel.hasImage(),
                        isProcessingFace = isProcessingFace,
                        onBack = {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Onboarding.route) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * ★★★ 개선된 DiagnosisEntryContent - 얼굴 감지 결과 피드백 포함 ★★★
 */
@Composable
private fun EnhancedDiagnosisEntryContent(
    currentImage: Bitmap?,
    faceDetectionResult: FaceDetectionResult?,
    onLaunchCamera: () -> Unit,
    onPickGallery: () -> Unit,
    onStartAnalysis: () -> Unit,
    hasImage: Boolean,
    isProcessingFace: Boolean,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = onBack) {
                Text("← 돌아가기")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ★★★ 개선된 이미지 미리보기 영역 - 얼굴 감지 결과 표시 ★★★
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isProcessingFace -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "이미지 회전을 교정하고 얼굴을 인식하고 있습니다...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    currentImage != null -> {
                        Image(
                            bitmap = currentImage.asImageBitmap(),
                            contentDescription = "얼굴 중앙 정렬된 이미지",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )

                        // ★★★ 얼굴 감지 결과 피드백 오버레이 ★★★
                        faceDetectionResult?.let { result ->
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                            ) {
                                if (result.faceDetected) {
                                    Text(
                                        text = "✓ 얼굴 ${result.faceCount}개 감지됨 (회전 교정 완료)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "신뢰도: ${(result.confidence * 100).toInt()}%",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Text(
                                        text = "⚠ 얼굴 미감지 (중앙 크롭됨)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📸",
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "얼굴 사진을 촬영하거나\n갤러리에서 선택해주세요",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "자동으로 회전이 교정되고 얼굴이 중앙에 정렬됩니다",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 카메라/갤러리 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onLaunchCamera,
                modifier = Modifier.weight(1f),
                enabled = !isProcessingFace
            ) {
                Text("카메라")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onPickGallery,
                modifier = Modifier.weight(1f),
                enabled = !isProcessingFace
            ) {
                Text("갤러리")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartAnalysis,
            enabled = hasImage && !isProcessingFace,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("피부 분석하기")
        }
    }
}

/**
 * ★★★ 얼굴 가이드라인이 포함된 카메라 프리뷰 ★★★
 */
@Composable
private fun EnhancedCameraPreviewContent(
    onImageCaptured: (Uri) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isCameraReady by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 카메라 프리뷰
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraProviderFuture.addListener({
                    try {
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build()

                        imageCapture = ImageCapture.Builder().build()

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                        isCameraReady = true
                    } catch (e: Exception) {
                        Log.e("DiagnosisEntryScreen", "Camera binding failed", e)
                        Toast.makeText(ctx, "카메라 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // ★★★ 얼굴 가이드라인 오버레이 ★★★
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2 - (size.height * 0.05f) // 약간 위쪽에 배치
            val radius = size.width * 0.35f

            // 외부 원 (가이드라인)
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 3.dp.toPx())
            )

            // 내부 원 (얼굴 영역 힌트)
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = radius * 0.8f,
                center = Offset(centerX, centerY),
                style = Stroke(width = 2.dp.toPx())
            )
        }

        // ★★★ 안내 텍스트 오버레이 ★★★
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "원 안에 얼굴을 맞춰주세요\n회전이 자동으로 교정됩니다",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // 촬영 및 뒤로가기 버튼
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = 0.6f)
                )
            ) {
                Text("뒤로", color = Color.White)
            }

            Button(
                onClick = {
                    if (isCameraReady) {
                        takePicture(context, imageCapture, onImageCaptured)
                    } else {
                        Toast.makeText(context, "카메라가 준비되지 않았습니다.", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isCameraReady,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("촬영")
            }
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("AI가 분석중입니다...")
        }
    }
}

/**
 * ★★★ URI에서 실제 파일 경로 추출 (EXIF 데이터 읽기용) ★★★
 */
private fun getPathFromUri(context: Context, uri: Uri): String? {
    return try {
        when (uri.scheme) {
            "file" -> uri.path
            "content" -> {
                // MediaStore에서 실제 경로 찾기
                val cursor = context.contentResolver.query(
                    uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                        it.getString(columnIndex)
                    } else null
                }
            }
            else -> null
        }
    } catch (e: Exception) {
        Log.e("DiagnosisEntryScreen", "Failed to get path from URI", e)
        null
    }
}

private suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IOException) {
        Log.e("DiagnosisEntryScreen", "Failed to convert URI to Bitmap", e)
        null
    }
}

private fun takePicture(
    context: Context,
    imageCapture: ImageCapture?,
    onImageCaptured: (Uri) -> Unit
) {
    val imageCapture = imageCapture ?: return

    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.KOREA)
        .format(System.currentTimeMillis())
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/SkinnyApp")
        }
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                Log.e("DiagnosisEntryScreen", "Photo capture failed: ${exception.message}", exception)
                Toast.makeText(context, "사진 촬영에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = output.savedUri ?: return
                Log.d("DiagnosisEntryScreen", "Photo capture succeeded: $savedUri")
                Toast.makeText(context, "사진이 촬영되었습니다. 회전이 자동 교정됩니다.", Toast.LENGTH_SHORT).show()
                onImageCaptured(savedUri)
            }
        }
    )
}
