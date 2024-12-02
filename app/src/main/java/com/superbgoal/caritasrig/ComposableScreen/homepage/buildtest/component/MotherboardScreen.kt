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
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RangeSlider
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.superbgoal.caritasrig.data.model.component.MotherboardBuild
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun MotherboardScreen(navController: NavController) {
    val context = LocalContext.current
    val motherboards: List<MotherboardBuild> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.motherboard_2
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredMotherboards by remember { mutableStateOf(motherboards) }
    var searchQuery by remember { mutableStateOf("") }

    // State untuk paginasi
    val itemsPerPage = 10
    val currentPage = remember { mutableStateOf(0) }
    val totalPages = remember { derivedStateOf { (filteredMotherboards.size + itemsPerPage - 1) / itemsPerPage } }

    // State untuk filter
    val priceRange = remember { mutableStateOf(0f..1000f) }
    val selectedColors = remember { mutableStateOf(listOf<String>()) }
    val selectedSocket = remember { mutableStateOf(listOf<String>()) }
    val selectedFormFactors = remember { mutableStateOf(listOf<String>()) }
    val selectedMemoryTypes = remember { mutableStateOf(listOf<String>()) }

    // Fungsi untuk menerapkan filter dan pencarian
    fun applyFilters() {
        filteredMotherboards = motherboards.filter { motherboard ->
            val matchesPrice = motherboard.price in priceRange.value
            val matchesColor = selectedColors.value.isEmpty() || motherboard.color in selectedColors.value
            val matchesSocket = selectedSocket.value.isEmpty() || motherboard.socketCpu in selectedSocket.value
            val matchesFormFactor = selectedFormFactors.value.isEmpty() || motherboard.formFactor in selectedFormFactors.value
            val matchesMemoryType = selectedMemoryTypes.value.isEmpty() || motherboard.memoryType in selectedMemoryTypes.value
            val matchesSearch = searchQuery.isEmpty() || motherboard.name.contains(searchQuery, ignoreCase = true)

            matchesPrice && matchesColor && matchesSocket && matchesFormFactor && matchesMemoryType && matchesSearch
        }
        currentPage.value = 0  // Reset ke halaman pertama setelah filter diterapkan
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

            // Daftar motherboard dengan paginasi
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                MotherboardList(
                    motherboards = filteredMotherboards,
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
                MotherboardFilterDialog(
                    onDismiss = { showFilterDialog = false },
                    priceRange = priceRange,
                    selectedColors = selectedColors,
                    selectedSocket = selectedSocket,
                    selectedFormFactors = selectedFormFactors,
                    selectedMemoryTypes = selectedMemoryTypes,
                    onApply = { _, _, _, _, _ ->
                        showFilterDialog = false
                        applyFilters()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MotherboardFilterDialog(
    onDismiss: () -> Unit,
    priceRange: MutableState<ClosedFloatingPointRange<Float>>,
    selectedColors: MutableState<List<String>>,
    selectedSocket: MutableState<List<String>>,
    selectedFormFactors: MutableState<List<String>>,
    selectedMemoryTypes: MutableState<List<String>>,
    onApply: (
        price: ClosedFloatingPointRange<Double>,
        selectedColors: List<String>,
        selectedSocket: List<String>,
        selectedFormFactors: List<String>,
        selectedMemoryTypes: List<String>
    ) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Filter Motherboards")
        },
        text = {
            Column {
                // Price slider
                Text(
                    text = "Price: ${String.format("$%.2f", priceRange.value.start)} - ${String.format("$%.2f", priceRange.value.endInclusive)}"
                )
                RangeSlider(
                    value = priceRange.value,
                    onValueChange = { range -> priceRange.value = range },
                    valueRange = 0f..1000f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Color selection dropdown
                Text(text = "Select Colors")
                DropdownSelection(
                    availableOptions = listOf("Black", "Blue", "Red", "Silver", "Black / Silver", "Black / Gray", "Black / Red", "Gray / Black"),
                    selectedOptions = selectedColors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Socket selection dropdown
                Text(text = "Select CPU Sockets")
                DropdownSelection(
                    availableOptions = listOf("AM4", "AM5", "LGA1700"),
                    selectedOptions = selectedSocket
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Form Factor selection dropdown
                Text(text = "Select Form Factors")
                DropdownSelection(
                    availableOptions = listOf("ATX", "Micro ATX", "Mini ITX"),
                    selectedOptions = selectedFormFactors
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Memory Type selection dropdown
                Text(text = "Select Memory Types")
                DropdownSelection(
                    availableOptions = listOf("DDR4", "DDR5"),
                    selectedOptions = selectedMemoryTypes
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    priceRange.value.start.toDouble()..priceRange.value.endInclusive.toDouble(),
                    selectedColors.value,
                    selectedSocket.value,
                    selectedFormFactors.value,
                    selectedMemoryTypes.value
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
fun DropdownSelection(
    availableOptions: List<String>,
    selectedOptions: MutableState<List<String>>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selectedOptions.value.isEmpty()) "Select options" else selectedOptions.value.joinToString(", "),
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
            modifier = Modifier.heightIn(max = 200.dp) // Limit the dropdown height
        ) {
            availableOptions.forEach { option ->
                DropdownMenuItem(onClick = {
                    if (option in selectedOptions.value) {
                        selectedOptions.value = selectedOptions.value - option
                    } else {
                        selectedOptions.value = selectedOptions.value + option
                    }
                }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = option in selectedOptions.value,
                            onCheckedChange = null // Handled by DropdownMenuItem
                        )
                        Text(text = option)
                    }
                }
            }
        }
    }
}




@Composable
fun MotherboardList(
    motherboards: List<MotherboardBuild>,
    navController: NavController,
    currentPage: MutableState<Int>,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    val context = LocalContext.current

    // Jumlah item per halaman dan kalkulasi item yang ditampilkan
    val itemsPerPage = 10
    val currentPageMotherboards = motherboards
        .drop(currentPage.value * itemsPerPage) // Lewati item halaman sebelumnya
        .take(itemsPerPage) // Batasi jumlah item untuk halaman saat ini

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(currentPageMotherboards) { motherboard ->
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                imageUrl = motherboard.imageUrl,
                price = motherboard.price,
                title = motherboard.name,
                context = context,
                component = motherboard,
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
                            componentType = "motherboard",
                            componentData = motherboard,
                            onSuccess = {
                                isLoading.value = false
                                navController.navigateUp()
                                Log.d("MotherboardActivity", "Motherboard ${motherboard.name} saved successfully under build title: $title")
                            },
                            onFailure = { errorMessage ->
                                isLoading.value = false
                                Log.e("MotherboardActivity", "Failed to store Motherboard under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        isLoading.value = false
                        Log.e("MotherboardActivity", "Build title is null; unable to store Motherboard.")
                    }
                },
                onFavClick = {
                    savedFavorite(motherboard = motherboard, context = context)
                }
            )
        }

        // Tombol paginasi
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


