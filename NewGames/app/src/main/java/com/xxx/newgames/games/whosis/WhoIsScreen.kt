package com.xxx.newgames.games.whosis

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
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
import com.xxx.newgames.ui.components.GameUi
import com.xxx.newgames.ui.components.ShuffleTipOverlay
import com.xxx.newgames.ui.components.rememberShuffleFeedback
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WhoIsScreen(onBack: () -> Unit, viewModel: WhoIsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(if (state.revealed) 180f else 0f) }
    val exit = remember { Animatable(0f) }
    var cardVisible by remember { mutableStateOf(state.revealed) }
    var animating by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var previewIndex by remember { mutableStateOf<Int?>(null) }
    var previewJob by remember { mutableStateOf<Job?>(null) }
    val shuffleFeedback = rememberShuffleFeedback()
    val angle = rotation.value % 360f
    val frontVisible = angle >= 90f && angle < 270f
    val finished = state.currentIndex >= state.userCount
    val leave: () -> Unit = {
        showSettings = false
        shuffleFeedback.dispose()
        onBack()
    }

    if (state.editing) {
        WhoIsEditorScreen(
            entries = state.wordPairs,
            isSaving = state.isSaving,
            errorMessage = state.errorMessage,
            onSave = viewModel::saveEditor,
        )
        return
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
                    title = "谁是卧底",
                    onBack = leave,
                    actions = {
                        GameActionChip("人数 ${state.userCount}", onClick = {
                            if (!animating) showSettings = true
                        })
                        GameActionChip("下一组", onClick = {
                            if (!animating) {
                                previewJob?.cancel()
                                previewIndex = null
                                scope.launch {
                                    cardVisible = false
                                    rotation.snapTo(0f)
                                    exit.snapTo(0f)
                                    shuffleFeedback.trigger { viewModel.nextGroup() }
                                }
                            }
                        })
                        GameActionChip("添加", onClick = {
                            if (!animating) viewModel.openEditor()
                        })
                    },
                    titleTrailing = {
                        Text(
                            text = if (finished) {
                                "第 ${state.round} 局 · 已发完"
                            } else {
                                "第 ${state.round} 局 · 第 ${state.currentIndex + 1} 位"
                            },
                            color = Color.White.copy(alpha = .85f),
                            fontSize = 14.sp,
                        )
                    },
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
                                    color = if (layer % 2 == 0) Color(0xFFC0C0C0) else Color(0xFFA9A9A9),
                                    size = Size(size.width, size.height - layer * layerHeight),
                                    cornerRadius = cornerRadius,
                                )
                            }
                        },
                ) {
                    Image(
                        painter = painterResource(R.drawable.poker_blue_back),
                        contentDescription = "谁是卧底牌堆",
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
                                    rotationY = if (frontVisible) angle - 180f else if (angle >= 270f) angle - 360f else angle
                                    cameraDistance = 10000f * density
                                    translationY = 340f * exit.value * density
                                    scaleX = 1f - .7f * exit.value
                                    scaleY = 1f - .7f * exit.value
                                    alpha = 1f - exit.value
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                painter = painterResource(
                                    if (frontVisible) R.drawable.poker_blue_empty else R.drawable.poker_blue_back,
                                ),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.FillBounds,
                            )
                            if (frontVisible) {
                                Text(
                                    text = state.currentWord,
                                    color = Color(0xFF4A8EB3),
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 18.dp),
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier.height(GameUi.primaryButtonHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    if (!finished) {
                        GamePrimaryButton(
                            text = if (state.revealed) "下一位" else "抽一张",
                            onClick = {
                                if (!animating) {
                                    scope.launch {
                                        animating = true
                                        try {
                                            if (!state.revealed) {
                                                rotation.snapTo(0f)
                                                exit.snapTo(0f)
                                                viewModel.revealOrNext()
                                                cardVisible = true
                                                rotation.animateTo(180f, tween(1_250, easing = FastOutSlowInEasing))
                                            } else {
                                                rotation.animateTo(360f, tween(1_000, easing = FastOutSlowInEasing))
                                                exit.animateTo(1f, tween(1_000, easing = FastOutSlowInEasing))
                                                cardVisible = false
                                                viewModel.revealOrNext()
                                                rotation.snapTo(0f)
                                                exit.snapTo(0f)
                                            }
                                        } finally {
                                            animating = false
                                        }
                                    }
                                }
                            },
                            enabled = !animating,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(1.dp),
                ) {
                    repeat(state.currentIndex) { index ->
                        Column(
                            modifier = Modifier.size(59.dp, 100.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(59.dp, 80.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .combinedClickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        onClick = {},
                                        onLongClickLabel = "查看卡牌内容",
                                        onLongClick = {
                                            previewJob?.cancel()
                                            previewIndex = index
                                            previewJob = scope.launch {
                                                delay(1_000)
                                                previewIndex = null
                                            }
                                        },
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                val previewed = previewIndex == index
                                Image(
                                    painter = painterResource(
                                        if (previewed) R.drawable.poker_blue_empty else R.drawable.poker_blue_back,
                                    ),
                                    contentDescription = "${index + 1}号玩家卡牌，长按查看",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds,
                                )
                                if (previewed) {
                                    Text(
                                        text = state.wordForPlayer(index),
                                        color = Color(0xFF4A8EB3),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        maxLines = 3,
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                    )
                                }
                            }
                            Text("${index + 1}号", color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
            }
        }
        ShuffleTipOverlay(visible = shuffleFeedback.visible, message = "下一组...")
    }
    if (showSettings) {
        WhoIsSettingsDialog(
            state = state,
            onDismiss = { showSettings = false },
            onConfirm = { total, undercover, blank ->
                val accepted = viewModel.setCounts(total, undercover, blank)
                if (accepted) {
                    previewJob?.cancel()
                    previewIndex = null
                    cardVisible = false
                    showSettings = false
                    scope.launch {
                        rotation.snapTo(0f)
                        exit.snapTo(0f)
                    }
                }
                accepted
            },
        )
    }
}

@Composable
private fun WhoIsSettingsDialog(
    state: WhoIsUiState,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int) -> Boolean,
) {
    var total by remember { mutableStateOf(state.userCount.toString()) }
    var undercover by remember { mutableStateOf(state.undercoverCount.toString()) }
    var blank by remember { mutableStateOf(state.blankCount.toString()) }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置人数") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = total,
                    onValueChange = { total = it; error = false },
                    label = { Text("总人数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = undercover,
                    onValueChange = { undercover = it; error = false },
                    label = { Text("卧底数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                OutlinedTextField(
                    value = blank,
                    onValueChange = { blank = it; error = false },
                    label = { Text("白板数") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                if (error) {
                    Text("总人数需为 3–20，卧底至少 1 位，白板不能为负，并保留至少 1 位平民", color = Color(0xFFEC5F98))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                error = !onConfirm(
                    total.toIntOrNull() ?: -1,
                    undercover.toIntOrNull() ?: -1,
                    blank.toIntOrNull() ?: -1,
                )
            }) { Text("确定") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}
