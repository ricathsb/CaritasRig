package com.superbgoal.caritasrig.activity.homepage.newsApi

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.kwabenaberko.newsapilib.models.Article

@Composable
fun HomeScreen2(navController: NavController) {
    val viewModel = HomeViewModel2()
    val articles by viewModel.articles.observeAsState(emptyList())
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Judul
        Text(
            text = "Tech News Feed",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            color = Color.Red,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )

        TextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search articles...") },
            singleLine = true,
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.LightGray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        if(searchQuery.isNotEmpty()) {
            viewModel.fetchEverythingWithQuery(searchQuery)
        } else {
            viewModel.fetchNewsTopHeadlines()
        }
        // Filtered Articles
        LazyColumn {
            val filteredArticles = articles.filter { article ->
                article.title.contains(searchQuery, ignoreCase = true)
            }
            items(filteredArticles) { article ->
                ArticleItem(article,navController)
            }
        }
    }
}


@Composable
fun ArticleItem(article: Article,navController: NavController) {

    Card(
        modifier = Modifier.padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            navController.navigate("news_article_screen")
            val viewModel = HomeViewModel2()
            Log.d("ArticleItem", "Article URL: ${article.url}")
            ArticleUrlSingleton.setArticleUrl(article.url)
        }
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ){
            AsyncImage(
                model = article.urlToImage?:"https://via.placeholder.com/150",
                contentDescription = null,
                modifier = Modifier.size(80.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp)
            ) {
                Text(text = article.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 3
                )
                Text(text = article.source.name,
                    maxLines = 1,
                    fontSize = 12.sp
                )

            }
        }

    }


}
