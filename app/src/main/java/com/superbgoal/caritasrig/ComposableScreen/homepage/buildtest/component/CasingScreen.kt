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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.TextButton
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
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.CasingBuild
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.DefaultSearchBar
import com.superbgoal.caritasrig.functions.SearchBarForComponent
import com.superbgoal.caritasrig.functions.parseImageUrl
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun CasingScreen(navController: NavController) {
    val context = LocalContext.current
    val casings: List<CasingBuild> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.casing_2
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredCasings by remember { mutableStateOf(casings) }
    var searchQuery by remember { mutableStateOf("") }

    // State for pagination
    val itemsPerPage = 10
    val currentPage = remember { mutableStateOf(0) }
    var totalPages = remember { mutableStateOf((filteredCasings.size / itemsPerPage) + if (filteredCasings.size % itemsPerPage == 0) 0 else 1) }

    // State for filter values
    var selectedType by remember { mutableStateOf("All") }
    var selectedColor by remember { mutableStateOf("All") }

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
                        // Apply search and filter simultaneously
                        filteredCasings = casings.filter { casing ->
                            val isMatchingType = selectedType == "All" || casing.type.lowercase().contains(selectedType.lowercase())
                            val isMatchingColor = selectedColor == "All" || casing.color.lowercase() == selectedColor.lowercase()
                            val isMatchingSearch = casing.name.contains(query, ignoreCase = true)

                            // Only show casings that match both the search query and filter criteria
                            isMatchingType && isMatchingColor && isMatchingSearch
                        }

                        // Update totalPages after filtering
                        totalPages.value = (filteredCasings.size / itemsPerPage) + if (filteredCasings.size % itemsPerPage == 0) 0 else 1

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

            // Paginated list of casings
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = Color.Transparent
            ) {
                CasingList(
                    casings = filteredCasings,
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
                CasingFilterDialog(
                    onDismiss = { showFilterDialog = false },
                    selectedType = selectedType,
                    selectedColor = selectedColor,
                    onTypeChange = { newType -> selectedType = newType },
                    onColorChange = { newColor -> selectedColor = newColor },
                    onApply = { newType, newColor ->
                        showFilterDialog = false
                        // Re-filter casings based on the selected filter
                        filteredCasings = casings.filter { casing ->
                            val casingTypeLower = casing.type.lowercase()
                            val selectedTypeLower = newType.lowercase()

                            // Apply the selected filters (type and color)
                            (newType == "All" || casingTypeLower.contains(selectedTypeLower)) &&
                                    (newColor == "All" || casing.color.lowercase() == newColor.lowercase())
                        }

                        // Update totalPages after applying the filter
                        totalPages.value = (filteredCasings.size / itemsPerPage) + if (filteredCasings.size % itemsPerPage == 0) 0 else 1

                        // Adjust currentPage if it's greater than totalPages after filtering
                        if (currentPage.value >= totalPages.value) {
                            currentPage.value = totalPages.value - 1
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CasingFilterDialog(
    onDismiss: () -> Unit,
    onApply: (selectedType: String, selectedColor: String) -> Unit,
    selectedType: String, // Pass the selectedType as a parameter
    selectedColor: String, // Pass the selectedColor as a parameter
    onTypeChange: (String) -> Unit, // A function to update selectedType
    onColorChange: (String) -> Unit, // A function to update selectedColor
) {
    val types = listOf("All", "ATX", "MicroATX", "Mini ITX")
    val colors = listOf("All", "Black", "White")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Casings") },
        text = {
            Column {
                // Type selection
                Text("Type:")
                types.forEach { type ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedType == type,
                            onCheckedChange = { if (it) onTypeChange(type) }
                        )
                        Text(text = type)
                    }
                }

                // Color selection
                Text("Color:")
                colors.forEach { color ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedColor == color,
                            onCheckedChange = { if (it) onColorChange(color) }
                        )
                        Text(text = color)
                    }
                }

            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(selectedType, selectedColor)
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
fun CasingList(
    casings: List<CasingBuild>,
    navController: NavController,
    currentPage: MutableState<Int>,
    totalPages: Int,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit
) {
    // Get context from LocalContext
    val context = LocalContext.current

    // Calculate the current page's casings
    val itemsPerPage = 10
    val currentPageCasings = casings
        .drop(currentPage.value * itemsPerPage) // Skipping previous pages
        .take(itemsPerPage) // Limiting the number of items to display

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(currentPageCasings) { casing ->
            // Track loading state for each casing
            val isLoading = remember { mutableStateOf(false) }

            // Use ComponentCard for each casing
            ComponentCard(
                imageUrl = parseImageUrl(casing.imageUrl),
                price = casing.price,
                title = casing.name,
                details = """
                            Name: ${casing.name}
                            Price: $${casing.price}
                            Manufacturer: ${casing.manufacturer}
                            Part #: ${casing.partNumber}
                            Type: ${casing.type}
                            Color: ${casing.color}
                            Power Supply: ${casing.powerSupply.ifEmpty { "Not included" }}
                            Side Panel: ${casing.sidePanel}
                            Power Supply Shroud: ${casing.powerSupplyShroud.ifEmpty { "Not included" }}
                            Front Panel USB: ${casing.frontPanelUsb}
                            Motherboard Form Factor: ${casing.motherboardFormFactor}
                            Maximum Video Card Length: ${casing.maxVideoCardLength}
                            Drive Bays: ${casing.driveBays}
                            Expansion Slots: ${casing.expansionSlots}
                            Dimensions: ${casing.dimensions}
                            Volume: ${casing.volume} L
                        """.trimIndent(),
                // Passing context from LocalContext
                component = casing,
                isLoading = isLoading.value, // Pass loading state to card
                navController = navController,
                onAddClick = {
                    // Start loading when the add button is clicked
                    isLoading.value = true
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()

                    // Use the BuildManager singleton to get the current build title
                    val buildTitle = BuildManager.getBuildTitle()

                    buildTitle?.let { title ->
                        // Save the component to the database
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "casing",
                            componentData = casing,
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                Log.d("CasingActivity", "Casing ${casing.name} saved successfully under build title: $title")
                                navController.navigateUp()
                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("CasingActivity", "Failed to store casing under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it }
                        )
                    } ?: run {
                        // Stop loading if buildTitle is null
                        isLoading.value = false
                        Log.e("CasingActivity", "Build title is null; unable to store casing.")
                    }
                },
                onFavClick = {
                    Log.d("CasingActivity", "Favorite button clicked for Casing: ${parseImageUrl(casing.imageUrl)}")
                    savedFavorite(casing = casing, context = context)
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
