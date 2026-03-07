package com.example.uitest.viewmodel

import com.example.uitest.data.ModuleConfig
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel


class DashboardViewModel : ViewModel() {

    val preset1 = mutableStateListOf(
        ModuleConfig("battery", "sensor", 1, 2f),
        ModuleConfig("cpu", "sensor", 1, 2f),
        ModuleConfig("gps", "sensor", 1, 2f),
        ModuleConfig("video", "video", 4, 1f)
    )

    val preset2 = mutableStateListOf(
        ModuleConfig("imu", "sensor", 1, 2f),
        ModuleConfig("altitude", "sensor", 2, 1f),
        ModuleConfig("logs", "log", 2, 1f)
    )

    val presets = listOf(preset1, preset2)
}