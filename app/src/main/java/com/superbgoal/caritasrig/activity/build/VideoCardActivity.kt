package com.superbgoal.caritasrig.activity.build

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.VideoCard

class VideoCardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define the type explicitly for Gson TypeToken
        val typeToken = object : TypeToken<List<VideoCard>>() {}.type
        val videoCards: List<VideoCard> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.videocard
        )

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    VideoCardList(videoCards)
                }
            }
        }
    }

    @Composable
    fun VideoCardList(videoCards: List<VideoCard>) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(videoCards) { videoCard ->
                VideoCardCard(videoCard)
            }
        }
    }

    @Composable
    fun VideoCardCard(videoCard: VideoCard) {
        Card(
            elevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = videoCard.name,
                        style = MaterialTheme.typography.h6
                    )
                    Text(
                        text = "Chipset: ${videoCard.chipset} | ${videoCard.memory}GB | Core Clock: ${videoCard.coreClock}MHz | Boost Clock: ${videoCard.boostClock}MHz | Color: ${videoCard.color} | Length: ${videoCard.length}mm",
                        style = MaterialTheme.typography.body2
                    )
                }
                Button(onClick = { /* Tambahkan aksi untuk tombol "Add" */ }) {
                    Text(text = "Add")
                }
            }
        }
    }
}
