package com.xxx.newgames.data

import android.content.Context

class AssetRepository(private val context: Context) {
    fun loadText(assetName: String): String = context.assets
        .open(assetName)
        .bufferedReader()
        .use { it.readText() }

    fun loadLines(assetName: String): List<String> = context.assets
        .open(assetName)
        .bufferedReader()
        .useLines { lines -> lines.map(String::trim).filter(String::isNotEmpty).toList() }
}
