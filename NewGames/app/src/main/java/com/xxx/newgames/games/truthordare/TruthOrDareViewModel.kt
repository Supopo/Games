package com.xxx.newgames.games.truthordare

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xxx.newgames.data.RandomUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

enum class TruthOrDareEditor(val title: String) {
    PLAYERS("添加玩家"), TRUTHS("添加真心话"), DARES("添加大冒险"),
}

enum class TruthOrDareChoice(val title: String) {
    TRUTH("真心话"), DARE("大冒险"),
}

enum class TruthOrDareResultStage { CHOOSE, QUESTION, REFUSED }

data class TruthOrDareUiState(
    val players: List<String> = (1..6).map { "${it}号玩家" },
    val truths: List<String> = emptyList(),
    val dares: List<String> = emptyList(),
    val drawCount: Int = 0,
    val currentPlayer: String = "",
    val isSpinning: Boolean = false,
    val resultStage: TruthOrDareResultStage? = null,
    val choice: TruthOrDareChoice? = null,
    val prompt: String? = null,
    val editor: TruthOrDareEditor? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

class TruthOrDareViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TruthOrDareRepository(application)
    private val initialContent = repository.load()
    private val _uiState = MutableStateFlow(
        TruthOrDareUiState(
            players = initialContent.players,
            truths = initialContent.truths,
            dares = initialContent.dares,
        ),
    )
    val uiState: StateFlow<TruthOrDareUiState> = _uiState.asStateFlow()
    fun startSpin(): Int? {
        val state = _uiState.value
        if (state.isSpinning || state.editor != null || state.resultStage != null) return null
        val selectedIndex = RandomUtils.randomItem(state.players.indices.toList()) ?: return null
        _uiState.value = state.copy(
            drawCount = state.drawCount + 1,
            isSpinning = true,
        )
        return selectedIndex
    }

    fun finishSpin(selectedIndex: Int) {
        val state = _uiState.value
        if (!state.isSpinning) return
        val player = state.players.getOrNull(selectedIndex) ?: return cancelSpin()
        _uiState.value = state.copy(
            isSpinning = false,
            currentPlayer = player,
            resultStage = TruthOrDareResultStage.CHOOSE,
            choice = null,
            prompt = null,
        )
    }

    fun cancelSpin() {
        _uiState.value = _uiState.value.copy(isSpinning = false)
    }

    fun choose(choice: TruthOrDareChoice) {
        val state = _uiState.value
        if (state.resultStage == null) return
        val prompts = if (choice == TruthOrDareChoice.TRUTH) state.truths else state.dares
        val candidates = prompts.filter { it != state.prompt }.ifEmpty { prompts }
        _uiState.value = state.copy(
            choice = choice,
            resultStage = TruthOrDareResultStage.QUESTION,
            prompt = RandomUtils.randomItem(candidates) ?: "暂无题目，请先添加${choice.title}",
        )
    }

    fun changePrompt() {
        _uiState.value.choice?.let(::choose)
    }

    fun refuse() {
        _uiState.value = _uiState.value.copy(resultStage = TruthOrDareResultStage.REFUSED)
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(resultStage = null, choice = null, prompt = null)
    }

    fun openEditor(editor: TruthOrDareEditor) {
        val state = _uiState.value
        if (state.isSpinning || state.resultStage != null) return
        _uiState.value = state.copy(editor = editor, errorMessage = null)
    }

    fun closeEditor() {
        if (!_uiState.value.isSaving) {
            _uiState.value = _uiState.value.copy(editor = null, errorMessage = null)
        }
    }

    fun saveEditor(entries: List<String>) {
        val state = _uiState.value
        val editor = state.editor ?: return
        if (state.isSaving || state.isSpinning) return
        val cleaned = entries.map(String::trim).filter(String::isNotEmpty)
        if (editor == TruthOrDareEditor.PLAYERS && cleaned.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "至少添加一名玩家")
            return
        }
        val content = TruthOrDareContent(
            players = if (editor == TruthOrDareEditor.PLAYERS) cleaned else state.players,
            truths = if (editor == TruthOrDareEditor.TRUTHS) cleaned else state.truths,
            dares = if (editor == TruthOrDareEditor.DARES) cleaned else state.dares,
        )
        _uiState.value = state.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            try {
                repository.save(content)
                _uiState.value = _uiState.value.copy(
                    players = content.players,
                    truths = content.truths,
                    dares = content.dares,
                    editor = null,
                    isSaving = false,
                    errorMessage = null,
                )
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false, errorMessage = "保存失败，请重试")
            }
        }
    }
}
