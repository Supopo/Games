package com.xxx.newgames.games.whosis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WhoIsUiStateTest {
    @Test
    fun lastPlayerCanBeUndercoverWithoutIndexingPastTheTwoWords() {
        val state = WhoIsUiState(
            userCount = 8,
            words = listOf("卧底词", "平民词"),
            undercoverIndices = setOf(7),
            currentIndex = 7,
            revealed = true,
        )

        assertEquals("卧底词", state.currentWord)
    }

    @Test
    fun civilianReceivesTheOtherWord() {
        val state = WhoIsUiState(
            userCount = 8,
            words = listOf("卧底词", "平民词"),
            undercoverIndices = setOf(7),
            currentIndex = 2,
            revealed = true,
        )

        assertEquals("平民词", state.currentWord)
    }

    @Test
    fun blankPlayersSeeBlankWhileMultipleUndercoverPlayersShareOneWord() {
        val state = WhoIsUiState(
            userCount = 8,
            words = listOf("卧底词", "平民词"),
            undercoverIndices = setOf(1, 6),
            blankIndices = setOf(3, 7),
        )

        assertEquals("卧底词", state.wordForPlayer(1))
        assertEquals("卧底词", state.wordForPlayer(6))
        assertEquals("白板", state.wordForPlayer(3))
        assertEquals("白板", state.wordForPlayer(7))
        assertEquals("平民词", state.wordForPlayer(0))
    }

    @Test
    fun roleAssignmentUsesDistinctPlayersAndRequestedCounts() {
        repeat(100) {
            val (undercover, blank) = assignRoles(total = 8, undercoverCount = 2, blankCount = 2)

            assertEquals(2, undercover.size)
            assertEquals(2, blank.size)
            assertTrue(undercover.all { it in 0 until 8 })
            assertTrue(blank.all { it in 0 until 8 })
            assertTrue(undercover.intersect(blank).isEmpty())
        }
    }
}
