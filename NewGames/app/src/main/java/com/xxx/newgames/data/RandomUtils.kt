package com.xxx.newgames.data

import kotlin.random.Random

object RandomUtils {
    fun <T> randomItem(items: List<T>, random: Random = Random.Default): T? =
        items.takeIf { it.isNotEmpty() }?.let { it[random.nextInt(it.size)] }

    fun splitIntoTeams(
        names: List<String>,
        teamCount: Int,
        random: Random = Random.Default,
    ): List<List<String>> {
        if (teamCount <= 0) return emptyList()
        val teams = MutableList(teamCount) { mutableListOf<String>() }
        names.shuffled(random).forEachIndexed { index, name ->
            teams[index % teamCount].add(name)
        }
        return teams.map { it.toList() }
    }
}
