package com.xxx.newgames.games.truthordare

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xxx.newgames.R
import com.xxx.newgames.ui.components.GameActionChip
import com.xxx.newgames.ui.components.GameTopBar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.random.Random

internal val TruthOrDarePink = Color(0xFFFFC0CB)
internal val TruthOrDarePurple = Color(0xFF7A378B)
internal val TruthOrDareDark = Color(0xFF252525)
private val DecelerateEasing = Easing { progress -> 1f - (1f - progress) * (1f - progress) }

@Composable
fun TruthOrDareScreen(
    onBack: () -> Unit,
    viewModel: TruthOrDareViewModel = viewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val pointerInteractionSource = remember { MutableInteractionSource() }
    val animation = remember { Animatable(0f) }
    var rotation by rememberSaveable { mutableFloatStateOf(0f) }
    var movementJob by remember { mutableStateOf<Job?>(null) }

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.cancelSpin()
            viewModel.clearResult()
        }
    }

    val editor = state.editor
    if (editor != null) {
        TruthOrDareEditorScreen(
            editor = editor,
            entries = when (editor) {
                TruthOrDareEditor.PLAYERS -> state.players
                TruthOrDareEditor.TRUTHS -> state.truths
                TruthOrDareEditor.DARES -> state.dares
            },
            isSaving = state.isSaving,
            errorMessage = state.errorMessage,
            onSave = viewModel::saveEditor,
        )
        return
    }

    val leave: () -> Unit = {
        viewModel.clearResult()
        viewModel.cancelSpin()
        onBack()
    }

    val onSpinClick: () -> Unit = {
        viewModel.startSpin()?.let { selectedIndex ->
            movementJob?.cancel()
            movementJob = scope.launch {
                try {
                    animation.snapTo(rotation)
                    val target = WheelSpin.targetRotation(
                        currentRotation = rotation,
                        selectedIndex = selectedIndex,
                        playerCount = state.players.size,
                        positionRatio = Random.nextFloat().coerceIn(.05f, .95f),
                        useSecondSegment = state.players.size < 4 && Random.nextBoolean(),
                    )
                    animation.animateTo(target, tween(2_000, easing = DecelerateEasing)) {
                        rotation = value
                    }
                    rotation = WheelSpin.normalizeDegrees(rotation)
                    viewModel.finishSpin(selectedIndex)
                } finally {
                    viewModel.cancelSpin()
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        Brush.radialGradient(
                            colors = listOf(TruthOrDarePurple, TruthOrDareDark),
                            center = center,
                            radius = size.minDimension * (600f / 1080f),
                        ),
                    )
                }
                .navigationBarsPadding(),
        ) {
            val wheelSize = minOf(maxWidth * .75f, maxHeight * .65f)
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .fillMaxWidth()
                    .statusBarsPadding(),
            ) {
                GameTopBar(title = "真心话大冒险", onBack = leave, actions = {
                    GameActionChip(text = state.drawCount.toString())
                })
            }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(wheelSize),
            contentAlignment = Alignment.Center,
        ) {
            val gestures = Modifier.pointerInput(state.isSpinning, state.resultStage) {
                if (state.isSpinning || state.resultStage != null) return@pointerInput
                val tracker = VelocityTracker()
                val center = Offset(size.width / 2f, size.height / 2f)
                var previousPosition = Offset.Zero
                detectDragGestures(
                    onDragStart = { position ->
                        movementJob?.cancel()
                        tracker.resetTracking()
                        previousPosition = position
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        tracker.addPosition(change.uptimeMillis, change.position)
                        val before = previousPosition - center
                        val after = change.position - center
                        if (before.getDistance() > 12.dp.toPx() && after.getDistance() > 12.dp.toPx()) {
                            val from = (atan2(before.y, before.x) * 180f / PI).toFloat()
                            val to = (atan2(after.y, after.x) * 180f / PI).toFloat()
                            rotation += WheelSpin.angularDelta(from, to)
                        }
                        previousPosition = change.position
                    },
                    onDragEnd = {
                        val velocity = tracker.calculateVelocity()
                        val radius = previousPosition - center
                        val distanceSquared = radius.x * radius.x + radius.y * radius.y
                        if (distanceSquared > 0f) {
                            val angularVelocity = (
                                (radius.x * velocity.y - radius.y * velocity.x) /
                                    distanceSquared * 180f / PI
                                ).toFloat().coerceIn(-1080f, 1080f)
                            if (abs(angularVelocity) > 20f) {
                                movementJob = scope.launch {
                                    animation.snapTo(rotation)
                                    animation.animateTo(
                                        rotation + angularVelocity / 2f,
                                        tween(1_000, easing = DecelerateEasing),
                                    ) { rotation = value }
                                    rotation = WheelSpin.normalizeDegrees(rotation)
                                }
                            }
                        }
                    },
                )
            }
            PlayerWheel(
                players = state.players,
                rotation = rotation,
                modifier = Modifier.fillMaxSize().then(gestures),
            )
            Image(
                painter = painterResource(R.drawable.icon_pkq),
                contentDescription = "点击皮卡丘指针开始转盘",
                modifier = Modifier
                    .size(90.dp)
                    .clickable(
                        interactionSource = pointerInteractionSource,
                        indication = null,
                        enabled = !state.isSpinning,
                        role = Role.Button,
                        onClick = onSpinClick,
                    ),
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TruthOrDareAction(
                text = "添加真心话",
                modifier = Modifier.weight(1f),
                enabled = !state.isSpinning,
                onClick = { viewModel.openEditor(TruthOrDareEditor.TRUTHS) },
            )
            TruthOrDareAction(
                text = "添加玩家",
                modifier = Modifier.weight(1f),
                enabled = !state.isSpinning,
                onClick = { viewModel.openEditor(TruthOrDareEditor.PLAYERS) },
            )
            TruthOrDareAction(
                text = "添加大冒险",
                modifier = Modifier.weight(1f),
                enabled = !state.isSpinning,
                onClick = { viewModel.openEditor(TruthOrDareEditor.DARES) },
            )
        }
        }

        state.resultStage?.let {
            TruthOrDareResultDialog(
                state = state,
                onChoose = viewModel::choose,
                onChangePrompt = viewModel::changePrompt,
                onRefuse = viewModel::refuse,
                onDismiss = viewModel::clearResult,
            )
        }
    }
}

