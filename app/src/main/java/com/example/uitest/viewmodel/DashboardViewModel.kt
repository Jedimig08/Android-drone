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
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

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