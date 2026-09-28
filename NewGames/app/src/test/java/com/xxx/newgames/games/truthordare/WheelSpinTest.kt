package com.xxx.newgames.games.truthordare

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WheelSpinTest {
    @Test
    fun fewerThanFourPlayersHaveTwoEqualWheelSegmentsEach() {
        assertEquals(2, WheelSpin.segmentCount(1))
        assertEquals(4, WheelSpin.segmentCount(2))
        assertEquals(6, WheelSpin.segmentCount(3))
        assertEquals(4, WheelSpin.segmentCount(4))
        for (playerCount in 1..3) {
            for (selectedIndex in 0 until playerCount) {
                val target = WheelSpin.targetRotation(
                    currentRotation = 0f,
                    selectedIndex = selectedIndex,
                    playerCount = playerCount,
                )
                val sweep = 360f / (playerCount * 2)
                val pointerSegment = (WheelSpin.normalizeDegrees(270f - target) / sweep).toInt()

                assertEquals(selectedIndex, pointerSegment)
            }
        }
    }

    @Test
    fun duplicatePlayerSegmentCanAlsoStopUnderThePointer() {
        val target = WheelSpin.targetRotation(
            currentRotation = 0f,
            selectedIndex = 1,
            playerCount = 3,
            useSecondSegment = true,
        )
        val pointerSegment = (WheelSpin.normalizeDegrees(270f - target) / 60f).toInt()

        assertEquals(4, pointerSegment)
    }

    @Test
    fun targetRotationPlacesSelectedSegmentUnderThePointer() {
        val playerCount = 6
        val selectedIndex = 3
        val target = WheelSpin.targetRotation(
            currentRotation = 0f,
            selectedIndex = selectedIndex,
            playerCount = playerCount,
        )

        val sweep = 360f / playerCount
        val segmentCenter = (selectedIndex + .5f) * sweep
        val finalCenter = WheelSpin.normalizeDegrees(segmentCenter + target)

        assertEquals(WheelSpin.normalizeDegrees(-90f), finalCenter, 0.001f)
        assertTrue(target >= 3 * 360f)
    }

    @Test
    fun targetRotationContinuesFromCurrentRotation() {
        val target = WheelSpin.targetRotation(
            currentRotation = 720f,
            selectedIndex = 0,
            playerCount = 4,
        )

        assertTrue(target > 720f)
        assertEquals(720f + 3 * 360f + 225f, target, 0.001f)
    }

    @Test
    fun everySelectedSegmentCanStopAtDifferentPositionsUnderThePointer() {
        for (playerCount in 1..20) {
            for (selectedIndex in 0 until playerCount) {
                for (positionRatio in listOf(.05f, .5f, .95f)) {
                    val currentRotation = 733f
                    val target = WheelSpin.targetRotation(
                        currentRotation, selectedIndex, playerCount, positionRatio,
                    )
                    val sweep = 360f / WheelSpin.segmentCount(playerCount)
                    val relativePointer = WheelSpin.normalizeDegrees(270f - target)

                    assertEquals(selectedIndex, (relativePointer / sweep).toInt())
                    assertTrue(target - currentRotation >= 3 * 360f)
                    assertTrue(target - currentRotation < 4 * 360f)
                }
            }
        }
    }

    @Test
    fun draggingAcrossZeroDegreesKeepsTheShortRotationDirection() {
        assertEquals(2f, WheelSpin.angularDelta(359f, 1f), .001f)
        assertEquals(-2f, WheelSpin.angularDelta(1f, 359f), .001f)
    }
}
