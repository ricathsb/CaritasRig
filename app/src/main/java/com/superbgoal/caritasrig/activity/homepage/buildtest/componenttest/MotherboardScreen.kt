package com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest
import kotlin.math.roundToInt
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.Motherboard
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

@Composable
fun MotherboardScreen(navController: NavController) {
    // Load motherboard data
    val context = LocalContext.current
    val motherboards: List<Motherboard> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.motherboard
        )
    }
    Log.d("MotherboardScreen", "Loaded motherboards: $motherboards")

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredMotherboards by remember { mutableStateOf(motherboards) }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Set background image
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Main content with TopAppBar and MotherboardList
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
                            text = "Motherboard",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                            Log.d("MotherboardScreen", "Back button clicked")
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
                            showFilterDialog = true
                            Log.d("MotherboardScreen", "Filter dialog triggered")
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
                Log.d("MotherboardScreen", "Displaying filtered motherboards: $filteredMotherboards")
                MotherboardList(motherboards = filteredMotherboards, navController)
            }
        }

        // Render FilterDialogMotherboard if the dialog is triggered
        if (showFilterDialog) {
            Log.d("MotherboardScreen", "Rendering FilterDialogMotherboard")
            FilterDialogMotherboard(
                onDismiss = {
                    showFilterDialog = false
                    Log.d("MotherboardScreen", "Filter dialog dismissed")
                },
                onApply = { selectedSockets, maxMemory, memorySlots ->
                    Log.d(
                        "MotherboardScreen",
                        "Filter applied with sockets=$selectedSockets, maxMemory=$maxMemory, memorySlots=$memorySlots"
                    )
                    showFilterDialog = false
                    filteredMotherboards = motherboards.filter { motherboard ->
                        val socketMatch = selectedSockets.isEmpty() || motherboard.socket in selectedSockets
                        val memoryMatch = motherboard.maxMemory <= maxMemory
//                        val slotsMatch = motherboard.memorySlots == memorySlots.toInt()

//                        Log.d("FilterLogic", "Motherboard: ${motherboard.name}, Socket: $socketMatch, Memory: $memoryMatch, Slots: $slotsMatch")
//                        Log.d("FilterLogic", "Slots Match Debug: ${motherboard.memorySlots} == ${memorySlots).toInt()}")

                        socketMatch && memoryMatch
                    }

                    Log.d("MotherboardScreen", "Filtered motherboards: $filteredMotherboards")
                }
            )
        }
    }
}
@Composable
fun FilterDialogMotherboard(
    onDismiss: () -> Unit,
    onApply: (selectedSockets: Set<String>, maxMemory: Int, memorySlots: Int) -> Unit
) {
    val availableSockets = listOf("AM4", "AM5", "LGA1700", "LGA1200")
    val selectedSockets = remember { mutableStateOf(setOf<String>()) }
    val maxMemory = remember { mutableStateOf(192f) }
    val memorySlots = remember { mutableStateOf(4f) }

    AlertDialog(
        onDismissRequest = {
            Log.d("FilterDialog", "Dialog dismissed by clicking outside")
            onDismiss()
        },
        title = { Text("Filter Motherboards") },
        text = {
            Column {
                // Socket Selection
                Text("Socket:")
                availableSockets.forEach { socket ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedSockets.value.contains(socket),
                            onCheckedChange = { isChecked ->
                                selectedSockets.value = if (isChecked) {
                                    selectedSockets.value + socket
                                } else {
                                    selectedSockets.value - socket
                                }
                                Log.d("FilterDialog", "Socket selection updated: $selectedSockets")
                            }
                        )
                        Text(text = socket)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Max Memory Slider
                Text("Max Memory: ${maxMemory.value.toInt()} GB")
                Slider(
                    value = maxMemory.value,
                    onValueChange = {
                        maxMemory.value = it
                        Log.d("FilterDialog", "Max memory updated: ${maxMemory.value.toInt()} GB")
                    },
                    valueRange = 0f..192f,
                    steps = 191
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Memory Slots Slider
//                Text("Memory Slots: ${memorySlots.value.toInt()}")
//                Slider(
//                    value = memorySlots.value,
//                    onValueChange = {
//                        memorySlots.value = it
//                        Log.d("FilterDialog", "Memory slots updated: ${memorySlots.value.toInt()}")
//                    },
//                    valueRange = 2f..4f,
//                    steps = 2
//                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                Log.d(
                    "FilterDialog",
                    "Apply clicked with sockets=${selectedSockets.value}, maxMemory=${maxMemory.value.toInt()}, memorySlots=${memorySlots.value.toInt()}"
                )
                onApply(
                    selectedSockets.value,
                    maxMemory.value.toInt(),
                    memorySlots.value.toInt()
                )
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                Log.d("FilterDialog", "Cancel button clicked")
                onDismiss()
            }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MotherboardList(motherboards: List<Motherboard>, navController: NavController) {
    // Get context from LocalContext
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(motherboards) { motherboard ->
            // Track loading state for each motherboard
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                imageUrl = motherboard.imageUrl, // Image URL for the card
                title = motherboard.name,
                details = "Socket: ${motherboard.socket} | Form Factor: ${motherboard.formFactor} | " +
                        "Max Memory: ${motherboard.maxMemory}GB | Slots: ${motherboard.memorySlots} | Color: ${motherboard.color}",
                context = context,
                component = motherboard,
                isLoading = isLoading.value,
                onAddClick = {
                    // Start loading when the add button is clicked
                    isLoading.value = true
                    Log.d("MotherboardActivity", "Selected Motherboard: ${motherboard.name}")

                    // Get the current user and build title
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    // Save motherboard if buildTitle is available
                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "motherboard", // Specify component type
                            componentData = motherboard, // Pass motherboard data
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                navController.navigateUp()
                                Log.d("MotherboardActivity", "Motherboard ${motherboard.name} saved successfully under build title: $title")
                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("MotherboardActivity", "Failed to store Motherboard under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it } // Update loading state
                        )
                    } ?: run {
                        // Stop loading if buildTitle is null
                        isLoading.value = false
                        Log.e("MotherboardActivity", "Build title is null; unable to store Motherboard.")
                    }
                }
            )
        }
    }
}

