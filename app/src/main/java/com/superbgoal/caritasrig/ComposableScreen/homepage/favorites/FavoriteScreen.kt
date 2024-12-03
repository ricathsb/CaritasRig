package com.superbgoal.caritasrig.ComposableScreen.homepage.favorites

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    // Calculate filtered components
    val filteredComponents = when (selectedComponent) {
        "All" -> allComponents.flatMap { it.value }
        else -> allComponents[selectedComponent] ?: emptyList()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stylish Header
            Text(
                text = "My Favorites",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Navigation Tabs with Elegant Design
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All") + allComponents.keys.toList()) { component ->
                    val isSelected = selectedComponent == component
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                color = if (isSelected) Color(0xFFBBB9B9) else Color(0xFF2C2B30),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color.Transparent else Color(0xFF3A3A4A),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedComponent = component }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = component.capitalize(),
                            color = if (isSelected) Color.Black else Color.White,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Updated ListFavoriteComponents
            ListFavoriteComponents(
                title = selectedComponent.capitalize(),
                components = filteredComponents,
                onDelete = { componentId ->
                    // Existing delete logic
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

            // Improved Empty State
            if (allComponents.all { it.value.isEmpty() }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No favorites added yet.\nExplore components to start your collection!",
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                }
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
fun ComponentCard(
    name: String,
    description: String,
    price: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF473947),
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFBBB9B9)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "$${"%.2f".format(price)}",
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFD4AF37),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ListFavoriteComponents(
    title: String,
    components: List<Map<String, Any>>,
    onDelete: (String) -> Unit,
    itemContent: @Composable (Map<String, Any>) -> Unit
) {
    if (components.isNotEmpty()) {
        Text(
            text = "$title Components",
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
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