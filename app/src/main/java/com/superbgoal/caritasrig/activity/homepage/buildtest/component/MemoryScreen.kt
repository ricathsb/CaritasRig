package com.superbgoal.caritasrig.activity.homepage.buildtest.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.data.model.component.Memory

@Composable
fun MemoryScreen(navController: NavController) {
    // Load memory data
    val context = LocalContext.current
    val memories: List<Memory> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.memory
        )
    }

    // Filter state
    val selectedModules = remember { mutableStateOf(0) }
    val selectedSpeed = remember { mutableStateOf(0) }
    val selectedSockets = remember { mutableStateOf(setOf<Int>()) }

    // State to control the dialog visibility
    val showFilterDialog = remember { mutableStateOf(false) }

    val filteredMemories = remember(selectedModules.value, selectedSpeed.value, selectedSockets.value) {
        memories.filter { memory ->
            (selectedModules.value == 0 || memory.modules >= selectedModules.value) &&
                    (selectedSpeed.value == 0 || memory.speed >= selectedSpeed.value) &&
                    (selectedSockets.value.isEmpty() || memory.socket in selectedSockets.value)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Main content with TopAppBar and MemoryList
        Column {
            TopAppBar(
                backgroundColor = Color.Transparent,
                contentColor = Color.White,
                elevation = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 10.dp)
                    ) {
                        Text(
                            text = "Part Pick",
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Memory Modules",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            showFilterDialog.value = true // Trigger the filter dialog
                        },
                        modifier = Modifier.padding(end = 20.dp, top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Filter"
                        )
                    }
                }
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                MemoryList(filteredMemories, navController)
            }
        }
    }

    // Show the FilterDialog when the state is true
    if (showFilterDialog.value) {
        FilterDialogMemory(
            selectedModules = selectedModules,
            selectedSpeed = selectedSpeed,
            selectedSockets = selectedSockets,
            onDismiss = { showFilterDialog.value = false } // Close the dialog
        )
    }
}

@Composable
fun FilterDialogMemory(
    selectedModules: MutableState<Int>,
    selectedSpeed: MutableState<Int>,
    selectedSockets: MutableState<Set<Int>>,
    onDismiss: () -> Unit
) {
    val availableSockets = listOf(3, 4, 5) // Example socket options DDR3, DDR4, DDR5

    androidx.compose.material.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Memory") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Modules (GB): ${selectedModules.value} GB")
                androidx.compose.material.Slider(
                    value = selectedModules.value.toFloat(),
                    onValueChange = { selectedModules.value = it.toInt() },
                    valueRange = 0f..64f,
                    steps = 63
                )

                Text("Speed (MHz): ${selectedSpeed.value} MHz")
                androidx.compose.material.Slider(
                    value = selectedSpeed.value.toFloat(),
                    onValueChange = { selectedSpeed.value = it.toInt() },
                    valueRange = 0f..8000f,
                    steps = 79
                )

                Text("Socket:")
                availableSockets.forEach { socket ->
                    androidx.compose.material.Checkbox(
                        checked = selectedSockets.value.contains(socket),
                        onCheckedChange = {
                            selectedSockets.value = if (it) {
                                selectedSockets.value + socket
                            } else {
                                selectedSockets.value - socket
                            }
                        }
                    )
                    Text(text = "DDR$socket")
                }
            }
        },
        confirmButton = {
            androidx.compose.material.TextButton(
                onClick = onDismiss
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            androidx.compose.material.TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MemoryList(memories: List<Memory>, navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(memories) { memoryItem ->
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                title = memoryItem.name,
                details = buildString {
                    append("Price: $${memoryItem.price}\n")
                    append("Speed: ${memoryItem.speed} MHz\n")
                    append("Modules: ${memoryItem.modules} GB\n")
                    append("Socket: DDR${memoryItem.socket}\n")
                },
                isLoading = isLoading.value,
                onAddClick = {
                    isLoading.value = true
                    Log.d("MemoryActivity", "Selected Memory: ${memoryItem.name}")

                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "memory",
                            componentData = memoryItem,
                            onSuccess = {
                                isLoading.value = false
                                Log.d("MemoryActivity", "Memory ${memoryItem.name} saved successfully under build title: $title")
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                isLoading.value = false
                                Log.e("MemoryActivity", "Failed to store Memory: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        isLoading.value = false
                        Log.e("MemoryActivity", "Build title is null; unable to store Memory.")
                    }
                }
            )
        }
    }
}
