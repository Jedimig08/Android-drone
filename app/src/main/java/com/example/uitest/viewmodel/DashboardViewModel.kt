package com.example.uitest.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.uitest.data.LayoutConfig
import com.example.uitest.data.LayoutRepository
import com.example.uitest.data.ModuleConfig
import com.example.uitest.data.Widget

class   DashboardViewModel(
    private val repo: LayoutRepository = LayoutRepository()
) : ViewModel() {

    var statePresets by mutableStateOf<List<SnapshotStateList<ModuleConfig>>>(emptyList())
        private set

    var columns by mutableStateOf(4)

    fun loadLayout(context: Context) {
        val layout = repo.loadLayout(context)
        statePresets = layout.toStatePresets()
        columns = layout.columns
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
