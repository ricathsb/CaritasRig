package com.superbgoal.caritasrig.ComposableScreen.homepage.homepage

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kwabenaberko.newsapilib.models.Article
import com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest.BuildViewModel
import com.superbgoal.caritasrig.R

@Composable
fun HomeScreen2(navController: NavController, buildViewModel: BuildViewModel) {
    val viewModel = HomepageScreen()
    val articles by viewModel.articles.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }

    // Use a boolean state to control focus instead of InteractionSource
    var isFocused by remember { mutableStateOf(false) }

    val sancreekFont = FontFamily(Font(R.font.sancreek))
    val sairastencilone = FontFamily(Font(R.font.sairastencilone))
    val listState = rememberLazyListState()
    val localContext = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current


    // Animasi ketinggian Card berdasarkan status fokus
    val techNewsCardPadding by animateDpAsState(targetValue = if (isFocused) 0.dp else 16.dp)
    val techNewsCardHeight by animateDpAsState(targetValue = if (isFocused) 700.dp else 300.dp)
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current


    Image(
        painter = painterResource(id = R.drawable.component_bg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )

    // Use a Box to handle outside tap detection
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        // Completely clear focus
                        focusManager.clearFocus(true)

                        // Dismiss keyboard
                        keyboardController?.hide()

                        // Reset focus state
                        isFocused = false
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // AnimatedVisibility untuk menampilkan atau menyembunyikan header
            AnimatedVisibility(visible = !isFocused) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CaritasRig",
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.White,
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sancreekFont
                    )

                    Text(
                        text = "Pick Parts.",
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sairastencilone
                    )
                    Text(
                        text = "Build Your PC.",
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sairastencilone
                    )
                    Text(
                        text = "Compare.",
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sairastencilone
                    )
                    Text(
                        text = "Benchmark.",
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = sairastencilone
                    )
                    Divider(color = Color.LightGray, thickness = 1.dp)
                }
            }

            AnimatedVisibility(visible = !isFocused) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()) // Tambahkan kemampuan scroll horizontal
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Jarak antar tombol
                ) {
                    val buttonBackgroundColor = Color(0xFF473947) // Warna latar tombol
                    val buttonContentColor = Color.White // Warna konten tombol (ikon dan teks)

                    // Tombol "Add Your Build"
                    Button(
                        onClick = {
                            buildViewModel.setNewBuildState(true)
                            navController.navigate("build_details")
                        },
                        modifier = Modifier
                            .size(width = 200.dp, height = 60.dp), // Ukuran tombol
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = buttonBackgroundColor
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Icon",
                                tint = buttonContentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add Your Build",
                                color = buttonContentColor
                            )
                        }
                    }

                    // Tombol "Explore Other's Build"
                    Button(
                        onClick = {
                            navController.navigate("shared_build_screen")
                        },
                        modifier = Modifier
                            .size(width = 200.dp, height = 60.dp), // Ukuran tombol
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = buttonBackgroundColor
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = "Explore Other's Build",
                                tint = buttonContentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Explore Other's Build",
                                color = buttonContentColor
                            )
                        }
                    }

                }

            Spacer(modifier = Modifier.width(16.dp)) // Jarak antara dua tombol
            }

            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = techNewsCardPadding)
                    .height(techNewsCardHeight),
                shape = RoundedCornerShape(25.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "TECH NEWS",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 16.dp),
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                    )

                    // Search bar with custom focus handling
                    TextField(
                        value = searchQuery,
                        onValueChange = { query ->
                            searchQuery = query
                            viewModel.fetchEverythingWithQuery(searchQuery)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .focusRequester(focusRequester)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            },
                        placeholder = { Text("Search articles...", color = Color.White) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color.Transparent,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.Gray,
                            textColor = Color.White
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Completely clear focus
                                focusManager.clearFocus(true)

                                // Dismiss keyboard
                                keyboardController?.hide()

                                // Reset focus state
                                isFocused = false
                            }
                        )
                    )

                    // List artikel dalam LazyColumn
                    LazyColumn(state = listState) {
                        val filteredArticles = articles.filter { article ->
                            article.title.contains(searchQuery, ignoreCase = true)
                        }
                        items(filteredArticles) { article ->
                            ArticleItem(article, navController)
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun ArticleItem(article: Article,navController: NavController) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("news_article_screen")
                ArticleUrlSingletons.setArticleUrl(article.url)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    )
    {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = article.urlToImage ?: "https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = article.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 2,
                    color = Color.Black
                )
                Text(
                    text = article.source.name,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }



}