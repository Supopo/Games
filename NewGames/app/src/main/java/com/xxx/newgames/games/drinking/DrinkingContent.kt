package com.xxx.newgames.games.drinking

data class DrinkingCard(
    val number: Int,
    val title: String,
    val description: String,
    val category: String,
    val categoryNote: String,
)

data class DrinkingContent(val cards: List<DrinkingCard>, val rules: List<String>)

fun parseDrinkingContent(text: String): DrinkingContent {
    val cards = mutableListOf<DrinkingCard>()
    val rules = mutableListOf<String>()
    val cardHeading = Regex("^(\\d{2,3})｜(.+)$")
    var category = ""
    var categoryNote = ""
    var pendingNumber: Int? = null
    var pendingTitle = ""
    var readingRules = false

    text.lineSequence().map(String::trim).filter(String::isNotEmpty).forEach { line ->
        when {
            line == "基础规则建议" -> readingRules = true
            readingRules -> if (line.matches(Regex("^\\d+\\..+"))) rules += line
            line.startsWith("【") && line.endsWith("】") -> {
                category = line.removeSurrounding("【", "】")
                categoryNote = ""
            }
            cardHeading.matches(line) -> {
                val match = cardHeading.matchEntire(line)!!
                pendingNumber = match.groupValues[1].toInt()
                pendingTitle = match.groupValues[2].trim()
            }
            line.startsWith("说明：") && pendingNumber != null -> {
                cards += DrinkingCard(
                    number = pendingNumber!!,
                    title = pendingTitle,
                    description = line.removePrefix("说明：").trim(),
                    category = category,
                    categoryNote = categoryNote,
                )
                pendingNumber = null
            }
            category.isNotEmpty() && pendingNumber == null && line != "====================" -> {
                categoryNote = line
            }
        }
    }
    return DrinkingContent(cards, rules)
}
