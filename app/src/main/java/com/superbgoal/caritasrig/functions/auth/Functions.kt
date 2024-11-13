// Function.kt
package com.superbgoal.caritasrig.functions.auth

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.getDatabaseReference
import com.superbgoal.caritasrig.data.model.Processor
import com.superbgoal.caritasrig.data.model.VideoCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    textColor: Color = Color.White,
    coroutineScope: CoroutineScope,
    onClick: suspend () -> Unit
) {
    var loadingState by remember { mutableStateOf(isLoading) }

    Box(modifier = modifier) {
        Button(
            onClick = {
                loadingState = true
                coroutineScope.launch {
                    onClick()
                    loadingState = false
                }
            },
            enabled = !loadingState,
            modifier = Modifier.fillMaxWidth(),
            colors = colors
        ) {
            Text(
                text = text,
                color = textColor
            )
        }

        if (loadingState) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Maintenance() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Under Maintenance :3",
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Add a simple content box to ensure something renders
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text= "Oops! We’re brewing up some cool updates. The site will be back online shortly. Thanks for hanging tight!",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
@Composable
fun ProcessorInfo(processor: Processor) {
    Text(text = "Name: ${processor.name}")
    Text(text = "Price: $${processor.price}")
    Text(text = "Cores: ${processor.core_count}")
    Text(text = "Core Clock: ${processor.core_clock} GHz")
    Text(text = "Boost Clock: ${processor.boost_clock} GHz")
    Text(text = "TDP: ${processor.tdp}W")
    Text(text = "Graphics: ${processor.graphics}")
    Text(text = "SMT: ${if (processor.smt) "Yes" else "No"}")
}

@Composable
fun VideoCardInfo(videoCard: VideoCard) {
    Text(text = "Name: ${videoCard.name}")
    Text(text = "Price: $${videoCard.price}")
    Text(text = "Memory: ${videoCard.memory} GB")
    Text(text = "Core Clock: ${videoCard.coreClock} MHz")
    Text(text = "Boost Clock: ${videoCard.boostClock} MHz")
//    Text(text = "TDP: ${videoCard.tdp}W")
}

@Composable
fun ComponentCard(
    title: String,
    details: String,
    onAddClick: () -> Unit,
    backgroundColor: Color = Color(0xFF3E2C47), // Default purple background color
    buttonColor: Color = Color(0xFF6E5768) // Default button color
) {
    Card(
        elevation = 4.dp,
        backgroundColor = backgroundColor, // Set background color in Card
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium, // Adjusted to h6 for visibility
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp)) // Spacing between title and details
                Text(
                    text = details,
                    style = MaterialTheme.typography.bodyMedium, // Adjusted for visibility
                    color = Color.White
                )
            }

            Button(
                onClick = onAddClick,
                colors = ButtonDefaults.buttonColors(buttonColor) // Set button color
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.add_btn), // Ensure this drawable exists
                    contentDescription = "Add Icon",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Add", color = Color.White)
            }
        }
    }
}

fun saveComponent(userId: String, buildTitle: String?, componentType: String, componentName: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val database = getDatabaseReference()
    buildTitle?.let { title ->
        database.child("users").child(userId).child("builds").child(title).child(componentType)
            .setValue(componentName)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure("Failed to save $componentType: ${error.message}")
            }
    } ?: run {
        // Handle the case where buildTitle is null, for example, log an error or show a message to the user
        Log.e("BuildManager", "Build title is null")
    }
}





