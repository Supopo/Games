package com.xxx.newgames.games.truthordare

import android.content.Context
import android.util.AtomicFile
import com.xxx.newgames.data.AssetRepository
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

internal data class TruthOrDareContent(
    val players: List<String>,
    val truths: List<String>,
    val dares: List<String>,
)

internal class TruthOrDareRepository(context: Context) {
    private val assets = AssetRepository(context.applicationContext)
    private val storage = AtomicFile(File(context.filesDir, "truth_or_dare.json"))
    private val saveMutex = Mutex()

    fun load(): TruthOrDareContent {
        val stored = runCatching {
            storage.openRead().bufferedReader().use { JSONObject(it.readText()) }
        }.getOrNull()
        val players = stored.readList("players")?.takeIf { it.isNotEmpty() }
            ?: (1..6).map { "${it}号玩家" }
        return TruthOrDareContent(
            players = players,
            truths = stored.readList("truths") ?: loadPrompts("qua.txt", "?"),
            dares = stored.readList("dares") ?: loadPrompts("do.txt", "~"),
        )
    }

    suspend fun save(content: TruthOrDareContent) = withContext(Dispatchers.IO) {
        saveMutex.withLock {
            val json = JSONObject()
                .put("players", JSONArray(content.players))
                .put("truths", JSONArray(content.truths))
                .put("dares", JSONArray(content.dares))
            val output = storage.startWrite()
            try {
                output.write(json.toString().toByteArray(Charsets.UTF_8))
                storage.finishWrite(output)
            } catch (error: Exception) {
                storage.failWrite(output)
                throw error
            }
        }
    }

    private fun loadPrompts(name: String, separator: String): List<String> =
        assets.loadText(name).split(separator).map(String::trim).filter(String::isNotEmpty)

    private fun JSONObject?.readList(key: String): List<String>? =
        this?.optJSONArray(key)?.let { list ->
            (0 until list.length()).map { list.optString(it).trim() }.filter(String::isNotEmpty)
        }
}
