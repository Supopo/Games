package com.xxx.newgames.games.truthordare

internal object WheelSpin {
    private const val FullTurn = 360f
    private const val ExtraTurns = 3

    fun segmentCount(playerCount: Int): Int = if (playerCount < 4) playerCount * 2 else playerCount

    fun targetRotation(
        currentRotation: Float,
        selectedIndex: Int,
        playerCount: Int,
        positionRatio: Float = .5f,
        useSecondSegment: Boolean = false,
    ): Float {
        require(playerCount > 0) { "playerCount must be greater than zero" }
        require(selectedIndex in 0 until playerCount) { "selectedIndex must belong to the wheel" }
        require(positionRatio > 0f && positionRatio < 1f) { "positionRatio must be inside the segment" }

        val sweep = FullTurn / segmentCount(playerCount)
        val segmentIndex = selectedIndex + if (useSecondSegment && playerCount < 4) playerCount else 0
        val desiredRotation = 270f - (segmentIndex + positionRatio) * sweep
        val delta = normalizeDegrees(desiredRotation - currentRotation)
        return currentRotation + ExtraTurns * FullTurn + delta
    }

    fun normalizeDegrees(degrees: Float): Float = (degrees % FullTurn + FullTurn) % FullTurn

    fun angularDelta(from: Float, to: Float): Float = normalizeDegrees(to - from + 180f) - 180f
}
