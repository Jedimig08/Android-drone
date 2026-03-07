package com.example.uitest.data

import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Widget(
    val id: String,
    val type: String,
    val spanX: Int,
    val aspRatio: Int
)

@Serializable
data class LayoutConfig(
    val layoutfile: String,
    val columns: Int,
    val presets: Map<String, List<Widget>>
)

class LayoutRepository {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun loadLayout(context: Context): LayoutConfig {
        val jsonString = context.assets.open("layout.json")
            .bufferedReader()
            .use { it.readText() }

        return json.decodeFromString<LayoutConfig>(jsonString)
    }
}

