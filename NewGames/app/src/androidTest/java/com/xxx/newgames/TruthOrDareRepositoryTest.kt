package com.xxx.newgames

import android.app.Application
import android.content.ContextWrapper
import androidx.test.core.app.ApplicationProvider
import com.xxx.newgames.games.truthordare.TruthOrDareRepository
import java.io.File
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class TruthOrDareRepositoryTest {
    @Test
    fun editedPlayersAndQuestionsSurviveRepositoryReload() = runBlocking {
        val application = ApplicationProvider.getApplicationContext<Application>()
        val directory = File(application.cacheDir, "truth-or-dare-test-" + UUID.randomUUID())
        check(directory.mkdirs())
        val context = object : ContextWrapper(application) {
            override fun getFilesDir(): File = directory
        }
        val repository = TruthOrDareRepository(context)
        val initial = repository.load()
        val edited = initial.copy(
            players = listOf("小明", "小红"),
            truths = listOf("最喜欢的一部电影是什么？"),
            dares = listOf("模仿一种动物"),
        )

        repository.save(edited)

        assertEquals(edited, TruthOrDareRepository(context).load())
    }
}
