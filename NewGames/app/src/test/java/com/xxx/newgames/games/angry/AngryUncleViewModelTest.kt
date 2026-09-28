package com.xxx.newgames.games.angry

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class AngryUncleViewModelTest {
    @Test
    fun restartKeepsTheInitialTwoRowCardCount() {
        val viewModel = AngryUncleViewModel()
        val initialCardCount = viewModel.uiState.value.cards.size

        viewModel.restart()

        assertEquals(8, initialCardCount)
        assertEquals(initialCardCount, viewModel.uiState.value.cards.size)
    }

    @Test
    fun bombPositionIsNotFixedAtTheFirstCard() {
        val viewModel = AngryUncleViewModel(Random(1234))
        val positions = buildSet {
            repeat(32) {
                viewModel.restart()
                add(viewModel.uiState.value.bomb)
            }
        }

        assertTrue(positions.any { it != 0 })
    }
}
