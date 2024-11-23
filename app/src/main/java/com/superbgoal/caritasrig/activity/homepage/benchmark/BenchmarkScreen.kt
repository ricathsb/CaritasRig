package com.superbgoal.caritasrig.activity.homepage.benchmark

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.VideoCard
import com.superbgoal.caritasrig.data.savedFavorite

@Composable
fun BenchmarkScreen(navController: NavController) {
    val context = LocalContext.current

    // Load processors and video cards
    val processors: List<Processor> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.processor
        )
    }
    val videoCards: List<VideoCard> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.videocard
        )
    }

    // State to track selected category (Processor or GPU)
    var selectedCategory by remember { mutableStateOf("Processor") }

    // Determine which list to display based on selectedCategory
    val displayedItems = if (selectedCategory == "Processor") processors else videoCards

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1B1F)) // Latar belakang gelap
    ) {
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Tombol kategori
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedCategory = "Processor" },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedCategory == "Processor") Color(0xFF3E3D42) else Color(0xFF2C2B30),
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                ) {
                    Text(text = "Processor", color = Color.White)
                }
                Button(
                    onClick = { selectedCategory = "GPU" },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedCategory == "GPU") Color(0xFF3E3D42) else Color(0xFF2C2B30)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                ) {
                    Text(text = "GPU", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // List content
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                if (selectedCategory == "Processor") {
                    ProcessorListWithFavorite(processors = processors, navController = navController)
                } else {
                    VideoCardListWithFavorite(videoCards = videoCards, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ProcessorListWithFavorite(processors: List<Processor>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(processors) { processor ->
            // Track favorite state for each processor
            var isFavorite by remember { mutableStateOf(false) }

            Card(
                backgroundColor = Color(0xFF473947),
                modifier = Modifier.fillMaxWidth()
                    .shadow(5.dp, RoundedCornerShape(15.dp)),
                elevation = 4.dp,
                shape = RoundedCornerShape(15.dp),
                onClick = { /* Navigate to processor detail */ }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = processor.name,
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${processor.core_count} cores, ${processor.core_clock} GHz",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            isFavorite = !isFavorite
                            savedFavorite(processor = processor)
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VideoCardListWithFavorite(videoCards: List<VideoCard>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videoCards) { videoCard ->
            var isFavorite by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                backgroundColor = Color(0xFF473947),
                onClick = { /* Navigate to video card detail */ }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = videoCard.name,
                        style = MaterialTheme.typography.h6,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${videoCard.memory} GB, ${videoCard.coreClock} MHz",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            isFavorite = !isFavorite
                            savedFavorite(videoCard = videoCard)
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}
