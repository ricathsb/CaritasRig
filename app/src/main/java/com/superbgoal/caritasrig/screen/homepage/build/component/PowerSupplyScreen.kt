package com.superbgoal.caritasrig.screen.homepage.build.component

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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RangeSlider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.PowerSupplyBuild
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.convertPrice
import com.superbgoal.caritasrig.functions.parseImageUrl
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun PowerSupplyScreen(navController: NavController) {
    val context = LocalContext.current
    val powerSupplies: List<PowerSupplyBuild> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.powersupply_2
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredPowerSupplies by remember { mutableStateOf(powerSupplies) }
    var searchQuery by remember { mutableStateOf("") }

    // State for pagination
    val itemsPerPage = 10
    val currentPage = remember { mutableStateOf(0) }
    val totalPages = remember { mutableStateOf(1) } // Set minimum totalPages to 1

    // State for filter values
    val wattageRange = remember { mutableStateOf(0f..1500f) }
    val selectedModularity = remember { mutableStateOf("") }
    val selectedManufacturers = remember { mutableStateOf(listOf<String>()) }

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
                        filteredPowerSupplies = powerSupplies.filter { psu ->
                            val isMatchingSearch = psu.name.contains(query, ignoreCase = true)
                            val psuWattage = parseWattage(psu.wattage)
                            val isMatchingFilter = psuWattage in wattageRange.value &&
                                    (selectedModularity.value.isEmpty() || psu.modular == selectedModularity.value) &&
                                    (selectedManufacturers.value.isEmpty() || psu.manufacturer in selectedManufacturers.value)
                            isMatchingSearch && isMatchingFilter
                        }

                        // Update totalPages after filtering
                        totalPages.value = maxOf(1, (filteredPowerSupplies.size / itemsPerPage) + if (filteredPowerSupplies.size % itemsPerPage == 0) 0 else 1)

                        // Adjust currentPage if it's greater than totalPages after filtering
                        currentPage.value = minOf(currentPage.value, totalPages.value - 1)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    onFilterClick = { showFilterDialog = true }
                )
            }

            // Paginated list of power supplies
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                if (filteredPowerSupplies.isEmpty()) {
                    // Show empty state when no data is available
                    Text(
                        text = "No power supplies match your filters.",
                        color = Color.Gray
                    )
                } else {
                    PowerSupplyList(
                        powerSupplies = filteredPowerSupplies,
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
            }

            // Show filter dialog
            if (showFilterDialog) {
                FilterPSUDialog(
                    onDismiss = { showFilterDialog = false },
                    wattageRange = wattageRange,
                    selectedModularity = selectedModularity,
                    availableModularities = listOf("Full", "Semi", "Non-Modular"),
                    selectedManufacturers = selectedManufacturers,
                    availableManufacturers = powerSupplies.map { it.manufacturer }.distinct(),
                    onApply = { newWattage, newModularity, newManufacturers ->
                        // Apply filter logic
                        wattageRange.value = newWattage
                        selectedModularity.value = newModularity
                        selectedManufacturers.value = newManufacturers

                        filteredPowerSupplies = powerSupplies.filter { psu ->
                            val psuWattage = parseWattage(psu.wattage)
                            psuWattage in wattageRange.value &&
                                    (newModularity.isEmpty() || psu.modular == newModularity) &&
                                    (newManufacturers.isEmpty() || psu.manufacturer in newManufacturers)
                        }

                        // Update totalPages after applying the filter
                        totalPages.value = maxOf(1, (filteredPowerSupplies.size / itemsPerPage) + if (filteredPowerSupplies.size % itemsPerPage == 0) 0 else 1)

                        // Adjust currentPage if it's greater than totalPages after filtering
                        currentPage.value = minOf(currentPage.value, totalPages.value - 1)
                        showFilterDialog = false
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FilterPSUDialog(
    onDismiss: () -> Unit,
    wattageRange: MutableState<ClosedFloatingPointRange<Float>>,
    selectedModularity: MutableState<String>,
    availableModularities: List<String>,
    selectedManufacturers: MutableState<List<String>>,
    availableManufacturers: List<String>,
    onApply: (
        wattageRange: ClosedFloatingPointRange<Float>,
        selectedModularity: String,
        selectedManufacturers: List<String>
    ) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Filter Power Supplies")
        },
        text = {
            Column {
                // Wattage range slider
                Text(text = "Wattage: ${wattageRange.value.start.toInt()}W - ${wattageRange.value.endInclusive.toInt()}W")
                RangeSlider(
                    value = wattageRange.value,
                    onValueChange = { range ->
                        wattageRange.value = range
                    },
                    valueRange = 0f..1500f
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Modularity dropdown
                Text(text = "Modularity")
                ModularityDropdown(
                    availableModularities = availableModularities,
                    selectedModularity = selectedModularity
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
                    wattageRange.value,
                    selectedModularity.value,
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
fun ModularityDropdown(
    availableModularities: List<String>,
    selectedModularity: MutableState<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = if (selectedModularity.value.isEmpty()) "Select modularity" else selectedModularity.value,
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
            availableModularities.forEach { modularity ->
                DropdownMenuItem(onClick = {
                    selectedModularity.value = modularity
                    expanded = false
                }) {
                    Text(text = modularity)
                }
            }
        }
    }
}


@Composable
fun PowerSupplyList(
    powerSupplies: List<PowerSupplyBuild>,
    navController: NavController,
    currentPage: MutableState<Int>,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    val context = LocalContext.current
    val itemsPerPage = 10

    // Calculate the current page's power supplies
    val currentPagePowerSupplies = powerSupplies
        .drop(currentPage.value * itemsPerPage) // Skip items from previous pages
        .take(itemsPerPage) // Limit items for the current page

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(currentPagePowerSupplies) { powerSupply ->
            // Track loading state for each power supply
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                price = powerSupply.price,
                imageUrl = parseImageUrl(powerSupply.imageUrl),
                title = powerSupply.name,
                priceConvert = convertPrice(powerSupply.price, context),
                context = context,
                details = """
                        Name: ${powerSupply.name}
                        Price: $${powerSupply.price}
                        Manufacturer: ${powerSupply.manufacturer}
                        Model: ${powerSupply.model}
                        Part #: ${powerSupply.partNumber}
                        Type: ${powerSupply.type}
                        Efficiency Rating: ${powerSupply.efficiencyRating}
                        Wattage: ${powerSupply.wattage} W
                        Length: ${powerSupply.length} mm
                        Modular: ${powerSupply.modular}
                        Color: ${powerSupply.color}
                        Fanless: ${powerSupply.fanless}
                        ATX 4-Pin Connectors: ${powerSupply.atx4PinConnectors}
                        EPS 8-Pin Connectors: ${powerSupply.eps8PinConnectors}
                        PCIe 12+4-Pin 12VHPWR Connectors: ${powerSupply.pcie12VhpwrConnectors}
                        PCIe 12-Pin Connectors: ${powerSupply.pcie12PinConnectors}
                        PCIe 8-Pin Connectors: ${powerSupply.pcie8PinConnectors}
                        PCIe 6+2-Pin Connectors: ${powerSupply.pcie6Plus2PinConnectors}
                        PCIe 6-Pin Connectors: ${powerSupply.pcie6PinConnectors}
                        SATA Connectors: ${powerSupply.sataConnectors}
                        Molex 4-Pin Connectors: ${powerSupply.molex4PinConnectors}
                        Specs Number: ${powerSupply.specsNumber}
                    """.trimIndent(),
                isLoading = isLoading.value,
                navController = navController,
                onAddClick = {
                    isLoading.value = true
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "powerSupply",
                            componentData = powerSupply,
                            onSuccess = {
                                isLoading.value = false
                                Log.d("PowerSupplyActivity", "Power Supply ${powerSupply.name} saved successfully under build title: $title")
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                isLoading.value = false
                                Log.e("PowerSupplyActivity", "Failed to store Power Supply: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        isLoading.value = false
                        Log.e("PowerSupplyActivity", "Build title is null; unable to store Power Supply.")
                    }
                },
                onFavClick = {
                    savedFavorite(powerSupply = powerSupply, context = context)
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


fun parseWattage(wattageString: String): Float {
    return wattageString
        .replace(" W", "") // Menghapus " W"
        .toFloatOrNull() ?: 0f // Konversi ke Float, default 0f jika gagal
}


