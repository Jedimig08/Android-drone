package com.example.uitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val presets = listOf(
                listOf(
                    Module("battery", "sensor", 1, 2f),
                    Module("cpu", "sensor", 1, 2f),
                    Module("gps", "sensor", 1, 2f),
                    Module("video", "video", 4, 1f)
                ),
                listOf(
                    Module("imu", "sensor", 1, 2f),
                    Module("altitude", "sensor", 2,1f),
                    Module("logs", "log", 2, 1f)
                )
            )

            // Pass the list of presets to DashboardPager
            DashboardPager(presets = presets)
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardPager(presets: List<List<Module>>) {

    val pagerState = rememberPagerState(
        pageCount = { presets.size }
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { pageIndex ->

        DashboardPage(modules = presets[pageIndex])
    }
}

@Composable
fun DashboardPage(modules: List<Module>) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),          // 4 columns for landscape
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(
            items = modules,
            span = { module ->
                GridItemSpan(module.spanX)
            }
        ) { module ->

            ModuleView(module = module)

        }
    }
}

@Composable
fun ModuleView(module: Module) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(module.asp_ratio) // makes square, can be changed per module
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text(text = module.type, color = Color.White)
    }
}

data class Module(
    val id: String,
    val type: String,
    val spanX: Int,
    val asp_ratio: Float
)