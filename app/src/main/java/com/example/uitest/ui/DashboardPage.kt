package com.example.uitest.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.uitest.data.ModuleConfig
import com.example.uitest.util.moveModule
import com.example.uitest.viewmodel.DashboardViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardPage(
    modules: SnapshotStateList<ModuleConfig>,
    viewModel: DashboardViewModel = viewModel()
) {

    var selectedModule: ModuleConfig? by remember { mutableStateOf(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.importLayout(it)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadLayout()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(viewModel.columns),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.run { spacedBy(8.dp) },
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val currentIndex =
                                modules.indexOfFirst { it.id == selectedModule?.id }

                            if (currentIndex != -1) {

                                val newSpan =
                                    editedSpanX.toIntOrNull() ?: modules[currentIndex].spanX
                                val newRatio =
                                    editedAspRatio.toFloatOrNull() ?: modules[currentIndex].aspRatio

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
                        Text("Try")
                    }

                    Button(
                        onClick = {
                            viewModel.saveLayout()
                        }
                    ){
                        Text("Save")
                    }

                    Button(
                        onClick = {
                            launcher.launch("application/json")
                        }
                    ){
                        Text("Load Layout")
                    }
                }
            }
        }
    }
}


@Composable
fun ModuleView(
    module: ModuleConfig,
    onEditRequest: (ModuleConfig) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(module.aspRatio)
            .background(module.color)
            .combinedClickable(
                onClick = { },
                onLongClick = { onEditRequest(module) }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (module.data != null){
            module.data.data
        } else {
            module.type
        }?.let {
            Text(
                text = it,
                color = Color.White
            )
        }
    }
}