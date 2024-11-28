package com.superbgoal.caritasrig.activity.homepage.buildtest.component

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
import com.superbgoal.caritasrig.data.model.component.Casing
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun CasingScreen(navController: NavController) {
    val context = LocalContext.current
    val casings: List<Casing> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.casing
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredCasings by remember { mutableStateOf(casings) }

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
                            text = "Casing",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
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
                CasingList(filteredCasings, navController)
            }
        }

        if (showFilterDialog) {
            CasingFilterDialog(
                onDismiss = { showFilterDialog = false },
                onApply = { selectedType, selectedColor, includePSU ->
                    showFilterDialog = false
                    filteredCasings = casings.filter { casing ->
                        val casingTypeLower = casing.type.lowercase()
                        val selectedTypeLower = selectedType.lowercase()

                        // Check if the selectedType is "All" or a substring of casing.type
                        (selectedType == "All" || casingTypeLower.contains(selectedTypeLower)) &&
                                (selectedColor == "All" || casing.color.lowercase() == selectedColor.lowercase()) &&
                                (!includePSU || casing.psu != null)
                    }
                }
            )
        }
    }
}

@Composable
fun CasingFilterDialog(
    onDismiss: () -> Unit,
    onApply: (selectedType: String, selectedColor: String, includePSU: Boolean) -> Unit
) {
    val types = listOf("All", "ATX", "MicroATX", "Mini ITX")
    val colors = listOf("All", "Black", "White", "RGB")

    var selectedType by remember { mutableStateOf("All") }
    var selectedColor by remember { mutableStateOf("All") }
    var includePSU by remember { mutableStateOf(false) }

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
                            onCheckedChange = { if (it) selectedType = type }
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
                            onCheckedChange = { if (it) selectedColor = color }
                        )
                        Text(text = color)
                    }
                }

                // Include PSU checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = includePSU,
                        onCheckedChange = { includePSU = it }
                    )
                    Text("Include only cases with PSU")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(selectedType, selectedColor, includePSU)
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
fun CasingList(casings: List<Casing>,navController: NavController) {
    // Get context from LocalContext
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(casings) { casing ->
            // Track loading state for each casing
            val isLoading = remember { mutableStateOf(false) }

            // Use ComponentCard for each casing
            ComponentCard(
                title = casing.name,
                details = "${casing.type} | ${casing.color} | PSU: ${casing.psu ?: "Not included"} | Volume: ${casing.externalVolume} L | 3.5\" Bays: ${casing.internal35Bays}",
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
                            componentType = "case",
                            componentData = casing,
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                Log.d("CasingActivity", "Casing ${casing.name} saved successfully under build title: $title")
                                navController.navigateUp()

                                // After success, navigate to BuildActivity
//                                    val intent = Intent(context, BuildActivity::class.java).apply {
//                                        putExtra("component_title", casing.name)
//                                        putExtra("component_data", casing) // Component sent as Parcelable
//                                    }
//                                    context.startActivity(intent)
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
                    savedFavorite(casing = casing, context = context)
                }
            )
        }
    }
}
