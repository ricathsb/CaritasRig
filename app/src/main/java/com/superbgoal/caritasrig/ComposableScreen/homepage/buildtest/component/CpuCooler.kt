package com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.CpuCoolerBuild
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun CpuCoolerScreen(navController: NavController) {
    val context = LocalContext.current
    val cpuCoolers: List<CpuCoolerBuild> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.cpucooler_2
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredCpuCoolers by remember { mutableStateOf(cpuCoolers) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

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
                            text = "CPU Coolers",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFilterDialog = true },
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
                CpuCoolerList(filteredCpuCoolers, navController)
            }
        }

//        if (showFilterDialog) {
//            CpuCoolerFilterDialog(
//                onDismiss = { showFilterDialog = false },
//                onApply = { selectedSize, selectedColor, maxNoiseLevel ->
//                    showFilterDialog = false
//                    filteredCpuCoolers = cpuCoolers.filter { cooler ->
//                        (selectedSize == "All" || cooler.size == selectedSize.toIntOrNull()) &&
//                                (selectedColor == "All" || cooler.color.equals(selectedColor, ignoreCase = true)) &&
//                                (maxNoiseLevel == "All" || cooler.noise_level <= maxNoiseLevel.toDoubleOrNull() ?: Double.MAX_VALUE)
//                    }
//                }
//            )
//        }
    }
}

@Composable
fun CpuCoolerFilterDialog(
    onDismiss: () -> Unit,
    onApply: (selectedSize: String, selectedColor: String, maxNoiseLevel: String) -> Unit
) {
    val sizes = listOf("All", "120", "240", "360")  // Common sizes for coolers
    val colors = listOf("All", "Black", "White")
    val noiseLevels = listOf("All", "20", "30", "40")  // Noise levels in dB

    var selectedSize by remember { mutableStateOf("All") }
    var selectedColor by remember { mutableStateOf("All") }
    var maxNoiseLevel by remember { mutableStateOf("All") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter CPU Coolers") },
        text = {
            Column {
                // Size selection
                Text("Size (mm):")
                sizes.forEach { size ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedSize == size,
                            onCheckedChange = { if (it) selectedSize = size }
                        )
                        Text(text = size)
                    }
                }

                // Color selection
                Text("Color:")
                colors.forEach { color ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedColor == color,
                            onCheckedChange = { if (it) selectedColor = color }
                        )
                        Text(text = color)
                    }
                }

                // Noise level selection
                Text("Max Noise Level (dB):")
                noiseLevels.forEach { noiseLevel ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = maxNoiseLevel == noiseLevel,
                            onCheckedChange = { if (it) maxNoiseLevel = noiseLevel }
                        )
                        Text(text = noiseLevel)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(selectedSize, selectedColor, maxNoiseLevel)
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CpuCoolerList(cpuCoolers: List<CpuCoolerBuild>,navController: NavController) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cpuCoolers) { coolerItem ->
            // Track loading state for each CPU Cooler
            val isLoading = remember { mutableStateOf(false) }

            // Use ComponentCard for each CPU Cooler
            ComponentCard(
                imageUrl = coolerItem.imageUrl,
                title = coolerItem.name,
                details = "Price: $${coolerItem.price} | Size: ${coolerItem.height}mm | Color: ${coolerItem.color} | " +
                        "RPM: ${coolerItem.fanRpm} | Noise Level: ${coolerItem.noiseLevel} dB",
                // Passing context from LocalContext
                component = coolerItem,
                isLoading = isLoading.value,
                onFavClick = {
                    savedFavorite(cpuCooler = coolerItem, context = context)
                },
                onAddClick = {
                    // Start loading when the add button is clicked
                    isLoading.value = true
                    Log.d("CpuCoolerActivity", "Selected CPU Cooler: ${coolerItem.name}")

                    // Get the current user and build title
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    // Save CPU Cooler if buildTitle is available
                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "cpucooler", // Specify component type
                            componentData = coolerItem, // Pass CPU Cooler data
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                navController.navigateUp()
                                Log.d("CpuCoolerActivity", "CPU Cooler ${coolerItem.name} saved successfully under build title: $title")
                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("CpuCoolerActivity", "Failed to store CPU Cooler under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it },


                        )
                    } ?: run {
                        // Stop loading if buildTitle is nulla
                        isLoading.value = false
                        Log.e("CpuCoolerActivity", "Build title is null; unable to store CPU Cooler.")
                    }
                }
            )
        }
    }
}
