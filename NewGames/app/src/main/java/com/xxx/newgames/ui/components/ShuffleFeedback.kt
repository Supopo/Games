package com.xxx.newgames.ui.components

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val PokerRefreshAsset = "poker_refresh.mp3"

internal fun playAssetSound(context: Context, assetName: String) {
    runCatching {
        context.assets.openFd(assetName).use { afd ->
            MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                setOnCompletionListener { player -> player.release() }
                setOnErrorListener { player, _, _ ->
                    player.release()
                    true
                }
                prepare()
                start()
            }
        }
    }
}

class ShuffleFeedbackState internal constructor(
    private val context: Context,
    private val scope: kotlinx.coroutines.CoroutineScope,
) {
    var visible by mutableStateOf(false)
        private set
    private var hideJob: Job? = null

    fun trigger(onShuffle: () -> Unit = {}) {
        playAssetSound(context, PokerRefreshAsset)
        visible = true
        onShuffle()
        hideJob?.cancel()
        hideJob = scope.launch {
            delay(1_000)
            visible = false
        }
    }

    fun dispose() {
        hideJob?.cancel()
        hideJob = null
        visible = false
    }
}

@Composable
fun rememberShuffleFeedback(): ShuffleFeedbackState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = remember(context) { ShuffleFeedbackState(context, scope) }
    DisposableEffect(state) {
        onDispose { state.dispose() }
    }
    return state
}

/** 页内浮层提示，避免使用系统 Dialog 窗口导致返回后点击被吞。 */
@Composable
fun ShuffleTipOverlay(visible: Boolean, message: String = "正在洗牌...") {
    if (!visible) return
    val sink = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f)
            .background(Color.Black.copy(alpha = .28f))
            .clickable(
                interactionSource = sink,
                indication = null,
                onClick = {},
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier
                .background(Color.Black.copy(alpha = .72f), RoundedCornerShape(14.dp))
                .padding(horizontal = 28.dp, vertical = 16.dp),
        )
    }
}
