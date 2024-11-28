package com.superbgoal.caritasrig.activity.homepage.buildtest.component

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.superbgoal.caritasrig.data.model.component.InternalHardDrive
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun InternalHardDriveScreen(navController: NavController) {
    val context = LocalContext.current
    val allHardDrives: List<InternalHardDrive> = remember {
        loadItemsFromResources(context, R.raw.internalharddrive)
    }

    // Filter state
    val (filterType, setFilterType) = remember { mutableStateOf("") } // Default filter

    // Filter the list based on the selected type
    val filteredHardDrives = allHardDrives.filter { drive ->
        filterType.isEmpty() || drive.type.equals(filterType, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 10.dp)) {
                        Text(
                            text = "Part Pick",
                            style = MaterialTheme.typography.h4,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Internal Hard Drive",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )

            // Simple filter dropdown or buttons
            FilterOptions(setFilterType)

            Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                InternalHardDriveList(filteredHardDrives, navController)
            }
        }
    }
}

@Composable
fun FilterOptions(setFilterType: (String) -> Unit) {
    // Example filter options
    val filterOptions = listOf("All", "SSD", "5000", "5400", "7200", "10000")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text("Filter by Type:", style = MaterialTheme.typography.subtitle1)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { option ->
                Button(
                    onClick = { setFilterType(if (option == "All") "" else option) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
                ) {
                    Text(option, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InternalHardDriveList(internalHardDrives: List<InternalHardDrive>, navController: NavController) {
    // Get context from LocalContext
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(internalHardDrives) { hardDrive ->
            // Track loading state for each hard drive
            val isLoading = remember { mutableStateOf(false) }

            // Use ComponentCard for each hard drive
            ComponentCard(
                title = hardDrive.name,
                details = "Capacity: ${hardDrive.capacity}GB | Price per GB: \$${hardDrive.pricePerGb} | Type: ${hardDrive.type} | Cache: ${hardDrive.cache}MB | Form Factor: ${hardDrive.formFactor} | Interface: ${hardDrive.interfacee}",
                // Passing context from LocalContext
                component = hardDrive,
                isLoading = isLoading.value, // Pass loading state to card
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

                    // Save hard drive if buildTitle is available
                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "internalharddrive", // Specify component type
                            componentData = hardDrive, // Pass hard drive data
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                Log.d("HardDriveActivity", "Hard Drive ${hardDrive.name} saved successfully under build title: $title")
                                navController.navigateUp()
                                // Navigate to BuildActivity after success

                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("HardDriveActivity", "Failed to store Hard Drive under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it } // Update loading state
                        )
                    } ?: run {
                        // Stop loading if buildTitle is null
                        isLoading.value = false
                        Log.e("HardDriveActivity", "Build title is null; unable to store Hard Drive.")
                    }
                }
            )
        }
    }
}