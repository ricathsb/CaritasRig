package com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest.component

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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RangeSlider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.ProcessorTrial
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.functions.parseImageUrl
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun CpuScreen(navController: NavController) {
    val context = LocalContext.current

    val processorTrial: List<ProcessorTrial> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.processor_build
        )
    }

    // Variables to handle filtered processors, search query, and pagination
    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredProcessors by remember { mutableStateOf(processorTrial) }
    var searchQuery by remember { mutableStateOf("") }

    // Pagination state
    val itemsPerPage = 15
    val currentPage = remember { mutableStateOf(0) }
    val totalPages = (filteredProcessors.size / itemsPerPage) + if (filteredProcessors.size % itemsPerPage == 0) 0 else 1

    // Core and Clock Range filters
    val coreCountRange = remember { mutableStateOf(0f..16f) }
    val coreClockRange = remember { mutableStateOf(1.0f..5.0f) }
    val boostClockRange = remember { mutableStateOf(1.0f..5.0f) }
    val selectedBrands = remember { mutableStateOf(listOf("AMD", "Intel")) }

    // Handle Search Bar and Filter Dialog
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search Bar and Filter Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchBarForComponent(
                    query = searchQuery,
                    onQueryChange = { query ->
                        searchQuery = query
                        // Apply search filter and update filtered processors
                        filteredProcessors = processorTrial.filter { processor ->
                            processor.name.contains(query, ignoreCase = true) &&
                                    (selectedBrands.value.isEmpty() || selectedBrands.value.any { processor.name.contains(it, ignoreCase = true) })
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onFilterClick = { showFilterDialog = true }
                )
            }

            // List of processors
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                // Paginated list
                val currentPageItems = filteredProcessors
                    .drop(currentPage.value * itemsPerPage)
                    .take(itemsPerPage)

                ProcessorList(
                    navController = navController,
                    processorTrial = currentPageItems,
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPreviousPage = { if (currentPage.value > 0) currentPage.value-- },
                    onNextPage = { if (currentPage.value < totalPages - 1) currentPage.value++ }
                )
            }
        }

        // Show filter dialog
        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                coreCountRange = coreCountRange,
                coreClockRange = coreClockRange,
                boostClockRange = boostClockRange,
                selectedBrands = selectedBrands,
                onApply = { coreCount, coreClock, boostClock, brands ->
                    showFilterDialog = false
                    // Apply filters to the original processor list
                    filteredProcessors = processorTrial.filter { processor ->
                        processor.coreCount in coreCount &&
                                processor.performanceCoreClock in coreClock &&
                                processor.performanceCoreBoostClock in boostClock &&
                                brands.any { processor.name.contains(it, ignoreCase = true) }
                    }
                }
            )
        }
    }
}

@Composable
fun ProcessorList(
    navController: NavController,
    processorTrial: List<ProcessorTrial>,
    currentPage: MutableState<Int>,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Processor items
        items(processorTrial) { processor ->
            val isLoading = remember { mutableStateOf(false) }
            ComponentCard(
                imageUrl = parseImageUrl(processor.imageUrl),
                title = processor.name,
                price = processor.price,
                details = """
                    Cores: ${processor.coreCount} cores
                    L3 Cache: ${processor.l3Cache} GHz
                    Core Clock: ${processor.performanceCoreClock} GHz
                    Core Boost Clock: ${processor.performanceCoreBoostClock} GHz
                    Efficiency Core Clock: ${processor.efficiencyCoreClock ?: "N/A"} GHz
                    Efficiency Core Boost Clock: ${processor.efficiencyCoreBoostClock ?: "N/A"} GHz
                    L2 Cache: ${processor.l2Cache}
                    TDP: ${processor.tdp}
                    Socket : ${processor.socket}
                    Integrated Graphics: ${processor.integratedGraphics}
                    Max Supported Memory: ${processor.maxSupportedMemory}
                    ECC Support: ${processor.eccSupport}
                    Includes CPU Cooler: ${processor.includesCpuCooler}
                    Simultaneous Multithreading: ${if (processor.smt) "Yes" else "No"}
                """.trimIndent(),
                isLoading = isLoading.value,
                onAddClick = {
                    isLoading.value = true
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()
                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "processor",
                            componentData = processor,
                            onSuccess = {
                                isLoading.value = false
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                isLoading.value = false
                            },
                            onLoading = { isLoading.value = it },
                        )
                    } ?: run {
                        isLoading.value = false
                        Log.e("BuildActivity", "Build title is null; unable to store CPU.")
                    }
                },
                navController = navController,
                onFavClick = {
                    savedFavorite(processor = processor, context = context)
                }
            )
        }

        // Pagination Buttons as the last item
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onPreviousPage,
                    enabled = currentPage.value > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("Previous", color = Color.White)
                }

                Text(
                    text = "${currentPage.value + 1} / $totalPages",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                Button(
                    onClick = onNextPage,
                    enabled = currentPage.value < totalPages - 1,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Next", color = Color.White)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    coreCountRange: MutableState<ClosedFloatingPointRange<Float>>,
    coreClockRange: MutableState<ClosedFloatingPointRange<Float>>,
    boostClockRange: MutableState<ClosedFloatingPointRange<Float>>,
    selectedBrands: MutableState<List<String>>,
    onApply: (coreCount: IntRange, coreClock: ClosedFloatingPointRange<Double>, boostClock: ClosedFloatingPointRange<Double>, selectedBrands: List<String>) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Filter CPUs")
        },
        text = {
            Column {
                // Core count slider
                Text(text = "Core Count: ${coreCountRange.value.start.toInt()} - ${coreCountRange.value.endInclusive.toInt()}")
                RangeSlider(
                    value = coreCountRange.value,
                    onValueChange = { range ->
                        coreCountRange.value = range
                    },
                    valueRange = 0f..16f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Core clock slider
                Text(
                    text = "Core Clock: ${
                        String.format("%.2f", coreClockRange.value.start)
                    } GHz - ${
                        String.format("%.2f", coreClockRange.value.endInclusive)
                    } GHz"
                )
                RangeSlider(
                    value = coreClockRange.value,
                    onValueChange = { range ->
                        coreClockRange.value = range
                    },
                    valueRange = 1.0f..5.0f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Boost clock slider
                Text(
                    text = "Boost Clock: ${
                        String.format("%.2f", boostClockRange.value.start)
                    } GHz - ${
                        String.format("%.2f", boostClockRange.value.endInclusive)
                    } GHz"
                )
                RangeSlider(
                    value = boostClockRange.value,
                    onValueChange = { range ->
                        boostClockRange.value = range
                    },
                    valueRange = 1.0f..5.0f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Brand selection checkboxes
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "AMD" in selectedBrands.value,
                        onCheckedChange = {
                            if (it) selectedBrands.value = selectedBrands.value + "AMD"
                            else selectedBrands.value = selectedBrands.value - "AMD"
                        }
                    )
                    Text(text = "AMD")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "Intel" in selectedBrands.value,
                        onCheckedChange = {
                            if (it) selectedBrands.value = selectedBrands.value + "Intel"
                            else selectedBrands.value = selectedBrands.value - "Intel"
                        }
                    )
                    Text(text = "Intel")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    coreCountRange.value.start.toInt()..coreCountRange.value.endInclusive.toInt(),
                    coreClockRange.value.start.toDouble()..coreClockRange.value.endInclusive.toDouble(),
                    boostClockRange.value.start.toDouble()..boostClockRange.value.endInclusive.toDouble(),
                    selectedBrands.value
                )
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