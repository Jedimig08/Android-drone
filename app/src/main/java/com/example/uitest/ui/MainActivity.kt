package com.example.uitest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import com.example.uitest.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            LaunchedEffect(Unit) {
                viewModel.loadLayout()
            }

            DashboardPager(presets = viewModel.statePresets)
        }
    }
}