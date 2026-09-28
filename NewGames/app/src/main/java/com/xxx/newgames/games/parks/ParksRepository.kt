package com.xxx.newgames.games.parks

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

internal class ParksRepository(context: Context) {
    private val assets = AssetRepository(context.applicationContext)
    private val storage = AtomicFile(File(context.filesDir, "parks.json"))
    private val saveMutex = Mutex()

    fun load(): List<String> {
        val stored = runCatching {
            storage.openRead().bufferedReader().use { JSONObject(it.readText()) }
        }.getOrNull()
        return stored?.optJSONArray("parks")?.let { list ->
            (0 until list.length()).map { list.optString(it).trim() }.filter(String::isNotEmpty)
        }?.takeIf { it.isNotEmpty() }
            ?: assets.loadText("parks.txt").split(",").map(String::trim).filter(String::isNotEmpty)
    }

    suspend fun save(parks: List<String>) = withContext(Dispatchers.IO) {
        saveMutex.withLock {
            val json = JSONObject().put("parks", JSONArray(parks))
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
