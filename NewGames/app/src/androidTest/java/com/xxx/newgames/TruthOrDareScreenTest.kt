package com.xxx.newgames

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.xxx.newgames.games.truthordare.TruthOrDareResultStage
import com.xxx.newgames.games.truthordare.TruthOrDareEditor
import com.xxx.newgames.games.truthordare.TruthOrDareScreen
import com.xxx.newgames.games.truthordare.TruthOrDareViewModel
import com.xxx.newgames.ui.theme.NewGamesTheme
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class TruthOrDareScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun addTruthButtonOpensTheEditorInsteadOfDrawingAPlayer() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TruthOrDareViewModel(application)
        compose.setContent {
            NewGamesTheme { TruthOrDareScreen(onBack = {}, viewModel = viewModel) }
        }

        compose.onNodeWithText("添加真心话").performClick()

        compose.onNodeWithText("输入题目").assertIsDisplayed()
        compose.onNodeWithText("添加").assertIsDisplayed()
        compose.runOnIdle {
            assertEquals(TruthOrDareEditor.TRUTHS, viewModel.uiState.value.editor)
            assertEquals(0, viewModel.uiState.value.drawCount)
            assertNull(viewModel.uiState.value.resultStage)
        }
    }

    @Test
    fun blankPlayerInputAddsNumberedPlayers() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TruthOrDareViewModel(application)
        compose.setContent {
            NewGamesTheme { TruthOrDareScreen(onBack = {}, viewModel = viewModel) }
        }

        compose.onNodeWithText("添加玩家").performClick()
        compose.onNodeWithText("添加").performClick()
        compose.onNodeWithText("添加").performClick()

        compose.onNodeWithText("7. 7号玩家").assertIsDisplayed()
        compose.onNodeWithText("8. 8号玩家").assertIsDisplayed()
    }

    @Test
    fun pointerClickAnimatesBeforeShowingTheGameChoice() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = TruthOrDareViewModel(application)
        compose.setContent {
            NewGamesTheme { TruthOrDareScreen(onBack = {}, viewModel = viewModel) }
        }
        compose.onNodeWithText("添加玩家").assertIsDisplayed()
        val screenshot = compose.onRoot().captureToImage().asAndroidBitmap()
        File(application.getExternalFilesDir(null), "truth-or-dare.png").outputStream().use {
            screenshot.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        compose.mainClock.autoAdvance = false
        compose.onNodeWithContentDescription("点击皮卡丘指针开始转盘").performClick()
        compose.mainClock.advanceTimeBy(1_000)
        compose.runOnIdle {
            assertTrue(viewModel.uiState.value.isSpinning)
            assertNull(viewModel.uiState.value.resultStage)
        }

        compose.mainClock.advanceTimeBy(1_500)
        compose.runOnIdle {
            assertEquals(TruthOrDareResultStage.CHOOSE, viewModel.uiState.value.resultStage)
        }
        compose.onNodeWithText("本轮选中的玩家是").assertIsDisplayed()
        compose.onNodeWithText("大冒险").assertIsDisplayed()
        compose.onNodeWithText("真心话").assertIsDisplayed()
    }
}
