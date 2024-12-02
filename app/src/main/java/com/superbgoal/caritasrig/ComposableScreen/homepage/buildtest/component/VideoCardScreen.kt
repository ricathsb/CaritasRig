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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.RangeSlider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.superbgoal.caritasrig.data.model.component.GpuBuild
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun VideoCardScreen(navController: NavController) {
    // Load video cards from JSON resource
    val context = LocalContext.current
    val videoCards: List<GpuBuild> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.gpu_build_updated
        )
    }

    // State variables
    var query by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedManufacturers by remember { mutableStateOf<List<String>>(emptyList()) }
    var tdpRange by remember { mutableStateOf(0f..500f) }
    var memoryRange by remember { mutableStateOf(0f..24f) }
    var filteredVideoCards by remember { mutableStateOf(videoCards) }

    // Pagination variables
    val itemsPerPage = 10
    val currentPage = remember { mutableStateOf(0) }
    val totalPages = (filteredVideoCards.size / itemsPerPage) +
            if (filteredVideoCards.size % itemsPerPage == 0) 0 else 1

    // Available manufacturers
    val availableManufacturers = listOf(
        "AMD", "ASRock", "ATI", "Acer", "Asus", "Dell", "Diamond",
        "EVGA", "GALAX", "Gainward", "Gigabyte", "HP", "Inno3D", "Intel",
        "Lenovo", "MAXSUN", "MSI", "Matrox", "NVIDIA", "PNY", "Palit",
        "PowerColor", "Sapphire", "Sparkle", "VisionTek", "XFX", "Zotac"
    )

    // Update filtered cards when query or filter states change
    LaunchedEffect(query, selectedManufacturers, tdpRange, memoryRange) {
        filteredVideoCards = videoCards.filter { gpu ->
            (query.isBlank() || gpu.name.contains(query, ignoreCase = true)) &&
                    (selectedManufacturers.isEmpty() || selectedManufacturers.contains(gpu.manufacturer)) &&
                    (gpu.tdp.toFloat() in tdpRange) &&
                    (gpu.memory in memoryRange)
        }
        // Reset to the first page if filters change
        currentPage.value = 0
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Search bar and filter button
            SearchBarForComponent(
                query = query,
                onQueryChange = { query = it },
                onFilterClick = { showFilterDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Paginated video card list
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                val currentPageItems = filteredVideoCards
                    .drop(currentPage.value * itemsPerPage)
                    .take(itemsPerPage)

                VideoCardList(
                    videoCards = currentPageItems,
                    navController = navController,
                    currentPage = currentPage,
                    totalPages = totalPages,
                    onPreviousPage = { if (currentPage.value > 0) currentPage.value-- },
                    onNextPage = { if (currentPage.value < totalPages - 1) currentPage.value++ }
                )
            }
        }

        // Filter dialog
        if (showFilterDialog) {
            FilterDialog(
                onDismiss = { showFilterDialog = false },
                tdpRange = remember { mutableStateOf(tdpRange) }, // Maintain state
                memoryRange = remember { mutableStateOf(memoryRange) }, // Maintain state
                selectedManufacturers = remember { mutableStateOf(selectedManufacturers) }, // Maintain state
                availableManufacturers = availableManufacturers,
                onApply = { newTdpRange, newMemoryRange, newSelectedManufacturers ->
                    tdpRange = newTdpRange
                    memoryRange = newMemoryRange
                    selectedManufacturers = newSelectedManufacturers
                    showFilterDialog = false
                }
            )
        }
    }
}


@Composable
fun VideoCardList(
    videoCards: List<GpuBuild>,
    navController: NavController,
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
        // Video card items
        items(videoCards) { videoCard ->
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                title = videoCard.name,
                price = videoCard.price,
                details = """
                    Chipset: ${videoCard.chipset}
                    Memory: ${videoCard.memory} GB
                    Core Clock: ${videoCard.coreClock} MHz
                    Boost Clock: ${videoCard.boostClock} MHz
                    Color: ${videoCard.color}
                    Length: ${videoCard.length} mm
                """.trimIndent(),
                isLoading = isLoading.value,
                onFavClick = {
                    savedFavorite(videoCard = videoCard, context = context)
                },
                imageUrl = videoCard.imageUrl,
                onAddClick = {
                    isLoading.value = true
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "videoCard",
                            componentData = videoCard,
                            onSuccess = {
                                isLoading.value = false
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                isLoading.value = false
                                Log.e("VideoCardActivity", "Failed to store Video Card: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        isLoading.value = false
                        Log.e("VideoCardActivity", "Build title is null; unable to store Video Card.")
                    }
                }
            )
        }

        // Pagination buttons as the last item
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
    tdpRange: MutableState<ClosedFloatingPointRange<Float>>,
    memoryRange: MutableState<ClosedFloatingPointRange<Float>>,
    selectedManufacturers: MutableState<List<String>>,
    availableManufacturers: List<String>,
    onApply: (tdpRange: ClosedFloatingPointRange<Float>, memoryRange: ClosedFloatingPointRange<Float>, selectedManufacturers: List<String>) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Filter Video Cards")
        },
        text = {
            Column {
                // TDP range slider
                Text(text = "TDP: ${tdpRange.value.start.toInt()}W - ${tdpRange.value.endInclusive.toInt()}W")
                RangeSlider(
                    value = tdpRange.value,
                    onValueChange = { range ->
                        tdpRange.value = range
                    },
                    valueRange = 0f..500f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Memory range slider
                Text(
                    text = "Memory: ${
                        String.format("%.0f", memoryRange.value.start)
                    } GB - ${
                        String.format("%.0f", memoryRange.value.endInclusive)
                    } GB"
                )
                RangeSlider(
                    value = memoryRange.value,
                    onValueChange = { range ->
                        memoryRange.value = range
                    },
                    valueRange = 0f..24f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Manufacturer dropdown menu
                Text(text = "Manufacturers")
                ManufacturerDropdown(
                    availableManufacturers = availableManufacturers,
                    selectedManufacturers = selectedManufacturers
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    tdpRange.value,
                    memoryRange.value,
                    selectedManufacturers.value
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ManufacturerDropdown(
    availableManufacturers: List<String>,
    selectedManufacturers: MutableState<List<String>>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selectedManufacturers.value.isEmpty()) "Select manufacturers" else selectedManufacturers.value.joinToString(", "),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 200.dp) // Membatasi tinggi maksimum
        ) {
            availableManufacturers.forEach { manufacturer ->
                DropdownMenuItem(onClick = {
                    if (manufacturer in selectedManufacturers.value) {
                        selectedManufacturers.value = selectedManufacturers.value - manufacturer
                    } else {
                        selectedManufacturers.value = selectedManufacturers.value + manufacturer
                    }
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = manufacturer in selectedManufacturers.value,
                            onCheckedChange = null // Handled by DropdownMenuItem
                        )
                        Text(text = manufacturer)
                    }
                }
            }
        }
    }
}