package com.superbgoal.caritasrig.activity.build

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.Processor



class CpuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Define the type explicitly for Gson TypeToken
        val typeToken = object : TypeToken<List<Processor>>() {}.type
        val processors: List<Processor> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.processor
        )


        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProcessorList(processors)
                }
            }
        }
    }


    @Composable
    fun ProcessorList(processors: List<Processor>) {
        val background = painterResource(id = R.drawable.component_bg) // Load the background image

        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = background,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().fillMaxWidth(),
            )

            // LazyColumn for the list of processors
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth() // Ensure LazyColumn takes the full size
            ) {
                items(processors) { processor ->
                    ProcessorCard(processor)
                }
            }
        }
    }

@Composable
fun ProcessorCard(processor: Processor) {
    val cardColor = Color(0xff0473947)
    val textColor = Color.White
    Card(
        elevation = 4.dp,
        backgroundColor = cardColor,
        modifier = Modifier
            .background(cardColor)
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
                    text = processor.name,
                    style = MaterialTheme.typography.h6,
                    color = textColor
                )
                Text(
                    text = "${processor.core_count} cores @ ${processor.core_clock} GHz Up To ${processor.boost_clock} GHz",
                    style = MaterialTheme.typography.body2,
                    color = textColor

                )
            }
            Button(
                onClick = { /* Tambahkan aksi untuk tombol "Add" */ },
                modifier = Modifier
                    .background(cardColor)
                    .clip(MaterialTheme.shapes.large)



            ) {
                Text(
                    text = "+ Add",
                    color = cardColor
                )
            }
        }
    }
}

}
