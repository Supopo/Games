package com.xxx.newgames.splash

import android.app.Activity
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.launch

private data class DrawnGlyph(val path: Path, val color: Int, val length: Float)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val activity = LocalContext.current as? Activity
    DisposableEffect(activity) {
        val controller = activity?.let { WindowCompat.getInsetsController(it.window, it.window.decorView) }
        controller?.isAppearanceLightStatusBars = true
        onDispose { controller?.isAppearanceLightStatusBars = false }
    }
    val trace = remember { Animatable(0f) }
    val fill = remember { Animatable(0f) }
    val finished by rememberUpdatedState(onFinished)
    val glyphs = remember {
        pikachuGlyphs.map { glyph ->
            val path = PathParser().parsePathString(glyph.pathData).toPath().asAndroidPath()
            val measure = PathMeasure(path, false)
            var length = 0f
            do {
                length += measure.length
            } while (measure.nextContour())
            DrawnGlyph(path, glyph.color, length)
        }
    }

    LaunchedEffect(Unit) {
        launch { fill.animateTo(1f, tween(durationMillis = 700, delayMillis = 600)) }
        trace.animateTo(1f, tween(durationMillis = 1_300, easing = LinearEasing))
        finished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .size(240.dp)
                .semantics { contentDescription = "皮卡丘启动动画" },
        ) {
            val canvas = drawContext.canvas.nativeCanvas
            val saveCount = canvas.save()
            canvas.scale(size.width / 256f, size.height / 256f)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 1.5f
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            glyphs.forEach { glyph ->
                paint.color = 0x32000000
                canvas.drawPath(glyph.path, paint)

                val tracedPath = Path()
                val measure = PathMeasure(glyph.path, false)
                var remaining = glyph.length * trace.value
                do {
                    val segment = minOf(remaining, measure.length)
                    if (segment > 0f) measure.getSegment(0f, segment, tracedPath, true)
                    remaining -= segment
                } while (remaining > 0f && measure.nextContour())
                paint.color = glyph.color
                canvas.drawPath(tracedPath, paint)

                if (fill.value > 0f) {
                    paint.style = Paint.Style.FILL
                    paint.alpha = (((glyph.color ushr 24) and 0xff) * fill.value).toInt()
                    canvas.drawPath(glyph.path, paint)
                    paint.style = Paint.Style.STROKE
                }
            }
            canvas.restoreToCount(saveCount)
        }
    }
}
