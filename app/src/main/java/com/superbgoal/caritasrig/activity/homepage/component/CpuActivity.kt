package com.superbgoal.caritasrig.activity.homepage.component

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.build.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

class CpuActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load processors from resources
        val processors: List<Processor> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.processor
        )

        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Set background image
                    Image(
                        painter = painterResource(id = R.drawable.component_bg),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Main content with TopAppBar and ProcessorList
                    Column {
                        TopAppBar(
                            backgroundColor = Color.Transparent,
                            contentColor = Color.White,
                            elevation = 0.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            title = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, bottom = 10.dp)
                                ) {
                                    Text(
                                        text = "Part Pick",
                                        style = MaterialTheme.typography.h4,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "CPU",
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(this@CpuActivity, BuildActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    },
                                    modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_back),
                                        contentDescription = "Back"
                                    )
                                }
                            },
                            actions = {
                                IconButton(
                                    onClick = {
                                        // Action for filter button (if needed)
                                    },
                                    modifier = Modifier.padding(end = 20.dp, top = 10.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_filter),
                                        contentDescription = "Filter"
                                    )
                                }
                            }
                        )

                        // Processor List content
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            ProcessorList(processors)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ProcessorList(processors: List<Processor>) {
        // Get context from LocalContext
        val context = LocalContext.current

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(processors) { processor ->
                // Track loading state for each processor
                val isLoading = remember { mutableStateOf(false) }

                // Use ComponentCard for each processor
                ComponentCard(
                    title = processor.name,
                    details = "${processor.price}$ | ${processor.core_count} cores | ${processor.core_clock} GHz",
                    context = context, // Passing context from LocalContext
                    component = processor,
                    isLoading = isLoading.value, // Pass loading state to card
                    onAddClick = {
                        // Start loading when the add button is clicked
                        isLoading.value = true
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString()

                        // Use the BuildManager singleton to get the current build title
                        val buildTitle = BuildManager.getBuildTitle()

                        buildTitle?.let { title ->
                            // Simpan komponen ke database
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "cpu",
                                componentData = processor,
                                onSuccess = {
                                    // Stop loading on success
                                    isLoading.value = false
                                    Log.d("BuildActivity", "Processor ${processor.name} saved successfully under build title: $title")

                                    // Setelah sukses, pindahkan ke BuildActivity
                                    val intent = Intent(context, BuildActivity::class.java).apply {
                                        putExtra("component_title", processor.name)
                                        putExtra("component_data", processor) // Komponen yang dikirim sebagai Parcelable
                                    }
                                    context.startActivity(intent)
                                },
                                onFailure = { errorMessage ->
                                    // Stop loading on failure
                                    isLoading.value = false
                                    Log.e("BuildActivity", "Failed to store CPU under build title: $errorMessage")
                                },
                                onLoading = { isLoading.value = it } // Update the loading state
                            )
                        } ?: run {
                            // Stop loading if buildTitle is null
                            isLoading.value = false
                            Log.e("BuildActivity", "Build title is null; unable to store CPU.")
                        }
                    }
                )
            }
        }
    }
}
