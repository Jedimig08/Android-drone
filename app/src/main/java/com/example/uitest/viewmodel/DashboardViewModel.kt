package com.example.uitest.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.example.uitest.data.LayoutConfig
import com.example.uitest.data.LayoutRepository
import com.example.uitest.data.ModuleConfig
import com.example.uitest.data.Widget
import android.app.Application
import android.net.Uri
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.uitest.data.ModuleData
import com.example.uitest.data.TermuxMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json


class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val termuxClient = TermuxClient()

    var latestLine: String? = null
        private set

    init {
        // Connect and start listening
        viewModelScope.launch {
            termuxClient.connect()
            sendlayout()
            listenForUpdates()
        }
    }

    private suspend fun sendlayout() {
        try {
            val currentLayout = LayoutConfig(
                columns = columns,
                presets = statePresets.toLayoutPresets()
            )

            val jsonString = Json.encodeToString(currentLayout)
            termuxClient.send(jsonString)

            println("Handshake: Layout sent to Termux successfully.")
        } catch (e: Exception) {
            println("Handshake failed: ${e.message}")
        }
    }

    private suspend fun listenForUpdates() {
        val networkJson = Json { ignoreUnknownKeys = true }

        withContext(Dispatchers.IO) {
            try {
                while (true) {
                    val line = termuxClient.receiveLine() ?: break

                    withContext(Dispatchers.Main) {
                        try {
                            // 1. Always parse into the Envelope first
                            val envelope = networkJson.decodeFromString<TermuxMessage>(line)

                            // 2. Decide what to do based on the "type"
                            when (envelope.type) {
                                "LAYOUT" -> {
                                    val newLayout = networkJson.decodeFromString<LayoutConfig>(envelope.content)
                                    columns = newLayout.columns
                                    statePresets = newLayout.toStatePresets()
                                    latestLine = "Layout Refreshed"
                                }
                                "LOG" -> {
                                    latestLine = envelope.content
                                    updateLogModules(envelope.content)
                                }
                            }
                        } catch (e: Exception) {
                            // Fallback for raw strings if the envelope fails
                            latestLine = "Error: Invalid Envelope"
                        }
                    }
                }
            } finally {
                termuxClient.disconnect()
            }
        }
    }
    /*
    private suspend fun listenForUpdates() {

        val jsonParser = Json { ignoreUnknownKeys = true }

        withContext(Dispatchers.IO) {
            try {
                while (true) {

                    //this is just to test soon to be obselete
                    val line = termuxClient.receiveLine() ?: break
                    withContext(Dispatchers.Main) {
                        latestLine = line
                        updateLogModules(line)
                    //
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // 4. Cleanup so you don't leave "ghost" sockets open
                termuxClient.disconnect()
            }
        }
    }
    */
    // just to test the termux connection to change the text on the module,
    // soon to be obselete
    fun updateLogModules(newText: String) {
        statePresets.forEach { preset ->
            for (i in preset.indices) {
                val module = preset[i]
                if (module.type == "log") {
                    // We use .copy() to trigger a recomposition in Compose
                    preset[i] = module.copy(data = ModuleData(data = newText))
                }
            }
        }
    }
    private val repo = LayoutRepository(application)

    var statePresets by mutableStateOf<List<SnapshotStateList<ModuleConfig>>>(emptyList())
        private set

    var columns by mutableIntStateOf(4)

    fun loadLayout() {
        val layout = repo.loadLayout()
        statePresets = layout.toStatePresets()
        columns = layout.columns
    }

    fun saveLayout() {
        val layout = LayoutConfig(
            columns = columns,
            presets = statePresets.toLayoutPresets()
        )

        repo.saveLayout(layout)
    }

    fun importLayout(uri: Uri) {
        repo.importLayout(uri)
        loadLayout()
    }
}

fun LayoutConfig.toStatePresets(): List<SnapshotStateList<ModuleConfig>> {
    return this.presets.values.map { widgetList ->
        widgetList.map { widget ->
            widget.toModuleConfig()
        }.toMutableStateList()
    }
}
fun Widget.toModuleConfig(): ModuleConfig {
    return ModuleConfig(
        id = this.id,
        type = this.type,
        spanX = this.spanX,
        aspRatio = this.aspRatio.toFloat()
    )
}

fun List<SnapshotStateList<ModuleConfig>>.toLayoutPresets(): Map<String, List<Widget>> {
    return this.mapIndexed { index, modules ->
        "preset${index + 1}" to modules.map { it.toWidget() }
    }.toMap()
}

fun ModuleConfig.toWidget(): Widget {
    return Widget(
        id = id,
        type = type,
        spanX = spanX,
        aspRatio = aspRatio.toDouble()
    )
}