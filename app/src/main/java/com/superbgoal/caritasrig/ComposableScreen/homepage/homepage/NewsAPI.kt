package com.superbgoal.caritasrig.ComposableScreen.homepage.homepage

import androidx.compose.foundation.Image
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.remember
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kwabenaberko.newsapilib.models.Article
import com.superbgoal.caritasrig.R

@Composable
fun HomeScreen2(navController: NavController) {
    val viewModel = HomepageScreen()
    val articles by viewModel.articles.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }
    val isSearchActive = remember { MutableInteractionSource() } // Interaction source untuk fokus
    val hasFocus by isSearchActive.collectIsFocusedAsState() // Mengumpulkan status fokus dari InteractionSource
    val sancreekFont = FontFamily(Font(R.font.sancreek))
    val sairastencilone = FontFamily(Font(R.font.sairastencilone))
    val listState = rememberLazyListState()

    // Animasi ketinggian Card berdasarkan status fokus
    val techNewsCardPadding by animateDpAsState(targetValue = if (hasFocus) 0.dp else 16.dp)
    val techNewsCardHeight by animateDpAsState(targetValue = if (hasFocus) 700.dp else 300.dp)

    Image(
        painter = painterResource(id = R.drawable.component_bg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // AnimatedVisibility untuk menampilkan atau menyembunyikan header
        AnimatedVisibility(visible = !hasFocus) {
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

        // Mulai Card TECH NEWS yang dapat mengisi layar saat search bar diaktifkan
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

                // Search bar dengan InteractionSource
                TextField(
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query
                        viewModel.fetchEverythingWithQuery(searchQuery)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    placeholder = { Text("Search articles...", color = Color.White) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    interactionSource = isSearchActive, // Pasangkan InteractionSource
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray,
                        textColor = Color.White
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



@Composable
fun ArticleItem(article: Article,navController: NavController) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate("news_article_screen")
                ArticleUrlSingleton.setArticleUrl(article.url)
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
