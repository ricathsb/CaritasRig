package com.superbgoal.caritasrig.activity.homepage.buildtest.component

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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
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
import com.superbgoal.caritasrig.data.model.component.PowerSupply
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent

@Composable
fun PowerSupplyScreen(navController: NavController) {
    // Load power supplies from JSON resource
    val context = LocalContext.current
    val powerSupplies: List<PowerSupply> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.powersupply // Ensure this JSON file exists
        )
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var filteredPowerSupplies by remember { mutableStateOf(powerSupplies) }

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

        // Main content with TopAppBar and PowerSupplyList
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
                            text = "Power Supply",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
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

            // PowerSupplyList content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                PowerSupplyList(filteredPowerSupplies,navController)
            }
        }

        // Filter dialog
        if (showFilterDialog) {
            FilterDialogPS(
                onDismiss = { showFilterDialog = false },
                onApply = { selectedTypes, selectedWattages, selectedModularities ->
                    showFilterDialog = false
                    filteredPowerSupplies = powerSupplies.filter { powerSupply ->
                        (selectedTypes.isEmpty() || selectedTypes.contains(powerSupply.type)) &&
                                (selectedWattages.isEmpty() || powerSupply.wattage in selectedWattages) &&
                                (selectedModularities.isEmpty() || selectedModularities.contains(powerSupply.modular))
                    }
                }
            )
        }
    }
}


@Composable
fun PowerSupplyList(powerSupplies: List<PowerSupply>, navController: NavController) {
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(powerSupplies) { powerSupply ->
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                title = powerSupply.name,
                details = "Type: ${powerSupply.type} | Efficiency: ${powerSupply.efficiency} | Wattage: ${powerSupply.wattage}W | Modularity: ${powerSupply.modular} | Color: ${powerSupply.color}",
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
                            componentType = "powersupply",
                            componentData = powerSupply,
                            onSuccess = {
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
                }
            )
        }
    }
}

@Composable
fun FilterDialogPS(
    onDismiss: () -> Unit,
    onApply: (selectedTypes: List<String>, selectedWattages: List<Int>, selectedModularities: List<String>) -> Unit
) {
    val availableTypes = listOf("ATX", "SFX", "Mini ITX")
    val selectedTypes = remember { mutableStateOf(availableTypes.toMutableList()) }

    val availableWattages = listOf(400, 500, 600, 750, 850, 1000, 1200)
    val selectedWattages = remember { mutableStateOf(availableWattages.toMutableList()) }

    val availableModularities = listOf("Full", "Semi", "Non")
    val selectedModularities = remember { mutableStateOf(availableModularities.toMutableList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Power Supplies") },
        text = {
            Column {
                Text("Type:")
                availableTypes.forEach { type ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = type in selectedTypes.value,
                            onCheckedChange = { isChecked ->
                                val updatedList = selectedTypes.value.toMutableList()
                                if (isChecked) updatedList.add(type) else updatedList.remove(type)
                                selectedTypes.value = updatedList
                            }
                        )
                        Text(text = type)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Wattage (W):")
                availableWattages.forEach { wattage ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = wattage in selectedWattages.value,
                            onCheckedChange = { isChecked ->
                                val updatedList = selectedWattages.value.toMutableList()
                                if (isChecked) updatedList.add(wattage) else updatedList.remove(wattage)
                                selectedWattages.value = updatedList
                            }
                        )
                        Text(text = "$wattage W")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Modularity:")
                availableModularities.forEach { modularity ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = modularity in selectedModularities.value,
                            onCheckedChange = { isChecked ->
                                val updatedList = selectedModularities.value.toMutableList()
                                if (isChecked) updatedList.add(modularity) else updatedList.remove(modularity)
                                selectedModularities.value = updatedList
                            }
                        )
                        Text(text = modularity)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApply(
                    selectedTypes.value,
                    selectedWattages.value,
                    selectedModularities.value
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
