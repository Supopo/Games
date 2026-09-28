package com.xxx.newgames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import com.xxx.newgames.navigation.AppNavHost
import com.xxx.newgames.ui.theme.NewGamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NewGamesTheme {
                AppNavHost()
            }
        }
    }
}
