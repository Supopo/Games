package com.xxx.newgames.games.angry

import androidx.lifecycle.ViewModel
import com.xxx.newgames.data.RandomUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

data class AngryUiState(
    val bomb: Int,
    val multiplier: Int = 2,
    val cards: List<Int> = (0 until 8).toList(),
    val cleared: Boolean = false,
)

class AngryUncleViewModel(
    private val random: Random = Random.Default,
) : ViewModel() {
    private val _uiState = MutableStateFlow(createGame(2))
    val uiState: StateFlow<AngryUiState> = _uiState.asStateFlow()

    fun setMultiplier(multiplier: Int) {
        _uiState.value = createGame(multiplier)
    }

    fun pick(card: Int) {
        val state = _uiState.value
        if (card == state.bomb) _uiState.value = state.copy(cleared = true)
        else _uiState.value = state.copy(cards = state.cards - card)
    }

    fun restart() = setMultiplier(_uiState.value.multiplier)

    private fun createGame(multiplier: Int): AngryUiState {
        val cards = (0 until multiplier * 4).toList()
        return AngryUiState(
            multiplier = multiplier,
            cards = cards,
            bomb = RandomUtils.randomItem(cards, random) ?: 0,
        )
    }
}
