package com.example.uitest.data

import androidx.compose.ui.graphics.Color
data class ModuleConfig(
    val id: String,
    val type: String,
    val spanX: Int,
    val aspRatio: Float,
    val color: Color = Color.DarkGray,
    val data: ModuleData? = null
)

data class ModuleData(
    val data: String?
)