package com.xxx.newgames.games.drinking

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DrinkingContentTest {
    @Test
    fun parserKeepsCardCategoryDescriptionAndBaseRulesSeparate() {
        val source = """
            喝酒游戏卡牌 100 张
            【一、快速反应类】
            01｜别看我
            说明：互相对视的人猜拳。

            【二、潜伏陷阱类】
            潜伏牌统一规则：最多持续3轮。
            02｜手机炸弹
            说明：第一个拿起手机的人受罚。

            基础规则建议
            1. 可以用无酒精饮料。
        """.trimIndent()

        val content = parseDrinkingContent(source)

        assertEquals(2, content.cards.size)
        assertEquals(DrinkingCard(1, "别看我", "互相对视的人猜拳。", "一、快速反应类", ""), content.cards[0])
        assertEquals("潜伏牌统一规则：最多持续3轮。", content.cards[1].categoryNote)
        assertEquals("第一个拿起手机的人受罚。", content.cards[1].description)
        assertEquals(listOf("1. 可以用无酒精饮料。"), content.rules)
    }

    @Test
    fun bundledFileProducesExactlyOneHundredCardsNotTheTrailingRules() {
        val source = File("src/main/assets/games100.txt").readText(Charsets.UTF_8)

        val content = parseDrinkingContent(source)

        assertEquals(100, content.cards.size)
        assertEquals((1..100).toList(), content.cards.map { it.number })
        assertEquals("别看我", content.cards.first().title)
        assertEquals("终极少数派", content.cards.last().title)
        assertTrue(content.cards.all { it.category.isNotBlank() && it.description.isNotBlank() })
        assertEquals(8, content.rules.size)
    }
}
