package com.superbgoal.caritasrig.activity.homepage.component

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.build.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.component.VideoCard
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

class VideoCardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val buildTitle = BuildManager.getBuildTitle()

        // Load video cards from JSON resource
        val typeToken = object : TypeToken<List<VideoCard>>() {}.type
        val videoCards: List<VideoCard> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.videocard // Ensure this JSON file exists
        )

        setContent {
            MaterialTheme {
                var showFilterDialog by remember { mutableStateOf(false) }
                var filteredVideoCards by remember { mutableStateOf(videoCards) }

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

                    // Main content with TopAppBar and VideoCardList
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
                                        text = "Video Card",
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(this@VideoCardActivity, BuildActivity::class.java)
                                        startActivity(intent)
                                        finish()
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

                        // VideoCardList content
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            VideoCardList(filteredVideoCards)
                        }
                    }

                    // Filter dialog
                    if (showFilterDialog) {
                        FilterDialog(
                            onDismiss = { showFilterDialog = false },
                            onApply = { selectedBrands, selectedMemorySizes, selectedCoreClocks ->
                                showFilterDialog = false
                                filteredVideoCards = videoCards.filter { videoCard ->
                                    (selectedBrands.isEmpty() || selectedBrands.any { videoCard.name.contains(it, ignoreCase = true) }) &&
                                            (selectedMemorySizes.isEmpty() || videoCard.memory in selectedMemorySizes) &&
                                            (selectedCoreClocks.isEmpty() || videoCard.coreClock.toInt() in selectedCoreClocks)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun VideoCardList(videoCards: List<VideoCard>) {
        val context = LocalContext.current

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videoCards) { videoCard ->
                val isLoading = remember { mutableStateOf(false) }

                ComponentCard(
                    title = videoCard.name,
                    details = "Chipset: ${videoCard.chipset} | ${videoCard.memory}GB | Core Clock: ${videoCard.coreClock}MHz | Boost Clock: ${videoCard.boostClock}MHz | Color: ${videoCard.color} | Length: ${videoCard.length}mm",
                    context = context,
                    component = videoCard,
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
                                componentType = "gpu",
                                componentData = videoCard,
                                onSuccess = {
                                    isLoading.value = false
                                    val intent = Intent(context, BuildActivity::class.java).apply {
                                        putExtra("component_title", videoCard.name)
                                        putExtra("component_data", videoCard)
                                    }
                                    context.startActivity(intent)
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
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun FilterDialog(
        onDismiss: () -> Unit,
        onApply: (
            selectedBrands: List<String>,
            selectedMemorySizes: List<Double>,
            selectedCoreClocks: IntRange
        ) -> Unit
    ) {
        val availableBrands = listOf("AMD", "NVIDIA", "Intel")
        val selectedBrands = remember { mutableStateOf(availableBrands.toMutableList()) }

        val availableMemorySizes = listOf(4.0, 6.0, 8.0, 12.0, 16.0)
        val selectedMemorySizes = remember { mutableStateOf(availableMemorySizes.toMutableList()) }

        val coreClockRange = remember { mutableStateOf(500..3000) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Filter Video Cards") },
            text = {
                Column {
                    Text("Brand:")
                    availableBrands.forEach { brand ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = brand in selectedBrands.value,
                                onCheckedChange = { isChecked ->
                                    val updatedList = selectedBrands.value.toMutableList()
                                    if (isChecked) updatedList.add(brand) else updatedList.remove(brand)
                                    selectedBrands.value = updatedList
                                }
                            )
                            Text(text = brand)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Memory Size (GB):")
                    availableMemorySizes.forEach { size ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = size in selectedMemorySizes.value,
                                onCheckedChange = { isChecked ->
                                    val updatedList = selectedMemorySizes.value.toMutableList()
                                    if (isChecked) updatedList.add(size) else updatedList.remove(size)
                                    selectedMemorySizes.value = updatedList
                                }
                            )
                            Text(text = "$size GB")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Core Clock: ${coreClockRange.value.start} MHz - ${coreClockRange.value.endInclusive} MHz")
                    RangeSlider(
                        value = coreClockRange.value.start.toFloat()..coreClockRange.value.endInclusive.toFloat(),
                        onValueChange = { range ->
                            coreClockRange.value = range.start.toInt()..range.endInclusive.toInt()
                        },
                        valueRange = 500f..3000f,
                        steps = 10
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onApply(
                        selectedBrands.value,
                        selectedMemorySizes.value,
                        coreClockRange.value
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
}
