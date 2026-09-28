package com.xxx.newgames.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.xxx.newgames.ui.theme.DeepPurple
import com.xxx.newgames.ui.theme.DeepPurpleEnd
import com.xxx.newgames.ui.theme.DeepPurpleMid

@Composable
fun GameBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepPurple, DeepPurpleMid, DeepPurpleEnd),
                ),
            ),
    ) {
        content()
    }
}
