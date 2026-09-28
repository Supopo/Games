package com.xxx.newgames.data

data class Player(val name: String)

data class GamePrompt(
    val text: String,
    val category: String,
)

data class TeamResult(
    val name: String,
    val members: List<String>,
)
