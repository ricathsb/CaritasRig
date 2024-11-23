package com.superbgoal.caritasrig.activity.homepage.favorites

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.superbgoal.caritasrig.functions.auth.SwipeToDeleteContainer

@Composable
fun FavoriteScreen(favoriteViewModel: FavoriteViewModel = viewModel()) {
    // State untuk mengontrol tampilan daftar yang dipilih (processors atau videoCards)
    var isShowingProcessors by remember { mutableStateOf(true) }

    // Collecting data from ViewModel
    val processors by favoriteViewModel.processors.collectAsState()
    val videoCards by favoriteViewModel.videoCards.collectAsState()

    // Memanggil fetchFavorites hanya sekali ketika komponen pertama kali dimuat
    LaunchedEffect(Unit) {
        favoriteViewModel.fetchFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tombol untuk toggle antara processor dan video card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { isShowingProcessors = true }, modifier = Modifier.weight(1f)) {
                Text(text = "Processors")
            }
            Button(onClick = { isShowingProcessors = false }, modifier = Modifier.weight(1f)) {
                Text(text = "Video Cards")
            }
        }

        // Menampilkan daftar berdasarkan pilihan
        if (isShowingProcessors) {
            // Menampilkan List Processors
            ListFavoriteProcessor(processors = processors) { processorId ->
                Log.d("FavoriteScreen", "Deleting processor with ID: $processorId")
                favoriteViewModel.deleteProcessor(processorId)
            }
        } else {
            // Menampilkan List Video Cards
            ListFavoriteVideoCard(videoCards = videoCards) { videoCardId ->
                Log.d("FavoriteScreen", "Deleting video card with ID: $videoCardId")
                favoriteViewModel.deleteVideoCard(videoCardId)
            }
        }

        // Jika tidak ada data favorite
        if (processors.isEmpty() && videoCards.isEmpty()) {
            Text(text = "No favorites added yet.", textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun ListFavoriteProcessor(processors: List<Map<String, Any>>, onDelete: (String) -> Unit) {
    if (processors.isNotEmpty()) {
        Text(text = "Processors:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = processors,
                key = { processor -> "${processor["name"]}_${processor["price"]}" } // Key unik
            ) { processor ->
                SwipeToDeleteContainer(
                    item = processor,
                    onDelete = { item ->
                        val processorName = item["name"] as? String
                        processorName?.let { onDelete(it) }
                    },
                ) { item ->
                    // Konten kartu processor
                    val processorName = item["name"] as? String ?: "Unknown Processor"
                    val processorPrice = item["price"] as? Double ?: 0.0
                    val processorCoreCount = item["core_count"] as? Int ?: 0
                    val processorCoreClock = item["core_clock"] as? Double ?: 0.0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${processorCoreCount} cores, ${processorCoreClock} GHz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
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
        Text(text = "Video Cards:", style = MaterialTheme.typography.titleMedium)
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = videoCards,
                key = { videoCard -> "${videoCard["name"]}_${videoCard["price"]}" } // Key unik
            ) { videoCard ->
                SwipeToDeleteContainer(
                    item = videoCard,
                    onDelete = { item ->
                        val videoCardName = item["name"] as? String
                        videoCardName?.let { onDelete(it) }
                    },
                ) { item ->
                    // Konten kartu video card
                    val videoCardName = item["name"] as? String ?: "Unknown Video Card"
                    val videoCardPrice = item["price"] as? Double ?: 0.0
                    val videoCardMemory = item["memory"] as? Int ?: 0
                    val videoCardCoreClock = item["coreClock"] as? Double ?: 0.0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${videoCardMemory} GB, ${videoCardCoreClock} MHz",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.secondary
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





