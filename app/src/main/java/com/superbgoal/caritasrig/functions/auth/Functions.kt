// Function.kt
package com.superbgoal.caritasrig.functions.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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





