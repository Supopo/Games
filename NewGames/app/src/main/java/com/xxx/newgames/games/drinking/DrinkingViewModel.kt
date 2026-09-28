package com.xxx.newgames.games.drinking

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.xxx.newgames.data.AssetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DrinkingUiState(
    val cards: List<DrinkingCard>,
    val rules: List<String>,
    val remainingCount: Int = cards.size,
    val current: DrinkingCard? = null,
    val revealed: Boolean = false,
)

class DrinkingViewModel(application: Application) : AndroidViewModel(application) {
    private val content = parseDrinkingContent(AssetRepository(application).loadText("games100.txt"))
    private val deck = DrinkingDeck(content.cards.size)
    private val _uiState = MutableStateFlow(
        DrinkingUiState(content.cards, content.rules, remainingCount = deck.remainingCount),
    )
    val uiState: StateFlow<DrinkingUiState> = _uiState.asStateFlow()

    fun shuffle() {
        deck.shuffle()
        _uiState.value = _uiState.value.copy(
            remainingCount = deck.remainingCount,
            current = null,
            revealed = false,
        )
    }

    fun draw() {
        val state = _uiState.value
        _uiState.value = if (state.revealed) {
            state.copy(current = null, revealed = false)
        } else {
            val index = deck.draw() ?: return
            state.copy(current = state.cards[index], remainingCount = deck.remainingCount, revealed = true)
        }
    }
}
