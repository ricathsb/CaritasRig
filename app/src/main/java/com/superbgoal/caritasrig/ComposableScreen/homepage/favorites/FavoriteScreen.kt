package com.superbgoal.caritasrig.ComposableScreen.homepage.favorites

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.SwipeToDeleteContainer

@Composable
fun FavoriteScreen(navController: NavController) {
    val favoriteViewModel: FavoriteViewModel = viewModel()
    var selectedComponent by remember { mutableStateOf("All") }

    val allComponents = mapOf(
        "processors" to favoriteViewModel.processors.collectAsState().value,
        "videoCards" to favoriteViewModel.videoCards.collectAsState().value,
        "motherboards" to favoriteViewModel.motherboards.collectAsState().value,
        "memory" to favoriteViewModel.memory.collectAsState().value,
        "powerSupplies" to favoriteViewModel.powerSupplies.collectAsState().value,
        "cpuCoolers" to favoriteViewModel.cpuCoolers.collectAsState().value,
        "casings" to favoriteViewModel.casings.collectAsState().value,
        "headphones" to favoriteViewModel.headphones.collectAsState().value,
        "internalHardDrives" to favoriteViewModel.internalHardDrives.collectAsState().value,
        "keyboards" to favoriteViewModel.keyboards.collectAsState().value,
        "mice" to favoriteViewModel.mice.collectAsState().value
    )

    LaunchedEffect(Unit) {
        favoriteViewModel.fetchFavorites()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3a3a52), Color(0xFF1f1f2e))
                )
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Navigation Tabs
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All") + allComponents.keys.toList()) { component ->
                    Button(
                        onClick = { selectedComponent = component },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedComponent == component) Color(0xFFD4AF37) else Color(0xFF2C2B30)
                        ),
                        modifier = Modifier
                            .height(40.dp)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = component.capitalize(),
                            color = if (selectedComponent == component) Color.Black else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Display selected component list
            val filteredComponents = when (selectedComponent) {
                "All" -> allComponents.flatMap { it.value }
                else -> allComponents[selectedComponent] ?: emptyList()
            }

            ListFavoriteComponents(
                title = selectedComponent.capitalize(),
                components = filteredComponents,
                onDelete = { componentId ->
                    // Temukan kategori komponen berdasarkan data
                    val componentCategory = allComponents.entries.find { it.value.any { it["name"] == componentId } }?.key

                    // Hapus komponen
                    favoriteViewModel.deleteComponent(componentCategory ?: "All", componentId)

                    // Tetapkan ulang selectedComponent hanya jika bukan dalam kategori "All"
                    if (selectedComponent != "All" && componentCategory != null) {
                        selectedComponent = componentCategory
                    }

                    Log.d("FavoriteScreen", "Deleted component: $componentId, category: $componentCategory")
                }
            ) { component ->
                val name = component["name"] as? String ?: "Unknown ${selectedComponent.capitalize()}"
                val description = component["description"] as? String ?: "No description available"
                val price = component["price"] as? Double ?: 0.0
                ComponentCard(
                    name = name,
                    description = description,
                    price = price
                )
            }

            // Show message if no favorites are added
            if (allComponents.all { it.value.isEmpty() }) {
                Text(
                    text = "No favorites added yet.",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                )
            }
        }
    }
}




//@Composable
//fun ListFavoriteProcessor(processors: List<Map<String, Any>>, te: (String) -> Unit) {
//    if (processors.isNotEmpty()) {
//        Text(
//            text = "Processors:",
//            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD4AF37))
//        )
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(
//                items = processors,
//                key = { processor -> "${processor["name"]}_${processor["price"]}" }
//            ) { processor ->
//                SwipeToDeleteContainer(
//                    item = processor,
//                    onDelete = { item ->
//                        val processorName = item["name"] as? String
//                        processorName?.let { onDelete(it) }
//                    },
//                ) { item ->
//                    val processorName = item["name"] as? String ?: "Unknown Processor"
//                    val processorPrice = item["price"] as? Double ?: 0.0
//                    val processorCoreCount = item["core_count"] as? Int ?: 0
//                    val processorCoreClock = item["core_clock"] as? Double ?: 0.0
//
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color(0xFF473947)),
//                        elevation = CardDefaults.cardElevation(8.dp),
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        ) {
//                            Text(
//                                text = processorName,
//                                style = MaterialTheme.typography.titleSmall,
//                                color = Color.White
//                            )
//                            Spacer(modifier = Modifier.height(4.dp))
//                            Text(
//                                text = "${processorCoreCount} cores, ${processorCoreClock} GHz",
//                                style = MaterialTheme.typography.bodySmall,
//                                color = Color.Gray
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(
//                                text = "$${"%.2f".format(processorPrice)}",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ListFavoriteVideoCard(
//    videoCards: List<Map<String, Any>>,
//    onDelete: (String) -> Unit
//) {
//    if (videoCards.isNotEmpty()) {
//        Text(
//            text = "Video Cards:",
//            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD4AF37))
//        )
//        LazyColumn(
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(
//                items = videoCards,
//                key = { videoCard -> "${videoCard["name"]}_${videoCard["price"]}" }
//            ) { videoCard ->
//                SwipeToDeleteContainer(
//                    item = videoCard,
//                    onDelete = { item ->
//                        val videoCardName = item["name"] as? String
//                        videoCardName?.let { onDelete(it) }
//                    },
//                ) { item ->
//                    val videoCardName = item["name"] as? String ?: "Unknown Video Card"
//                    val videoCardPrice = item["price"] as? Double ?: 0.0
//                    val videoCardMemory = item["memory"] as? Int ?: 0
//                    val videoCardCoreClock = item["coreClock"] as? Double ?: 0.0
//
//                    Card(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(vertical = 4.dp),
//                        colors = CardDefaults.cardColors(containerColor = Color(0xFF473947)),
//                        elevation = CardDefaults.cardElevation(8.dp),
//                        shape = RoundedCornerShape(12.dp)
//                    ) {
//                        Column(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(16.dp)
//                        ) {
//                            Text(
//                                text = videoCardName,
//                                style = MaterialTheme.typography.titleSmall,
//                                color = Color.White
//                            )
//                            Spacer(modifier = Modifier.height(4.dp))
//                            Text(
//                                text = "${videoCardMemory} GB, ${videoCardCoreClock} MHz",
//                                style = MaterialTheme.typography.bodySmall,
//                                color = Color.Gray
//                            )
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text(
//                                text = "$${"%.2f".format(videoCardPrice)}",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.primary
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//}


@Composable
fun ListFavoriteComponents(
    title: String,
    components: List<Map<String, Any>>,
    onDelete: (String) -> Unit,
    itemContent: @Composable (Map<String, Any>) -> Unit
) {
    if (components.isNotEmpty()) {
        Text(
            text = "$title:",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD4AF37)),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            items(
                items = components,
                key = { component -> "${component["name"]}_${component["price"]}" }
            ) { component ->
                SwipeToDeleteContainer(
                    item = component,
                    onDelete = { item ->
                        val componentName = item["name"] as? String
                        componentName?.let { onDelete(it) }
                    }
                ) {
                    itemContent(component)
                }
            }
        }
    }
}

@Composable
fun ComponentCard(
    name: String,
    description: String,
    price: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF473947)),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleSmall,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$${"%.2f".format(price)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
