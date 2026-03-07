package com.example.uitest.UI

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import com.example.uitest.data.ModuleConfig

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardPager(presets: List<SnapshotStateList<ModuleConfig>>) {

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