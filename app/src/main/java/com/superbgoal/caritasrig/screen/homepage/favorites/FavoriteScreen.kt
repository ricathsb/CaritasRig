package com.superbgoal.caritasrig.screen.homepage.favorites

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.SwipeToDeleteContainer

@Composable
fun FavoriteScreen() {
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
            // Updated ListFavoriteComponents with fixed onDelete logic
            // Updated ListFavoriteComponents with improved onDelete logic
            ListFavoriteComponents(
                title = selectedComponent.capitalize(),
                components = filteredComponents,
                onDelete = { componentId ->
                    // Tentukan jenis komponen dari semua kategori jika selectedComponent adalah "All"
                    val componentType = if (selectedComponent == "All") {
                        allComponents.entries.firstOrNull { (_, components) ->
                            components.any { it["name"] == componentId }
                        }?.key ?: "unknown"
                    } else {
                        selectedComponent
                    }

                    // Lanjutkan hanya jika jenis komponen valid dan bukan "unknown"
                    if (componentType != "unknown") {
                        favoriteViewModel.deleteComponent(componentType = componentType, componentName = componentId)
                    } else {
                        Log.e("FavoriteViewModel", "Failed to determine component type for deletion")
                    }
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
                        text = stringResource(id = R.string.no_favorites_added),
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
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
            text = "$title ${stringResource(id = R.string.components)}",
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