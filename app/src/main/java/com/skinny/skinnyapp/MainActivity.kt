package com.skinny.skinnyapp

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.rememberNavController
import com.skinny.skinnyapp.navigation.SkinnyNavGraph
import com.skinny.skinnyapp.ui.theme.SkinnyappTheme

class MainActivity : ComponentActivity() {

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 카메라 Intent 결과 처리
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                // TODO: imageUri 처리
            }
        }

        // 갤러리 Intent 결과 처리
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data
                // TODO: imageUri 처리
            }
        }

        setContent {
            SkinnyappTheme {
                val navController = rememberNavController()

                SkinnyNavGraph(
                    navController = navController,
                    onLaunchCamera = { launchCamera() },
                    onPickGallery = { launchGallery() }
                )
            }
        }
    }

    // 카메라 앱 실행
    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(intent)
    }

    // 갤러리 이미지 선택
    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }
}
