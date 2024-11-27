package com.superbgoal.caritasrig.activity.homepage.benchmark

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.VideoCard
import kotlinx.coroutines.launch
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.functions.savedFavorite


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
                    modifier = Modifier.padding(horizontal = 8.dp).weight(1f)
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
                    modifier = Modifier.padding(horizontal = 8.dp).weight(1f)
                ) {
                    Text(text = "GPU", color = Color.White)
                }
            }

            // Swipeable Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> ProcessorListWithFavorite(processors = processors, navController = navController)
                    1 -> VideoCardListWithFavorite(videoCards = videoCards, navController = navController)
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
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
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${processor.core_count} cores, ${processor.core_clock} GHz",
                        style = MaterialTheme.typography.body2,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            savedFavorite(processor=processor)
                        }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                onClick = {

                }
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
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(onClick = {
                            savedFavorite(videoCard = videoCard)
                        }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
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
