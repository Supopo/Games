package com.xxx.newgames

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.xxx.newgames.games.truthordare.TruthOrDareChoice
import com.xxx.newgames.games.truthordare.TruthOrDareEditor
import com.xxx.newgames.games.truthordare.TruthOrDareResultStage
import com.xxx.newgames.games.truthordare.TruthOrDareViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TruthOrDareTest {
    @Test
    fun spinCountsAtStartAndOnlyShowsTheSelectedPlayerAfterFinishing() {
        val viewModel = TruthOrDareViewModel(ApplicationProvider.getApplicationContext<Application>())
        val initial = viewModel.uiState.value

        val selectedIndex = checkNotNull(viewModel.startSpin())

        assertTrue(viewModel.uiState.value.isSpinning)
        assertEquals(initial.drawCount + 1, viewModel.uiState.value.drawCount)
        assertNull(viewModel.uiState.value.resultStage)
        assertNull(viewModel.uiState.value.prompt)
        assertNull(viewModel.startSpin())
        viewModel.openEditor(TruthOrDareEditor.PLAYERS)
        assertNull(viewModel.uiState.value.editor)

        viewModel.finishSpin(selectedIndex)

        assertFalse(viewModel.uiState.value.isSpinning)
        assertEquals(initial.players[selectedIndex], viewModel.uiState.value.currentPlayer)
        assertEquals(TruthOrDareResultStage.CHOOSE, viewModel.uiState.value.resultStage)
        assertNull(viewModel.uiState.value.prompt)
    }

    @Test
    fun editorEntryDoesNotDrawAndTruthChoiceUsesTheTruthQuestionBank() {
        val viewModel = TruthOrDareViewModel(ApplicationProvider.getApplicationContext<Application>())
        viewModel.openEditor(TruthOrDareEditor.TRUTHS)

        assertEquals(TruthOrDareEditor.TRUTHS, viewModel.uiState.value.editor)
        assertEquals(0, viewModel.uiState.value.drawCount)
        assertNull(viewModel.uiState.value.resultStage)

        viewModel.closeEditor()
        viewModel.finishSpin(checkNotNull(viewModel.startSpin()))
        viewModel.choose(TruthOrDareChoice.TRUTH)

        val state = viewModel.uiState.value
        assertEquals(TruthOrDareResultStage.QUESTION, state.resultStage)
        assertEquals(TruthOrDareChoice.TRUTH, state.choice)
        assertTrue(state.truths.isEmpty() || state.prompt in state.truths)
    }
}
