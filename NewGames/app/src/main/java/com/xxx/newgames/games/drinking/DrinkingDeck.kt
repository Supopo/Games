package com.xxx.newgames.games.drinking

import kotlin.random.Random

internal class DrinkingDeck(cardCount: Int, private val random: Random = Random.Default) {
    private val cardCount = cardCount
    private val remaining = (0 until cardCount).shuffled(random).toMutableList()

    val remainingCount: Int get() = remaining.size

    fun draw(): Int? {
        if (remaining.isEmpty()) return null
        return remaining.removeAt(0)
    }

    fun shuffle() {
        remaining.clear()
        remaining.addAll((0 until cardCount).shuffled(random))
    }
}
