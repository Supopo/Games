package com.xxx.newgames.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NewGamesColorScheme = darkColorScheme(
    primary = PurplePrimary,
    secondary = OrangeAccent,
    background = DeepPurple,
    surface = DeepPurple,
)

@Composable
fun NewGamesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = NewGamesColorScheme,
        content = content,
    )
}
