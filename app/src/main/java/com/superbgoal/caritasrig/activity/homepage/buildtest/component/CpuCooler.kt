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
import androidx.compose.foundation.lazy.items
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
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.CpuCooler
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

@Composable
fun CpuCoolerScreen(navController: NavController) {
    // Load CPU coolers from JSON resource
    val context = LocalContext.current
    val cpuCoolers: List<CpuCooler> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.cpucooler // Pastikan file JSON ini ada
        )
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

        // Main content with TopAppBar and CPU Cooler List
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
                            text = "CPU Coolers",
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
                        onClick = {
                            // Action for filter button (if needed)
                        },
                        modifier = Modifier.padding(end = 20.dp, top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Filter"
                        )
                    }
                }
            )

            // CPU Cooler List content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                CpuCoolerList(cpuCoolers,navController)
            }
        }
    }
}


@Composable
fun CpuCoolerList(cpuCoolers: List<CpuCooler>,navController: NavController) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cpuCoolers) { coolerItem ->
            // Track loading state for each CPU Cooler
            val isLoading = remember { mutableStateOf(false) }

            // Use ComponentCard for each CPU Cooler
            ComponentCard(
                title = coolerItem.name,
                details = "Price: $${coolerItem.price} | Size: ${coolerItem.size}mm | Color: ${coolerItem.color} | " +
                        "RPM: ${coolerItem.rpm} | Noise Level: ${coolerItem.noise_level} dB",
                context = context, // Passing context from LocalContext
                component = coolerItem,
                isLoading = isLoading.value, // Pass loading state to card
                onAddClick = {
                    // Start loading when the add button is clicked
                    isLoading.value = true
                    Log.d("CpuCoolerActivity", "Selected CPU Cooler: ${coolerItem.name}")

                    // Get the current user and build title
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    // Save CPU Cooler if buildTitle is available
                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "cpucooler", // Specify component type
                            componentData = coolerItem, // Pass CPU Cooler data
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                navController.navigateUp()
                                Log.d("CpuCoolerActivity", "CPU Cooler ${coolerItem.name} saved successfully under build title: $title")
                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("CpuCoolerActivity", "Failed to store CPU Cooler under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it } // Update loading state
                        )
                    } ?: run {
                        // Stop loading if buildTitle is nulla
                        isLoading.value = false
                        Log.e("CpuCoolerActivity", "Build title is null; unable to store CPU Cooler.")
                    }
                }
            )
        }
    }
}