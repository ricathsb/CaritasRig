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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
import com.superbgoal.caritasrig.data.model.Motherboard
import com.superbgoal.caritasrig.data.model.test.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

class MotherboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val buildTitle = BuildManager.getBuildTitle()


        // Define the type explicitly for Gson TypeToken
        val typeToken = object : TypeToken<List<Motherboard>>() {}.type
        val motherboards: List<Motherboard> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.motherboard // Ensure this JSON file exists in resources
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

                    // Main content with TopAppBar and MotherboardList
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
                                        text = "Motherboard",
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(this@MotherboardActivity, BuildActivity::class.java)
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
                                        // Action for filter button
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

                        // Motherboard List content
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            MotherboardList(motherboards)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MotherboardList(motherboards: List<Motherboard>) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(motherboards) { motherboard ->
                // Use ComponentCard for each motherboard
                ComponentCard(
                    title = motherboard.name,
                    details = "Socket: ${motherboard.socket} | Form Factor: ${motherboard.formFactor} | Max Memory: ${motherboard.maxMemory}GB | Slots: ${motherboard.memorySlots} | Color: ${motherboard.color}",
                    onAddClick = {
                        Log.d("MotherboardActivity", "Selected Motherboard: ${motherboard.name}")

                        // Get the current user and build title
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString()

                        // Use the BuildManager singleton to get the current build title
                        val buildTitle = BuildManager.getBuildTitle()

                        // Check if buildTitle is available before storing data in Firebase
                        buildTitle?.let { title ->
                            // Menyimpan motherboard menggunakan fungsi saveComponent
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "motherboard", // Menyimpan motherboard dengan tipe "motherboard"
                                componentName = motherboard.name, // Nama motherboard
                                onSuccess = {
                                    Log.d("MotherboardActivity", "Motherboard ${motherboard.name} saved successfully under build title: $title")
                                },
                                onFailure = { errorMessage ->
                                    Log.e("MotherboardActivity", "Failed to store Motherboard under build title: ${errorMessage}")
                                }
                            )
                        } ?: run {
                            // Handle the case where buildTitle is null
                            Log.e("MotherboardActivity", "Build title is null; unable to store Motherboard.")
                        }
                    }
                )

            }
        }
    }
}
