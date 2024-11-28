package com.superbgoal.caritasrig.activity.homepage.benchmark

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder

import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.VideoCard
import kotlinx.coroutines.launch
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.ProcessorList
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.functions.savedFavorite

@OptIn(ExperimentalPagerApi::class)
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

    var sortOrder by remember { mutableStateOf("Descending") }
    val sortedProcessors = remember(sortOrder) {
        if (sortOrder == "Descending") {
            processors.sortedWith(compareByDescending<Processor> { it.single_core_score }
                .thenByDescending { it.multi_core_score })
        } else {
            processors.sortedWith(compareBy<Processor> { it.single_core_score }
                .thenBy { it.multi_core_score })
        }
    }
    val sortedVideoCards = remember(sortOrder) {
        if (sortOrder == "Descending") videoCards else videoCards.reversed()
    }


    val pagerState = rememberPagerState(initialPage = 0){2}
    val coroutineScope = rememberCoroutineScope()

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

        Column {
            // Tab indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (pagerState.currentPage == 0) colorResource(id = R.color.brown) else Color.Gray
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                ) {
                    Text(text = "Processor", color = Color.White)
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (pagerState.currentPage == 1) colorResource(id = R.color.brown) else Color.Gray
                    ),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .weight(1f)
                ) {
                    Text(text = "GPU", color = Color.White)
                }
            }

            // Dropdown for sorting
            SortingDropdown(
                sortOrder = sortOrder,
                onOrderChange = { sortOrder = it }
            )

            // Swipeable Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> ProcessorListWithFavorite(processors = sortedProcessors, navController = navController)
                    1 -> VideoCardListWithFavorite(videoCards = sortedVideoCards, navController = navController)
                }
            }
        }
    }
}

@Composable
fun SortingDropdown(sortOrder: String, onOrderChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Teks "Sort by" di sebelah kiri
        Text(
            text = "Sort by:",
            style = MaterialTheme.typography.body1,
            color = Color.Black // Warna teks bisa disesuaikan
        )

        // Tombol Dropdown di sebelah kanan
        Box {
            Button(
                onClick = { expanded = true },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = colorResource(id = R.color.brown), // Warna tombol dari resources
                    contentColor = Color.White // Warna teks tombol
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = sortOrder)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    onOrderChange("Descending")
                    expanded = false
                }) {
                    Text("Descending")
                }
                DropdownMenuItem(onClick = {
                    onOrderChange("Ascending")
                    expanded = false
                }) {
                    Text("Ascending")
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
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(processors) { processor ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(id = R.color.brown1),
                onClick = { /* Navigate to processor detail */ }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Processor Name
                    Text(
                        text = processor.name,
                        style = MaterialTheme.typography.h6,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Processor Details
                    Text(
                        text = "${processor.core_count} cores, ${processor.core_clock} GHz",
                        style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal Bar Chart
                    Text(
                        text = "Single-Core Score: ${processor.single_core_score}",
                        style = MaterialTheme.typography.body2,
                        color = Color.Black
                    )
                    HorizontalBar(
                        value = processor.single_core_score,
                        maxValue = 3000, // Adjust max value based on your dataset
                        color = Color.Blue
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Multi-Core Score: ${processor.multi_core_score}",
                        style = MaterialTheme.typography.body2,
                        color = Color.Black
                    )
                    HorizontalBar(
                        value = processor.multi_core_score,
                        maxValue = 35000, // Adjust max value based on your dataset
                        color = Color.Green
                    )
                }
            }
        }
    }
}

@Composable
fun HorizontalBar(value: Int, maxValue: Int, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = value / maxValue.toFloat())
                .height(8.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun VideoCardListWithFavorite(videoCards: List<VideoCard>, navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 5.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(videoCards) { videoCard ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(id = R.color.brown1),
                onClick = { }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = videoCard.name,
                        style = MaterialTheme.typography.h6,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${videoCard.memory} GB, ${videoCard.coreClock} MHz",
                        style = MaterialTheme.typography.body2,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}
