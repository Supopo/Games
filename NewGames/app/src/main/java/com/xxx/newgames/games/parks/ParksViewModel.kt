package com.xxx.newgames.games.parks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ParksUiState(
    val parks: List<String> = emptyList(),
    val current: String = "点击抽一张",
    val revealed: Boolean = false,
    val editing: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class ParksViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ParksRepository(application)
    private var drawOrder = emptyList<String>()
    private var nextIndex = 0
    private val _uiState = MutableStateFlow(ParksUiState(parks = repository.load()))
    val uiState: StateFlow<ParksUiState> = _uiState.asStateFlow()

    init {
        reshuffleOrder(_uiState.value.parks)
    }

    fun shuffle() {
        val state = _uiState.value
        if (state.editing || state.isSaving) return
        reshuffleOrder(state.parks)
        _uiState.value = state.copy(current = "点击抽一张", revealed = false)
    }

    fun draw() {
        val state = _uiState.value
        if (state.editing || state.isSaving) return
        _uiState.value = if (!state.revealed) {
            if (drawOrder.isEmpty()) reshuffleOrder(state.parks)
            val next = drawOrder.getOrNull(nextIndex % drawOrder.size.coerceAtLeast(1))
            nextIndex++
            state.copy(current = next ?: "暂无题目，请先添加", revealed = true)
        } else {
            state.copy(revealed = false, current = "点击抽一张")
        }
    }

    fun openEditor() {
        val state = _uiState.value
        if (state.editing || state.isSaving || state.revealed) return
        _uiState.value = state.copy(editing = true, errorMessage = null)
    }

    fun saveEditor(entries: List<String>) {
        val state = _uiState.value
        if (!state.editing || state.isSaving) return
        val cleaned = entries.map(String::trim).filter(String::isNotEmpty)
        if (cleaned.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "至少添加一条公园题目")
            return
        }
        _uiState.value = state.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repository.save(cleaned)
                reshuffleOrder(cleaned)
                _uiState.value = _uiState.value.copy(
                    parks = cleaned,
                    editing = false,
                    isSaving = false,
                    errorMessage = null,
                    current = "点击抽一张",
                    revealed = false,
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = "保存失败，请重试")
            }
        }
    }

    private fun reshuffleOrder(parks: List<String>) {
        drawOrder = parks.shuffled()
        nextIndex = 0
    }
}
