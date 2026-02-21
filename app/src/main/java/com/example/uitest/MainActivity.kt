package com.example.uitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val presets = listOf<@Composable () -> Unit>(
                { PageContent(color = Color.Red, text = "Preset 1") },
                { PageContent(color = Color.Green, text = "Preset 2") }
            )

            DashboardPager(presets = presets)
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardPager(presets: List<@Composable () -> Unit>) {

    val pagerState = rememberPagerState(
        pageCount = { presets.size }
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->

        presets[pageIndex]()
    }
}

@Composable
fun PageContent(color: Color, text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = Color.White)
    }
}