@Composable
internal fun TruthOrDareAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    GameActionChip(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        horizontalPadding = 8.dp,
    )
}

@Composable
private fun PlayerWheel(players: List<String>, rotation: Float, modifier: Modifier = Modifier) {
    val paint = remember { Paint(Paint.ANTI_ALIAS_FLAG) }
    val path = remember { Path() }
    Canvas(
        modifier = modifier
            .semantics { contentDescription = "玩家转盘：" + players.joinToString("，") }
            .graphicsLayer { rotationZ = rotation },
    ) {
        val segmentCount = WheelSpin.segmentCount(players.size)
        val sweep = 360f / segmentCount.coerceAtLeast(1)
        val lineWidth = 1.dp.toPx()
        val inset = lineWidth
        val arcBounds = RectF(0f, 0f, size.width, size.height)
        val baseTextSize = size.minDimension * (if (players.size > 8) 30f else 50f) / 810f
        repeat(segmentCount) { index ->
            val player = players[index % players.size]
            val start = index * sweep
            drawArc(
                color = TruthOrDarePink,
                startAngle = start,
                sweepAngle = sweep,
                useCenter = true,
                topLeft = Offset(inset, inset),
                size = Size(size.width - inset * 2, size.height - inset * 2),
                style = Stroke(lineWidth),
            )

            paint.color = android.graphics.Color.WHITE
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = baseTextSize
            paint.typeface = android.graphics.Typeface.DEFAULT
            val arcLength = (size.minDimension / 2f * sweep * PI / 180f).toFloat()
            val measuredWidth = paint.measureText(player)
            if (measuredWidth > arcLength * .8f) {
                paint.textSize *= arcLength * .8f / measuredWidth
            }
            val textHeight = paint.fontMetrics.bottom - paint.fontMetrics.top
            path.reset()
            path.addArc(arcBounds, start, sweep)
            drawContext.canvas.nativeCanvas.drawTextOnPath(
                player,
                path,
                0f,
                textHeight + size.minDimension * (10f / 810f),
                paint,
            )
        }
    }
}
