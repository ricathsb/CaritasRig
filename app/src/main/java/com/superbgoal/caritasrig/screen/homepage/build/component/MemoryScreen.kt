package com.superbgoal.caritasrig.screen.homepage.build.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.data.model.component.MemoryBuild
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.convertPrice
import com.superbgoal.caritasrig.functions.parseImageUrl
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun MemoryScreen(navController: NavController) {
    val context = LocalContext.current
    val memories: List<MemoryBuild> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.memory_2
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredMemories by remember { mutableStateOf(memories) }
    var searchQuery by remember { mutableStateOf("") }

    // State untuk paginasi
    val itemsPerPage = 10
    val currentPage = remember { mutableStateOf(0) }

    // Perbarui totalPages setelah filter diterapkan
    val totalPages = remember {
        derivedStateOf {
            if (filteredMemories.isEmpty()) 1 else (filteredMemories.size + itemsPerPage - 1) / itemsPerPage
        }
    }

    // State untuk filter
    val selectedSpeeds = remember { mutableStateOf(setOf<String>()) }
    val selectedFormFactors = remember { mutableStateOf(setOf<String>()) }
    val selectedColors = remember { mutableStateOf(setOf<String>()) }
    val selectedCASLatencies = remember { mutableStateOf(setOf<Int>()) }

    // Fungsi untuk menerapkan filter dan pencarian
    fun applyFilters() {
        filteredMemories = memories.filter { memory ->
            val matchesSpeed = selectedSpeeds.value.isEmpty() || memory.speed in selectedSpeeds.value
            val matchesFormFactor = selectedFormFactors.value.isEmpty() || memory.formFactor in selectedFormFactors.value
            val matchesColor = selectedColors.value.isEmpty() || memory.color in selectedColors.value
            val matchesCASLatency = selectedCASLatencies.value.isEmpty() || memory.casLatency in selectedCASLatencies.value
            val matchesSearch = searchQuery.isEmpty() || memory.name.contains(searchQuery, ignoreCase = true)

            matchesSpeed && matchesFormFactor && matchesColor && matchesCASLatency && matchesSearch
        }
        // Reset ke halaman pertama setelah filter diterapkan
        currentPage.value = 0
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

        Column {
            // Search Bar
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
                        applyFilters()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onFilterClick = { showFilterDialog = true }
                )
            }

            // Daftar memory dengan paginasi
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                MemoryList(
                    memories = filteredMemories.drop(currentPage.value * itemsPerPage).take(itemsPerPage),
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

            // Dialog filter
            if (showFilterDialog) {
                MemoryFilterDialog(
                    onDismiss = { showFilterDialog = false },
                    selectedSpeeds = selectedSpeeds,
                    selectedFormFactors = selectedFormFactors,
                    selectedColors = selectedColors,
                    selectedCASLatencies = selectedCASLatencies,
                    onApply = { speeds, formFactors, colors, casLatencies ->
                        selectedSpeeds.value = speeds
                        selectedFormFactors.value = formFactors
                        selectedColors.value = colors
                        selectedCASLatencies.value = casLatencies

                        applyFilters() // Terapkan filter setelah memilih
                        showFilterDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MemoryFilterDialog(
    onDismiss: () -> Unit,
    onApply: (
        selectedSpeeds: Set<String>,
        selectedFormFactors: Set<String>,
        selectedColors: Set<String>,
        selectedCASLatencies: Set<Int>
    ) -> Unit,
    selectedSpeeds: MutableState<Set<String>>,
    selectedFormFactors: MutableState<Set<String>>,
    selectedColors: MutableState<Set<String>>,
    selectedCASLatencies: MutableState<Set<Int>>
) {
    val speeds = listOf("DDR4-3200", "DDR4-3600", "DDR4-4800", "DDR5-5200", "DDR5-5600", "DDR5-6000", "DDR5-6400", "DDR5-6600", "DDR5-7200", "DDR5-8400")
    val formFactors = listOf("288-pin DIMM (DDR4)", "288-pin DIMM (DDR5)")
    val colors = listOf("Black", "Black / Yellow", "Black / Gray", "Black / Silver", "White", "White / Black", "Gray")
    val latencies = listOf(15, 16, 17, 18, 22, 30, 32, 34, 36, 40)

    // State untuk dropdown
    val expandedSpeed = remember { mutableStateOf(false) }
    val expandedFormFactor = remember { mutableStateOf(false) }
    val expandedColor = remember { mutableStateOf(false) }
    val expandedCASLatency = remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Memory Components") },
        text = {
            Column {
                // Dropdown untuk speeds
                DropdownFilter(
                    label = "Speeds",
                    items = speeds,
                    selectedItems = selectedSpeeds.value,
                    onItemToggle = { item ->
                        selectedSpeeds.value = if (selectedSpeeds.value.contains(item)) {
                            selectedSpeeds.value - item
                        } else {
                            selectedSpeeds.value + item
                        }
                    },
                    expanded = expandedSpeed.value,
                    onExpandedChange = { expandedSpeed.value = it }
                )

                // Dropdown untuk form factors
                DropdownFilter(
                    label = "Form Factors",
                    items = formFactors,
                    selectedItems = selectedFormFactors.value,
                    onItemToggle = { item ->
                        selectedFormFactors.value = if (selectedFormFactors.value.contains(item)) {
                            selectedFormFactors.value - item
                        } else {
                            selectedFormFactors.value + item
                        }
                    },
                    expanded = expandedFormFactor.value,
                    onExpandedChange = { expandedFormFactor.value = it }
                )

                // Dropdown untuk colors
                DropdownFilter(
                    label = "Colors",
                    items = colors,
                    selectedItems = selectedColors.value,
                    onItemToggle = { item ->
                        selectedColors.value = if (selectedColors.value.contains(item)) {
                            selectedColors.value - item
                        } else {
                            selectedColors.value + item
                        }
                    },
                    expanded = expandedColor.value,
                    onExpandedChange = { expandedColor.value = it }
                )

                // Dropdown untuk CAS latencies
                DropdownFilter(
                    label = "CAS Latencies",
                    items = latencies.map { it.toString() },
                    selectedItems = selectedCASLatencies.value.map { it.toString() }.toSet(),
                    onItemToggle = { item ->
                        selectedCASLatencies.value = if (selectedCASLatencies.value.contains(item.toInt())) {
                            selectedCASLatencies.value - item.toInt()
                        } else {
                            selectedCASLatencies.value + item.toInt()
                        }
                    },
                    expanded = expandedCASLatency.value,
                    onExpandedChange = { expandedCASLatency.value = it }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    selectedSpeeds.value,
                    selectedFormFactors.value,
                    selectedColors.value,
                    selectedCASLatencies.value
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
fun DropdownFilter(
    label: String,
    items: List<String>,
    selectedItems: Set<String>,
    onItemToggle: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(it) }
    ) {
        OutlinedTextField(
            value = if (selectedItems.isEmpty()) "All" else selectedItems.joinToString(", "),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(label) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items.forEach { item ->
                DropdownMenuItem(onClick = { onItemToggle(item) }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedItems.contains(item),
                            onCheckedChange = null
                        )
                        Text(text = item)
                    }
                }
            }
        }
    }
}



@Composable
fun MemoryList(
    memories: List<MemoryBuild>,
    navController: NavController,
    currentPage: MutableState<Int>,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    // Konteks untuk fungsi tambahan
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Render setiap memory item langsung dari daftar yang diterima
        items(memories) { memoryItem ->
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                price = memoryItem.price,
                imageUrl = parseImageUrl(memoryItem.imageUrl),
                title = memoryItem.name,
                priceConvert = convertPrice(memoryItem.price, context),
                context = context,
                details = """
                    Name: ${memoryItem.name}
                    Price: $${memoryItem.price}
                    Manufacturer: ${memoryItem.manufacturer}
                    Part #: ${memoryItem.partNumber}
                    Speed: ${memoryItem.speed}
                    Form Factor: ${memoryItem.formFactor}
                    Modules: ${memoryItem.modules}
                    Price / GB: ${memoryItem.pricePerGb}
                    Color: ${memoryItem.color}
                    First Word Latency: ${memoryItem.firstWordLatency}
                    CAS Latency: ${memoryItem.casLatency}
                    Voltage: ${memoryItem.voltage}
                    Timing: ${memoryItem.timing}
                    ECC / Registered: ${memoryItem.eccRegistered}
                    Heat Spreader: ${memoryItem.heatSpreader}
                    Specs Number: ${memoryItem.specsNumber}
                    Architecture: ${memoryItem.arsitektur}
                """.trimIndent(),
                isLoading = isLoading.value,
                onFavClick = { savedFavorite(memory = memoryItem, context = context) },
                onAddClick = {
                    isLoading.value = true
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.orEmpty()
                    val buildTitle = BuildManager.getBuildTitle()

                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "memory",
                            componentData = memoryItem,
                            onSuccess = {
                                isLoading.value = false
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                isLoading.value = false
                                Log.e("MemoryList", "Error: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        isLoading.value = false
                        Log.e("MemoryList", "Build title is null; unable to save memory.")
                    }
                }
            )
        }

        // Tombol Pagination
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
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


