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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RangeSlider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
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
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.parseImageUrl
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun CpuCoolerScreen(navController: NavController) {
    val context = LocalContext.current
    val cpuCoolers: List<CpuCoolerBuild> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.cpucooler_processed
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredCpuCoolers by remember { mutableStateOf(cpuCoolers) }
    var searchQuery by remember { mutableStateOf("") }

    // State for pagination
    val itemsPerPage = 10
    val currentPage = remember { mutableStateOf(0) }
    var totalPages = remember { mutableStateOf((filteredCpuCoolers.size / itemsPerPage) + if (filteredCpuCoolers.size % itemsPerPage == 0) 0 else 1) }
Log.d("CpuCoolerActivity", "Total pages: ${totalPages.value}")
    // State for filter values
    val priceRange = remember { mutableStateOf(0f..500f) }
    val noiseLevelRange = remember { mutableStateOf(10f..50f) }
    val selectedColors = remember { mutableStateOf(listOf<String>()) }
    val selectedSocket = remember { mutableStateOf(listOf<String>()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column {
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
                        // Apply search and filter simultaneously
                        filteredCpuCoolers = cpuCoolers.filter { cooler ->
                            val isMatchingSearch = cooler.name.contains(query, ignoreCase = true)
                            isMatchingSearch &&
                                    (cooler.price in priceRange.value) &&
                                    (cooler.noiseLevel.split(" - ").map { it.replace(" dB", "").toFloat() }.let { it[0] in noiseLevelRange.value }) &&
                                    (selectedColors.value.isEmpty() || cooler.color in selectedColors.value) &&
                                    (selectedSocket.value.isEmpty() || selectedSocket.value.any { socket -> cooler.cpuSocket.contains(socket) })
                        }

                        // Update totalPages after filtering
                        totalPages.value = (filteredCpuCoolers.size / itemsPerPage) + if (filteredCpuCoolers.size % itemsPerPage == 0) 0 else 1

                        // Adjust currentPage if it's greater than totalPages after filtering
                        if (currentPage.value >= totalPages.value) {
                            currentPage.value = totalPages.value - 1
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onFilterClick = { showFilterDialog = true }
                )
            }

            // Paginated list of CPU coolers
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                CpuCoolerList(
                    cpuCoolers = filteredCpuCoolers,
                    navController = navController,
                    currentPage = currentPage,
                    totalPages = totalPages.value,
                    onPreviousPage = {
                        if (currentPage.value > 0) currentPage.value--
                    },
                    onNextPage = {
                        if (currentPage.value < totalPages.value - 1) currentPage.value++
                    }
                )
            }

            // Show filter dialog
            if (showFilterDialog) {
                CpuCoolerFilterDialog(
                    onDismiss = { showFilterDialog = false },
                    priceRange = priceRange,
                    noiseLevelRange = noiseLevelRange,
                    selectedColors = selectedColors,
                    selectedSocket = selectedSocket,
                    onApply = { newPriceRange, newNoiseLevelRange, newColors, newSocket ->
                        filteredCpuCoolers = cpuCoolers.filter { cooler ->
                            val noiseRange = cooler.noiseLevel.split(" - ").map { it.replace(" dB", "").toFloat() }
                            val coolerMinNoise = noiseRange[0]
                            val coolerMaxNoise = if (noiseRange.size > 1) noiseRange[1] else coolerMinNoise
                            val isWithinRange = coolerMinNoise >= newNoiseLevelRange.start && coolerMaxNoise <= newNoiseLevelRange.endInclusive

                            (cooler.price in newPriceRange) &&
                                    isWithinRange &&
                                    (newColors.isEmpty() || cooler.color in newColors) &&
                                    (newSocket.isEmpty() || newSocket.any { socket -> cooler.cpuSocket.contains(socket) })
                        }

                        // Update totalPages after applying the filter
                        totalPages.value = (filteredCpuCoolers.size / itemsPerPage) + if (filteredCpuCoolers.size % itemsPerPage == 0) 0 else 1

                        // Adjust currentPage if it's greater than totalPages after filtering
                        if (currentPage.value >= totalPages.value) {
                            currentPage.value = totalPages.value - 1
                        }
                        showFilterDialog = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CpuCoolerFilterDialog(
    onDismiss: () -> Unit,
    priceRange: MutableState<ClosedFloatingPointRange<Float>>,
    noiseLevelRange: MutableState<ClosedFloatingPointRange<Float>>,
    selectedColors: MutableState<List<String>>,
    selectedSocket: MutableState<List<String>>,
    onApply: (price: ClosedFloatingPointRange<Double>, noiseLevel: ClosedFloatingPointRange<Double>, selectedColors: List<String>, selectedSocket: List<String>) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Filter CPU Coolers")
        },
        text = {
            Column {
                // Price slider
                Text(text = "Price: ${String.format("$%.2f", priceRange.value.start)} - ${String.format("$%.2f", priceRange.value.endInclusive)}")
                RangeSlider(
                    value = priceRange.value,
                    onValueChange = { range -> priceRange.value = range },
                    valueRange = 0f..500f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Noise level slider
                Text(text = "Noise Level: ${String.format("%.1f", noiseLevelRange.value.start)} - ${String.format("%.1f", noiseLevelRange.value.endInclusive)} dB")
                RangeSlider(
                    value = noiseLevelRange.value,
                    onValueChange = { range -> noiseLevelRange.value = range },
                    valueRange = 10f..50f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color selection checkboxes
                Text(text = "Select Colors")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "Black" in selectedColors.value,
                        onCheckedChange = {
                            if (it) selectedColors.value = selectedColors.value + "Black"
                            else selectedColors.value = selectedColors.value - "Black"
                        }
                    )
                    Text(text = "Black")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "White" in selectedColors.value,
                        onCheckedChange = {
                            if (it) selectedColors.value = selectedColors.value + "White"
                            else selectedColors.value = selectedColors.value - "White"
                        }
                    )
                    Text(text = "White")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Socket selection checkboxes
                Text(text = "Select CPU Sockets")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "AM4" in selectedSocket.value,
                        onCheckedChange = {
                            if (it) selectedSocket.value = selectedSocket.value + "AM4"
                            else selectedSocket.value = selectedSocket.value - "AM4"
                        }
                    )
                    Text(text = "AM4")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "LGA1700" in selectedSocket.value,
                        onCheckedChange = {
                            if (it) selectedSocket.value = selectedSocket.value + "LGA1700"
                            else selectedSocket.value = selectedSocket.value - "LGA1700"
                        }
                    )
                    Text(text = "LGA1700")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    priceRange.value.start.toDouble()..priceRange.value.endInclusive.toDouble(),
                    noiseLevelRange.value.start.toDouble()..noiseLevelRange.value.endInclusive.toDouble(),
                    selectedColors.value,
                    selectedSocket.value
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


@Composable
fun CpuCoolerList(
    cpuCoolers: List<CpuCoolerBuild>,
    navController: NavController,
    currentPage: MutableState<Int>,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    // Get context from LocalContext
    val context = LocalContext.current

    // Calculate the current page's coolers
    val itemsPerPage = 10
    val currentPageCoolers = cpuCoolers
        .drop(currentPage.value * itemsPerPage) // Skip items from previous pages
        .take(itemsPerPage) // Limit items for the current page

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(currentPageCoolers) { cooler ->
            // Track loading state for each cooler
            val isLoading = remember { mutableStateOf(false) }

            // Use ComponentCard for each cooler
            ComponentCard(
                imageUrl = parseImageUrl(cooler.imageUrl),
                price = cooler.price,
                title = cooler.name,
                details = """
    Name: ${cooler.name}
    Price: $${cooler.price}
    Manufacturer: ${cooler.manufacturer}
    Model: ${cooler.model}
    Part #: ${cooler.partNumber}
    Fan RPM: ${cooler.fanRpm}
    Noise Level: ${cooler.noiseLevel} dB
    Color: ${cooler.color}
    Height: ${cooler.height}mm
    CPU Socket: ${cooler.cpuSocket}
    Water Cooled: ${cooler.waterCooled}
    Fanless: ${cooler.fanless}
    Specs Number: ${cooler.specsNumber}
""".trimIndent(),
                component = cooler,
                isLoading = isLoading.value,
                navController = navController,
                onAddClick = {
                    // Start loading when the add button is clicked
                    isLoading.value = true
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()

                    val buildTitle = BuildManager.getBuildTitle()

                    buildTitle?.let { title ->
                        // Save the component to the database
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "cpuCooler",
                            componentData = cooler,
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                Log.d("CpuCoolerActivity", "Cpu Cooler ${cooler.name} saved successfully under build title: $title")
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("CpuCoolerActivity", "Failed to store CPU cooler under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        // Stop loading if buildTitle is null
                        isLoading.value = false
                        Log.e("CpuCoolerActivity", "Build title is null; unable to store CPU cooler.")
                    }
                },
                onFavClick = {
                    savedFavorite(cpuCooler = cooler, context = context)
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





