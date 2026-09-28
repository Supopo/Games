package com.xxx.newgames.games.whosis

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

internal class WhoIsRepository(context: Context) {
    private val assets = AssetRepository(context.applicationContext)
    private val storage = AtomicFile(File(context.filesDir, "who_is.json"))
    private val saveMutex = Mutex()

    fun load(): List<String> {
        val stored = runCatching {
            storage.openRead().bufferedReader().use { JSONObject(it.readText()) }
        }.getOrNull()
        return stored?.optJSONArray("words")?.let { list ->
            (0 until list.length()).map { list.optString(it).trim() }.filter(String::isNotEmpty)
        }?.takeIf { it.isNotEmpty() }
            ?: assets.loadText("words.txt").split(",").map(String::trim).filter(String::isNotEmpty)
    }

    suspend fun save(words: List<String>) = withContext(Dispatchers.IO) {
        saveMutex.withLock {
            val json = JSONObject().put("words", JSONArray(words))
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
}

internal fun normalizeWordPair(raw: String): String? {
    val parts = raw.split("--").map(String::trim).filter(String::isNotEmpty)
    if (parts.size < 2) return null
    return "${parts[0]}--${parts[1]}"
}
