package com.example.uitest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            DashboardPager(presets = viewModel.presets)
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardPager(presets: List<SnapshotStateList<Module>>) {

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardPage(modules: SnapshotStateList<Module>) {

    var selectedModule: Module? by remember { mutableStateOf(null) }

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

            ModuleView(module = module, onEditRequest = { selectedModule = it })

        }
    }

    if (selectedModule != null) {

        var editedType by remember(selectedModule) {
            mutableStateOf(selectedModule!!.type)
        }

        var editedSpanX by remember(selectedModule) {
            mutableStateOf(selectedModule!!.spanX.toString())
        }

        var editedAspRatio by remember(selectedModule) {
            mutableStateOf(selectedModule!!.aspRatio.toString())
        }

        var moveToIndex by remember { mutableStateOf("") }

        ModalBottomSheet(
            onDismissRequest = { selectedModule = null }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    TextField(
                        value = editedSpanX,
                        onValueChange = { editedSpanX = it },
                        label = { Text("Span") },
                        modifier = Modifier.weight(1f)
                    )

                    TextField(
                        value = editedAspRatio,
                        onValueChange = { editedAspRatio = it },
                        label = { Text("Ratio") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Edit Module")

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    TextField(
                        value = moveToIndex,
                        onValueChange = { moveToIndex = it },
                        label = { Text("Move to index") },
                        singleLine = true
                    )

                    TextField(
                        value = editedType,
                        onValueChange = { editedType = it },
                        label = { Text("Module Type") }
                    )

                }
                Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val currentIndex =
                                modules.indexOfFirst { it.id == selectedModule?.id }

                            if (currentIndex != -1) {

                                val newSpan = editedSpanX.toIntOrNull() ?: modules[currentIndex].spanX
                                val newRatio = editedAspRatio.toFloatOrNull() ?: modules[currentIndex].aspRatio

                                // Update module FIRST
                                modules[currentIndex] =
                                    modules[currentIndex].copy(
                                        type = editedType,
                                        spanX = newSpan,
                                        aspRatio = newRatio
                                    )

                                val targetIndex = moveToIndex.toIntOrNull()

                                if (targetIndex != null &&
                                    targetIndex in 0..modules.lastIndex
                                ) {
                                    moveModule(modules, currentIndex, targetIndex)
                                }
                            }

                            moveToIndex = ""
                            selectedModule = null
                        }
                    ) {
                        Text("Save")
                    }
            }
        }
    }
}


@Composable
fun ModuleView(module: Module, onEditRequest: (Module) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(module.aspRatio)
            .background(Color.DarkGray)
            .combinedClickable(
                onClick = {
                    // normal click (optional)
                },
                onLongClick = {
                    onEditRequest(module)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(text = module.type, color = Color.White)
    }
}
fun moveModule(
    modules: SnapshotStateList<Module>,
    fromIndex: Int,
    toIndex: Int
) {
    val item = modules.removeAt(fromIndex)
    modules.add(toIndex, item)
}
data class Module(
    val id: String,
    val type: String,
    val spanX: Int,
    val aspRatio: Float
)

class DashboardViewModel : ViewModel() {

    val preset1 = mutableStateListOf(
        Module("battery", "sensor", 1, 2f),
        Module("cpu", "sensor", 1, 2f),
        Module("gps", "sensor", 1, 2f),
        Module("video", "video", 4, 1f)
    )

    val preset2 = mutableStateListOf(
        Module("imu", "sensor", 1, 2f),
        Module("altitude", "sensor", 2, 1f),
        Module("logs", "log", 2, 1f)
    )

    val presets = listOf(preset1, preset2)
}