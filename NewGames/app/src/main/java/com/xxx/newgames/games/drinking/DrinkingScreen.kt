package com.xxx.newgames.games.drinking

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxx.newgames.R
import com.xxx.newgames.ui.components.GameActionChip
import com.xxx.newgames.ui.components.GameBackground
import com.xxx.newgames.ui.components.GamePrimaryButton
import com.xxx.newgames.ui.components.GameTopBar
import com.xxx.newgames.ui.components.ShuffleTipOverlay
import com.xxx.newgames.ui.components.rememberShuffleFeedback
import kotlinx.coroutines.launch

@Composable
fun DrinkingScreen(onBack: () -> Unit, viewModel: DrinkingViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(if (state.revealed) 180f else 0f) }
    val exit = remember { Animatable(0f) }
    var cardVisible by remember { mutableStateOf(state.revealed) }
    var animating by remember { mutableStateOf(false) }
    var showRules by remember { mutableStateOf(false) }
    val shuffleFeedback = rememberShuffleFeedback()
    val angle = rotation.value % 360f
    val frontVisible = angle >= 90f && angle < 270f
    val leave: () -> Unit = {
        showRules = false
        shuffleFeedback.dispose()
        onBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GameBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                GameTopBar(
                    title = "Games100",
                    onBack = leave,
                    actions = {
                        GameActionChip("洗牌", onClick = {
                            if (!animating) {
                                scope.launch {
                                    cardVisible = false
                                    rotation.snapTo(0f)
                                    exit.snapTo(0f)
                                    shuffleFeedback.trigger { viewModel.shuffle() }
                                }
                            }
                        })
                        GameActionChip("规则", onClick = { showRules = true })
                    },
                    titleTrailing = { GameActionChip("剩余 ${state.remainingCount}") },
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    val cardWidth = 220.dp
                    val cardHeight = 300.dp
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .size(cardWidth, 324.dp)
                            .drawBehind {
                                val layerHeight = 3.dp.toPx()
                                val cornerRadius = CornerRadius(10.dp.toPx())
                                repeat(8) { layer ->
                                    drawRoundRect(
                                        color = if (layer % 2 == 0) Color(0xFFC0C0C0) else Color(
                                            0xFFA9A9A9
                                        ),
                                        size = Size(size.width, size.height - layer * layerHeight),
                                        cornerRadius = cornerRadius,
                                    )
                                }
                            },
                    ) {
                        Image(
                            painter = painterResource(R.drawable.poker_blue_back),
                            contentDescription = "Games100 牌堆",
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .size(cardWidth, cardHeight)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.FillBounds,
                        )
                        if (cardVisible) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .size(cardWidth, cardHeight)
                                    .graphicsLayer {
                                        rotationY =
                                            if (frontVisible) angle - 180f else if (angle >= 270f) angle - 360f else angle
                                        cameraDistance = 10000f * density
                                        translationY = 340f * exit.value * density
                                        scaleX = 1f - .7f * exit.value
                                        scaleY = 1f - .7f * exit.value
                                        alpha = 1f - exit.value
                                    },
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    painter = painterResource(if (frontVisible) R.drawable.poker_blue_empty else R.drawable.poker_blue_back),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.FillBounds,
                                )
                                if (frontVisible) {
                                    state.current?.let { card ->
                                        Column(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(12.dp),
                                        ) {
                                            Text(
                                                "${
                                                    card.number.toString().padStart(2, '0')
                                                } / ${state.cards.size}",
                                                color = Color(0xFF4A8EB3),
                                                fontSize = 16.sp
                                            )
                                            Text(
                                                card.title,
                                                color = Color(0xFF4A8EB3),
                                                fontSize = 27.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                            )
                                            Text(
                                                card.category,
                                                color = Color(0xFF4A8EB3),
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    GamePrimaryButton(
                        text = when {
                            state.revealed -> "下一张"
                            state.remainingCount == 0 -> "重新洗牌"
                            else -> "抽一张"
                        },
                        enabled = !animating && state.cards.isNotEmpty(),
                        onClick = {
                            if (!animating) {
                                scope.launch {
                                    animating = true
                                    try {
                                        when {
                                            state.revealed -> {
                                                rotation.animateTo(
                                                    360f,
                                                    tween(1_000, easing = FastOutSlowInEasing)
                                                )
                                                exit.animateTo(
                                                    1f,
                                                    tween(550, easing = FastOutSlowInEasing)
                                                )
                                                cardVisible = false
                                                viewModel.draw()
                                                rotation.snapTo(0f)
                                                exit.snapTo(0f)
                                            }

                                            state.remainingCount == 0 -> {
                                                shuffleFeedback.trigger { viewModel.shuffle() }
                                            }

                                            else -> {
                                                rotation.snapTo(0f)
                                                exit.snapTo(0f)
                                                viewModel.draw()
                                                cardVisible = true
                                                rotation.animateTo(
                                                    180f,
                                                    tween(1_250, easing = FastOutSlowInEasing)
                                                )
                                            }
                                        }
                                    } finally {
                                        animating = false
                                    }
                                }
                            }
                        },
                    )
                    if (state.revealed && frontVisible) {
                        state.current?.let { card ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .background(
                                        Color.White.copy(alpha = .10f),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                if (card.categoryNote.isNotBlank()) {
                                    Text(
                                        card.categoryNote,
                                        color = Color(0xFFFFC0CB),
                                        fontSize = 14.sp
                                    )
                                }
                                Text(
                                    card.description,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp
                                )
                            }
                        }
                    }
                }
            }
            ShuffleTipOverlay(visible = shuffleFeedback.visible)
        }

        if (showRules) {
            AlertDialog(
                onDismissRequest = { showRules = false },
                title = { Text("基础规则") },
                text = {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 440.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        state.rules.forEach { rule -> Text(rule) }
                    }
                },
                confirmButton = { TextButton(onClick = { showRules = false }) { Text("知道了") } },
            )
        }
    }
}
