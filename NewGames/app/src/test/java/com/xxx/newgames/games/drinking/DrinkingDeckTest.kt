package com.xxx.newgames.games.drinking

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DrinkingDeckTest {
    @Test
    fun shuffleUsesEveryCardOnceBeforeExhaustionAndRestoresTheDeck() {
        val deck = DrinkingDeck(cardCount = 4, random = Random(1))

        assertEquals(setOf(0, 1, 2, 3), List(4) { deck.draw() }.toSet())
        assertNull(deck.draw())
        assertEquals(0, deck.remainingCount)

        deck.shuffle()

        assertEquals(4, deck.remainingCount)
        assertEquals(setOf(0, 1, 2, 3), List(4) { deck.draw() }.toSet())
    }

    @Test
    fun shuffleChangesDrawOrderAcrossRuns() {
        val first = DrinkingDeck(cardCount = 8, random = Random(1)).let { deck ->
            List(8) { deck.draw() }
        }
        val second = DrinkingDeck(cardCount = 8, random = Random(2)).let { deck ->
            List(8) { deck.draw() }
        }

        assertEquals(setOf(0, 1, 2, 3, 4, 5, 6, 7), first.toSet())
        assertEquals(setOf(0, 1, 2, 3, 4, 5, 6, 7), second.toSet())
        assertEquals(false, first == second)
    }
}
