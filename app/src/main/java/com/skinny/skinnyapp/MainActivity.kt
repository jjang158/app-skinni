package com.skinny.skinnyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.skinny.skinnyapp.navigation.SkinnyNavGraph
import com.skinny.skinnyapp.ui.theme.SkinnyappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkinnyappTheme {
                val navController = rememberNavController()
                SkinnyNavGraph(navController)
            }
        }
    }
}
