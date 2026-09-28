package com.xxx.newgames.games.whosis

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WhoIsUiState(
    val userCount: Int = 6,
    val undercoverCount: Int = 1,
    val blankCount: Int = 0,
    val wordPairs: List<String> = emptyList(),
    val currentPair: String = "",
    val words: List<String> = emptyList(),
    val undercoverIndices: Set<Int> = setOf(0),
    val blankIndices: Set<Int> = emptySet(),
    val currentIndex: Int = 0,
    val revealed: Boolean = false,
    val round: Int = 1,
    val editing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val currentWord: String
        get() = wordForPlayer(currentIndex)

    fun wordForPlayer(index: Int): String = when {
        index !in 0 until userCount -> ""
        index in blankIndices -> "白板"
        index in undercoverIndices -> words.getOrNull(0).orEmpty()
        else -> words.getOrNull(1).orEmpty()
    }
}

internal fun assignRoles(total: Int, undercoverCount: Int, blankCount: Int): Pair<Set<Int>, Set<Int>> {
    val positions = (0 until total).shuffled()
    return positions.take(undercoverCount).toSet() to positions.drop(undercoverCount).take(blankCount).toSet()
}

class WhoIsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WhoIsRepository(application)
    private val initialPairs = repository.load()
    private val usedPairs = mutableSetOf<String>()
    private val _uiState = MutableStateFlow(createRound(initialPairs, 6, 1, 0, 1))
    val uiState: StateFlow<WhoIsUiState> = _uiState.asStateFlow()

    fun revealOrNext() {
        val state = _uiState.value
        if (state.editing || state.isSaving || state.currentIndex >= state.userCount) return
        _uiState.value = if (!state.revealed) {
            state.copy(revealed = true)
        } else if (state.currentIndex + 1 < state.userCount) {
            state.copy(currentIndex = state.currentIndex + 1, revealed = false)
        } else {
            state.copy(currentIndex = state.userCount, revealed = false)
        }
    }

    fun nextGroup() {
        val state = _uiState.value
        if (state.editing || state.isSaving) return
        _uiState.value = createRound(
            wordPairs = state.wordPairs,
            count = state.userCount,
            undercoverCount = state.undercoverCount,
            blankCount = state.blankCount,
            round = state.round + 1,
            excludePair = state.currentPair,
        )
    }

    fun setCounts(total: Int, undercoverCount: Int, blankCount: Int): Boolean {
        val state = _uiState.value
        if (state.editing || state.isSaving) return false
        if (total !in 3..20 || undercoverCount !in 1 until total || blankCount < 0 || blankCount >= total - undercoverCount) {
            return false
        }
        _uiState.value = createRound(
            wordPairs = state.wordPairs,
            count = total,
            undercoverCount = undercoverCount,
            blankCount = blankCount,
            round = state.round + 1,
            excludePair = state.currentPair,
        )
        return true
    }

    fun openEditor() {
        val state = _uiState.value
        if (state.editing || state.isSaving || state.revealed) return
        _uiState.value = state.copy(editing = true, errorMessage = null)
    }

    fun saveEditor(entries: List<String>) {
        val state = _uiState.value
        if (!state.editing || state.isSaving) return
        val cleaned = entries.mapNotNull(::normalizeWordPair)
        if (cleaned.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "至少添加一组词对，格式如：卧底词--平民词")
            return
        }
        if (cleaned.size != entries.map(String::trim).filter(String::isNotEmpty).size) {
            _uiState.value = state.copy(errorMessage = "词对需用 -- 分隔，例如：苹果--香蕉")
            return
        }
        _uiState.value = state.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repository.save(cleaned)
                usedPairs.clear()
                val current = _uiState.value
                _uiState.value = createRound(
                    wordPairs = cleaned,
                    count = current.userCount,
                    undercoverCount = current.undercoverCount,
                    blankCount = current.blankCount,
                    round = current.round,
                ).copy(editing = false, isSaving = false, errorMessage = null)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = "保存失败，请重试")
            }
        }
    }

    private fun createRound(
        wordPairs: List<String>,
        count: Int,
        undercoverCount: Int,
        blankCount: Int,
        round: Int,
        excludePair: String? = null,
    ): WhoIsUiState {
        val pair = pickUnusedPair(wordPairs, excludePair)
        val pairWords = pair.split("--").map(String::trim)
        val words = listOf(pairWords.first(), pairWords.getOrElse(1) { pairWords.first() })
        val (undercoverIndices, blankIndices) = assignRoles(count, undercoverCount, blankCount)
        return WhoIsUiState(
            userCount = count,
            undercoverCount = undercoverCount,
            blankCount = blankCount,
            wordPairs = wordPairs,
            currentPair = pair,
            words = words,
            undercoverIndices = undercoverIndices,
            blankIndices = blankIndices,
            round = round,
        )
    }

    private fun pickUnusedPair(wordPairs: List<String>, excludePair: String?): String {
        var pool = wordPairs.filter { it !in usedPairs && it != excludePair }
        if (pool.isEmpty()) {
            usedPairs.clear()
            pool = wordPairs.filter { it != excludePair }.ifEmpty { wordPairs }
        }
        val pair = pool.randomOrNull() ?: "苹果--香蕉"
        usedPairs += pair
        return pair
    }
}
