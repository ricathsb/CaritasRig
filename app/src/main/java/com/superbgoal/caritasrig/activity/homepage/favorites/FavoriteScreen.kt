package com.superbgoal.caritasrig.activity.homepage.favorites

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.SwipeToDeleteContainer

@Composable
fun FavoriteScreen(favoriteViewModel: FavoriteViewModel = viewModel()) {
    var isShowingProcessors by remember { mutableStateOf(true) }

    val processors by favoriteViewModel.processors.collectAsState()
    val videoCards by favoriteViewModel.videoCards.collectAsState()

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { isShowingProcessors = true },
                    modifier = Modifier
                        .weight(1f) ,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2B30))
                ) {
                    Text(text = "Processors", color = Color.White)
                }
                Button(
                    onClick = { isShowingProcessors = false },
                    modifier = Modifier
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2B30))
                ) {
                    Text(text = "Video Cards", color = Color.White)
                }
            }

            if (isShowingProcessors) {
                ListFavoriteProcessor(processors = processors) { processorId ->
                    Log.d("FavoriteScreen", "Deleting processor with ID: $processorId")
                    favoriteViewModel.deleteProcessor(processorId)
                }
            } else {
                ListFavoriteVideoCard(videoCards = videoCards) { videoCardId ->
                    Log.d("FavoriteScreen", "Deleting video card with ID: $videoCardId")
                    favoriteViewModel.deleteVideoCard(videoCardId)
                }
            }

            if (processors.isEmpty() && videoCards.isEmpty()) {
                Text(
                    text = "No favorites added yet.",
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun ListFavoriteProcessor(processors: List<Map<String, Any>>, onDelete: (String) -> Unit) {
    if (processors.isNotEmpty()) {
        Text(
            text = "Processors:",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD4AF37))
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = processors,
                key = { processor -> "${processor["name"]}_${processor["price"]}" }
            ) { processor ->
                SwipeToDeleteContainer(
                    item = processor,
                    onDelete = { item ->
                        val processorName = item["name"] as? String
                        processorName?.let { onDelete(it) }
                    },
                ) { item ->
                    val processorName = item["name"] as? String ?: "Unknown Processor"
                    val processorPrice = item["price"] as? Double ?: 0.0
                    val processorCoreCount = item["core_count"] as? Int ?: 0
                    val processorCoreClock = item["core_clock"] as? Double ?: 0.0

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
                                text = processorName,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${processorCoreCount} cores, ${processorCoreClock} GHz",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${"%.2f".format(processorPrice)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ListFavoriteVideoCard(
    videoCards: List<Map<String, Any>>,
    onDelete: (String) -> Unit
) {
    if (videoCards.isNotEmpty()) {
        Text(
            text = "Video Cards:",
            style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFD4AF37))
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = videoCards,
                key = { videoCard -> "${videoCard["name"]}_${videoCard["price"]}" }
            ) { videoCard ->
                SwipeToDeleteContainer(
                    item = videoCard,
                    onDelete = { item ->
                        val videoCardName = item["name"] as? String
                        videoCardName?.let { onDelete(it) }
                    },
                ) { item ->
                    val videoCardName = item["name"] as? String ?: "Unknown Video Card"
                    val videoCardPrice = item["price"] as? Double ?: 0.0
                    val videoCardMemory = item["memory"] as? Int ?: 0
                    val videoCardCoreClock = item["coreClock"] as? Double ?: 0.0

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
                                text = videoCardName,
                                style = MaterialTheme.typography.titleSmall,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${videoCardMemory} GB, ${videoCardCoreClock} MHz",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "$${"%.2f".format(videoCardPrice)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
