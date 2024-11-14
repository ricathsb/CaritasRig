package com.superbgoal.caritasrig.activity.homepage.build

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.Processor
import com.superbgoal.caritasrig.data.model.test.BuildManager
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
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(processors) { processor ->
                // Use ComponentCard for each processor
                ComponentCard(
                    title = processor.name,
                    details = "${processor.price}$ | ${processor.core_count} cores | ${processor.core_clock} GHz",
                    onAddClick = {
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString() // Dapatkan ID pengguna

                        // Use the BuildManager singleton to get the current build title
                        val buildTitle = BuildManager.getBuildTitle()

                        // Check if buildTitle is available before storing data in Firebase
                        buildTitle?.let { title ->
                            // Menyimpan processor dengan memanggil saveComponent
                                saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "cpu", // Menyimpan processor dengan tipe "cpu"
                                componentName = processor.name, // Nama processor
                                onSuccess = {
                                    Log.d("BuildActivity", "Processor ${processor.name} saved successfully under build title: $title")
                                },
                                onFailure = { errorMessage ->
                                    Log.e("BuildActivity", "Failed to store CPU under build title: ${errorMessage}")
                                }
                            )
                        } ?: run {
                            // Handle the case where buildTitle is null
                            Log.e("BuildActivity", "Build title is null; unable to store CPU.")
                        }
                    }
                )

            }

        }
    }
}
