package com.xxx.newgames.data

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RandomUtilsTest {
    @Test
    fun randomItemReturnsNullForEmptyInput() {
        assertNull(RandomUtils.randomItem(emptyList(), Random(1)))
    }

    @Test
    fun randomItemReturnsAnExistingItem() {
        val items = listOf("A", "B", "C")
        check(RandomUtils.randomItem(items, Random(1)) in items)
    }

    @Test
    fun splitIntoTeamsKeepsEveryoneAndBalancesTeamSizes() {
        val names = listOf("A", "B", "C", "D", "E")

        val teams = RandomUtils.splitIntoTeams(names, teamCount = 2, random = Random(2))

        assertEquals(names.toSet(), teams.flatten().toSet())
        check(teams.map { it.size }.sorted() == listOf(2, 3))
    }
}
