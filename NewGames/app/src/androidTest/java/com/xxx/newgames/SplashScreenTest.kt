package com.xxx.newgames

import android.app.Activity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.xxx.newgames.splash.SplashScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SplashScreenTest {
    @get:Rule val compose = createComposeRule()

    @Test
    fun statusBarRemainsVisibleWhileSplashIsDisplayed() {
        lateinit var activity: Activity
        compose.mainClock.autoAdvance = false
        compose.setContent {
            activity = LocalContext.current as Activity
            SplashScreen(onFinished = {})
        }

        compose.onNodeWithContentDescription("皮卡丘启动动画").assertIsDisplayed()
        compose.runOnUiThread {
            val insets = ViewCompat.getRootWindowInsets(activity.window.decorView)
            assertEquals(true, insets?.isVisible(WindowInsetsCompat.Type.statusBars()))
        }
    }

    @Test
    fun pikachuIsShownBeforeTheSplashCompletes() {
        var completionCount = 0
        compose.mainClock.autoAdvance = false
        compose.setContent { SplashScreen(onFinished = { completionCount++ }) }

        compose.onNodeWithContentDescription("皮卡丘启动动画").assertIsDisplayed()
        compose.mainClock.advanceTimeBy(600)
        compose.runOnUiThread { assertEquals(0, completionCount) }

        compose.mainClock.advanceTimeBy(800)
        compose.runOnUiThread { assertEquals(1, completionCount) }
    }
}
