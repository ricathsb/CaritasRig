package com.superbgoal.caritasrig.activity.homepage.component

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.superbgoal.caritasrig.activity.homepage.build.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.component.Mouse
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

class MouseActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    val buildTitle = BuildManager.getBuildTitle()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase database reference
        val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(databaseUrl).reference
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Define the type explicitly for Gson TypeToken
        val typeToken = object : TypeToken<List<Mouse>>() {}.type
        val mice: List<Mouse> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.mouse
        )

        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Background Image
                    Image(
                        painter = painterResource(id = R.drawable.component_bg),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Main content with TopAppBar and Mouse List
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
                                        text = "Mice",
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        // Navigate back to BuildActivity
                                        val intent = Intent(this@MouseActivity, BuildActivity::class.java)
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

                        // Mouse List content
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            MouseList(mice, currentUser?.uid.toString())
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun MouseList(mice: List<Mouse>, userId: String) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mice) { mouseItem ->
                ComponentCard(
                    title = mouseItem.name,
                    details = "Type: ${mouseItem.name} | DPI: ${mouseItem.maxDpi} | Color: ${mouseItem.color}",
                    onAddClick = {
                        Log.d("MouseActivity", "Selected Mouse: ${mouseItem.name}")

                        // Get the current user and build title
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString()

                        // Use the BuildManager singleton to get the current build title
                        val buildTitle = BuildManager.getBuildTitle()

                        // Check if buildTitle is available before storing data in Firebase
                        buildTitle?.let { title ->
                            // Menyimpan mouse menggunakan fungsi saveComponent
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "mouse", // Menyimpan mouse dengan tipe "mouse"
                                componentData = mouseItem, // Nama mouse
                                onSuccess = {
                                    Log.d("MouseActivity", "Mouse ${mouseItem.name} saved successfully under build title: $title")
                                },
                                onFailure = { errorMessage ->
                                    Log.e("MouseActivity", "Failed to store Mouse under build title: ${errorMessage}")
                                }
                            )
                        } ?: run {
                            // Handle the case where buildTitle is null
                            Log.e("MouseActivity", "Build title is null; unable to store Mouse.")
                        }
                    }
                )

            }
        }
    }
}
