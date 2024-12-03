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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RangeSlider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
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
import com.superbgoal.caritasrig.data.model.component.InternalHardDriveBuild
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.parseImageUrl
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun InternalHardDriveScreen(navController: NavController) {
    val context = LocalContext.current
    val allHardDrives: List<InternalHardDriveBuild> = remember {
        loadItemsFromResources(context, R.raw.storage)
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredHardDrives by remember { mutableStateOf(allHardDrives) }
    var searchQuery by remember { mutableStateOf("") }

    val itemsPerPage = 10
    val currentPage = remember { mutableStateOf(0) }

    // Calculate total pages based on filtered items
    val totalPages = remember {
        derivedStateOf {
            maxOf(1, (filteredHardDrives.size / itemsPerPage) + if (filteredHardDrives.size % itemsPerPage == 0) 0 else 1)
        }
    }

    // Ensure currentPage is within bounds
    currentPage.value = currentPage.value.coerceIn(0, totalPages.value - 1)

    // State for filter values
    val capacityRange = remember { mutableStateOf(0f..5000f) } // Default capacity range in GB
    val priceRange = remember { mutableStateOf(0f..1000f) } // Default price range in USD
    val selectedTypes = remember { mutableStateOf(listOf<String>()) } // SSD or HDD
    val selectedInterfaces = remember { mutableStateOf(listOf<String>()) } // M.2 PCIe, SATA, etc.

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
                        filteredHardDrives = allHardDrives.filter { drive ->
                            val capacity = convertCapacityToGB(drive.capacity)
                            Log.d("HardDriveActivity", "Capacity: $capacity")
                            val price = drive.price

                            val isMatchingCapacity = capacity in capacityRange.value.start.toInt()..capacityRange.value.endInclusive.toInt()
                            val isMatchingPrice = price in priceRange.value
                            val isMatchingType = selectedTypes.value.isEmpty() || drive.type in selectedTypes.value
                            val isMatchingSearch = drive.name.contains(query, ignoreCase = true)

                            isMatchingCapacity && isMatchingPrice && isMatchingType && isMatchingSearch
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onFilterClick = { showFilterDialog = true }
                )
            }

            // Paginated list of hard drives
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                InternalHardDriveList(
                    internalHardDrives = filteredHardDrives,
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
                HardDriveFilterDialog(
                    onDismiss = { showFilterDialog = false },
                    capacityRange = capacityRange,
                    priceRange = priceRange,
                    selectedTypes = selectedTypes,
                    onApply = { newCapacityRange, newPriceRange, newTypes ->
                        showFilterDialog = false
                        filteredHardDrives = allHardDrives.filter { drive ->
                            val capacity = convertCapacityToGB(drive.capacity)
                            val price = drive.price

                            // Check filters
                            val isMatchingCapacity = capacity in newCapacityRange.start..newCapacityRange.endInclusive
                            val isMatchingPrice = price in newPriceRange
                            val isMatchingType = newTypes.isEmpty() || drive.type in newTypes

                            // Debug logs
                            Log.d("HardDriveFilter", "Type: ${drive.arsitektur} $isMatchingType")
                            Log.d("HardDriveFilter", "Capacity: $capacity")
                            Log.d("HardDriveFilter", "$isMatchingCapacity $isMatchingPrice $isMatchingType")

                            // Apply filters
                            isMatchingCapacity && isMatchingPrice && isMatchingType
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun InternalHardDriveList(
    internalHardDrives: List<InternalHardDriveBuild>,
    navController: NavController,
    currentPage: MutableState<Int>,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    // Get context from LocalContext
    val context = LocalContext.current

    // Items per page
    val itemsPerPage = 10

    // Calculate the current page's hard drives
    val currentPageHardDrives = internalHardDrives
        .drop(currentPage.value * itemsPerPage) // Skip items for previous pages
        .take(itemsPerPage) // Take items for the current page

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(currentPageHardDrives) { hardDrive ->
            // Track loading state for each hard drive
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                price = hardDrive.price,
                imageUrl = parseImageUrl(hardDrive.imageUrl),
                title = hardDrive.name,
                details = """
    Name: ${hardDrive.name}
    Price: $${hardDrive.price}
    Manufacturer: ${hardDrive.manufacturer}
    Part #: ${hardDrive.partNumber}
    Capacity: ${hardDrive.capacity}
    Price / GB: ${hardDrive.pricePerGB}
    Type: ${hardDrive.type}
    Cache: ${hardDrive.cache}
    Form Factor: ${hardDrive.formFactor}
    Interface: ${hardDrive.interfaceType}
    NVME: ${hardDrive.nvme}
    Architecture: ${hardDrive.arsitektur}
""".trimIndent(),
                component = hardDrive,
                isLoading = isLoading.value,
                onFavClick = {
                    savedFavorite(internalHardDrive = hardDrive, context = context)
                },
                onAddClick = {
                    // Start loading when the add button is clicked
                    isLoading.value = true

                    // Get userId and buildTitle
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "internalHardDrive",
                            componentData = hardDrive,
                            onSuccess = {
                                isLoading.value = false
                                Log.d("HardDriveActivity", "Hard Drive ${hardDrive.name} saved successfully under build title: $title")
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                isLoading.value = false
                                Log.e("HardDriveActivity", "Failed to store Hard Drive under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        isLoading.value = false
                        Log.e("HardDriveActivity", "Build title is null; unable to store Hard Drive.")
                    }
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Blue),
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
fun HardDriveFilterDialog(
    onDismiss: () -> Unit,
    capacityRange: MutableState<ClosedFloatingPointRange<Float>>, // Capacity in GB
    priceRange: MutableState<ClosedFloatingPointRange<Float>>, // Price in USD
    selectedTypes: MutableState<List<String>>, // SSD, HDD (with RPM for HDD)
    onApply: (capacity: IntRange, price: ClosedFloatingPointRange<Double>, types: List<String>) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Filter Hard Drives")
        },
        text = {
            Column {
                // Capacity slider
                Text(text = "Capacity: ${capacityRange.value.start.toInt()} GB - ${capacityRange.value.endInclusive.toInt()} GB")
                RangeSlider(
                    value = capacityRange.value,
                    onValueChange = { range ->
                        capacityRange.value = range
                    },
                    valueRange = 0f..20000f // Assuming max capacity of 20 TB
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Price slider
                Text(
                    text = "Price: $${String.format("%.2f", priceRange.value.start)} - $${String.format("%.2f", priceRange.value.endInclusive)}"
                )
                RangeSlider(
                    value = priceRange.value,
                    onValueChange = { range ->
                        priceRange.value = range
                    },
                    valueRange = 0f..1000f // Assuming max price of $1000
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Type selection checkboxes
                Text(text = "Type")
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "SSD" in selectedTypes.value,
                        onCheckedChange = {
                            if (it) selectedTypes.value = selectedTypes.value + "SSD"
                            else selectedTypes.value = selectedTypes.value - "SSD"
                        }
                    )
                    Text(text = "SSD")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "5400 RPM" in selectedTypes.value,
                        onCheckedChange = {
                            if (it) selectedTypes.value = selectedTypes.value + "5400 RPM"
                            else selectedTypes.value = selectedTypes.value - "5400 RPM"
                        }
                    )
                    Text(text = "HDD 5400 RPM")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "7200 RPM" in selectedTypes.value,
                        onCheckedChange = {
                            if (it) selectedTypes.value = selectedTypes.value + "7200 RPM"
                            else selectedTypes.value = selectedTypes.value - "7200 RPM"
                        }
                    )
                    Text(text = "HDD 7200 RPM")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = "10000 RPM" in selectedTypes.value,
                        onCheckedChange = {
                            if (it) selectedTypes.value = selectedTypes.value + "10000 RPM"
                            else selectedTypes.value = selectedTypes.value - "10000 RPM"
                        }
                    )
                    Text(text = "HDD 10000 RPM")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    capacityRange.value.start.toInt()..capacityRange.value.endInclusive.toInt(),
                    priceRange.value.start.toDouble()..priceRange.value.endInclusive.toDouble(),
                    selectedTypes.value
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

fun convertCapacityToGB(capacity: String): Int {
    return when {
        capacity.contains("TB", ignoreCase = true) -> {
            // Konversi TB ke GB (1 TB = 1000 GB)
            val value = capacity.replace("TB", "").trim().toFloatOrNull() ?: 0f
            (value * 1000).toInt() // Mengalikan dengan 1000 untuk mendapatkan GB
        }
        capacity.contains("GB", ignoreCase = true) -> {
            // Ambil nilai GB langsung
            capacity.replace("GB", "").trim().toIntOrNull() ?: 0
        }
        else -> 0 // Jika tidak ada kapasitas yang terdeteksi, return 0
    }
}