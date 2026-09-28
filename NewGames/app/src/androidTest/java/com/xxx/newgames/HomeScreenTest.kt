package com.xxx.newgames

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.test.espresso.Espresso
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeShowsLegacyGameEntriesWithoutAdvertisingEntry() {
        waitForHome()
        listOf(
            "真心话大冒险",
            "Games100",
            "愤怒的皮卡丘",
            "逛三园",
            "谁是卧底",
        ).forEach { title ->
            composeRule.onNodeWithTag("home_game_grid")
                .performScrollToNode(hasText(title))
            composeRule.onNodeWithText(title).assertIsDisplayed()
        }

        composeRule.onNodeWithText("广告测试").assertDoesNotExist()
        composeRule.onNodeWithText("打Call").assertDoesNotExist()
        composeRule.onNodeWithText("分组神器").assertDoesNotExist()
        composeRule.onNodeWithText("随机英雄").assertDoesNotExist()
        composeRule.onNodeWithText("记牌器").assertDoesNotExist()
    }

    @Test
    fun drinkingCardsEntryOpensTheNewGame() {
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid")
            .performScrollToNode(hasText("Games100"))
        composeRule.onNodeWithText("Games100").performClick()

        composeRule.onNodeWithText("抽一张").assertIsDisplayed()
        composeRule.onNodeWithText("剩余 100").assertIsDisplayed()
    }

    @Test
    fun parksEntryReopensWithOneTapAfterBack() {
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid")
            .performScrollToNode(hasText("逛三园"))
        composeRule.onNodeWithText("逛三园").performClick()
        composeRule.onNodeWithText("洗牌").assertIsDisplayed()

        composeRule.onNodeWithText("返回").performClick()
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid")
            .performScrollToNode(hasText("逛三园"))
        composeRule.onNode(hasText("逛三园") and hasClickAction()).performTouchInput { click() }

        composeRule.onNodeWithText("洗牌").assertIsDisplayed()
    }

    @Test
    fun parksEntryRespondsWhileReturnTransitionIsInProgress() {
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid")
            .performScrollToNode(hasText("逛三园"))
        composeRule.onNodeWithText("逛三园").performClick()
        composeRule.onNodeWithText("洗牌").assertIsDisplayed()

        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithText("返回").performClick()
        composeRule.mainClock.advanceTimeBy(100)
        waitForHome()
        composeRule.onNode(hasText("逛三园") and hasClickAction()).performTouchInput { click() }

        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()
        composeRule.onNodeWithText("洗牌").assertIsDisplayed()
    }

    @Test
    fun truthOrDareEntryReopensWithOneTap() {
        assertReopensWithOneTap("真心话大冒险", "添加玩家")
    }

    @Test
    fun drinkingEntryReopensWithOneTap() {
        assertReopensWithOneTap("Games100", "规则")
    }

    @Test
    fun drinkingEntryReopensAfterSystemBack() {
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid").performScrollToNode(hasText("Games100"))
        composeRule.onNode(hasText("Games100") and hasClickAction()).performClick()
        composeRule.onNodeWithText("规则").assertIsDisplayed()

        Espresso.pressBack()
        waitForHome()
        composeRule.onNode(hasText("Games100") and hasClickAction()).performTouchInput { click() }

        composeRule.onNodeWithText("规则").assertIsDisplayed()
    }

    @Test
    fun whoIsEntryReopensWithOneTap() {
        assertReopensWithOneTap("谁是卧底", "下一组")
    }

    @Test
    fun switchingBetweenGamesNeedsOnlyOneTap() {
        waitForHome()
        listOf(
            "Games100" to "规则",
            "真心话大冒险" to "添加玩家",
            "谁是卧底" to "下一组",
            "Games100" to "规则",
        ).forEach { (title, gameControl) ->
            composeRule.onNodeWithTag("home_game_grid").performScrollToNode(hasText(title))
            composeRule.onNode(hasText(title) and hasClickAction()).performTouchInput { click() }
            composeRule.onNodeWithText(gameControl).assertIsDisplayed()
            composeRule.onNodeWithText("返回").performClick()
            waitForHome()
        }
    }

    @Test
    fun drinkingReopensAfterRulesDialogWasDismissed() {
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid").performScrollToNode(hasText("Games100"))
        composeRule.onNode(hasText("Games100") and hasClickAction()).performClick()
        composeRule.onNodeWithText("规则").performClick()
        composeRule.onNodeWithText("知道了").performClick()
        composeRule.onNodeWithText("返回").performClick()
        waitForHome()
        composeRule.onNode(hasText("Games100") and hasClickAction()).performTouchInput { click() }
        composeRule.onNodeWithText("规则").assertIsDisplayed()
    }

    @Test
    fun whoIsReopensAfterSettingsDialogWasDismissed() {
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid").performScrollToNode(hasText("谁是卧底"))
        composeRule.onNode(hasText("谁是卧底") and hasClickAction()).performClick()
        composeRule.onNodeWithText("人数 6").performClick()
        composeRule.onNodeWithText("取消").performClick()
        composeRule.onNodeWithText("返回").performClick()
        waitForHome()
        composeRule.onNode(hasText("谁是卧底") and hasClickAction()).performTouchInput { click() }
        composeRule.onNodeWithText("下一组").assertIsDisplayed()
    }

    private fun assertReopensWithOneTap(title: String, gameControl: String) {
        waitForHome()
        composeRule.onNodeWithTag("home_game_grid").performScrollToNode(hasText(title))
        composeRule.onNode(hasText(title) and hasClickAction()).performClick()
        composeRule.onNodeWithText(gameControl).assertIsDisplayed()

        composeRule.mainClock.autoAdvance = false
        composeRule.onNodeWithText("返回").performClick()
        composeRule.mainClock.advanceTimeBy(100)
        waitForHome()
        composeRule.onNode(hasText(title) and hasClickAction()).performTouchInput { click() }

        composeRule.mainClock.autoAdvance = true
        composeRule.waitForIdle()
        composeRule.onNodeWithText(gameControl).assertIsDisplayed()
    }

    private fun waitForHome() {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithTag("home_game_grid").fetchSemanticsNodes().isNotEmpty()
        }
    }
}
