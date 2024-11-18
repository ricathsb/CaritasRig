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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.component.Casing
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

import androidx.compose.ui.platform.LocalContext

class CasingActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference
    val buildTitle = BuildManager.getBuildTitle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase database reference
        val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
        database = FirebaseDatabase.getInstance(databaseUrl).reference
        val currentUser = FirebaseAuth.getInstance().currentUser
        val buildTitle = BuildManager.getBuildTitle()

        // Define the type explicitly for Gson TypeToken
        val typeToken = object : TypeToken<List<Casing>>() {}.type
        val casing: List<Casing> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.casing
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

                    // Main content with TopAppBar and CasingList
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
                                        text = "Casing",
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        // Navigate back to BuildActivity
//                                        val intent = Intent(this@CasingActivity, BuildActivity::class.java)
//                                        startActivity(intent)
//                                        finish()
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

                        // CasingList content
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            CasingList(casing)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CasingList(casings: List<Casing>) {
        // Get context from LocalContext
        val context = LocalContext.current

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(casings) { casing ->
                // Track loading state for each casing
                val isLoading = remember { mutableStateOf(false) }

                // Use ComponentCard for each casing
                ComponentCard(
                    title = casing.name,
                    details = "${casing.type} | ${casing.color} | PSU: ${casing.psu ?: "Not included"} | Volume: ${casing.externalVolume} L | 3.5\" Bays: ${casing.internal35Bays}",
                    context = context, // Passing context from LocalContext
                    component = casing,
                    isLoading = isLoading.value, // Pass loading state to card
                    onAddClick = {
                        // Start loading when the add button is clicked
                        isLoading.value = true
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString()

                        // Use the BuildManager singleton to get the current build title
                        val buildTitle = BuildManager.getBuildTitle()

                        buildTitle?.let { title ->
                            // Save the component to the database
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "case",
                                componentData = casing,
                                onSuccess = {
                                    // Stop loading on success
                                    isLoading.value = false
                                    Log.d("CasingActivity", "Casing ${casing.name} saved successfully under build title: $title")

                                    // After success, navigate to BuildActivity
//                                    val intent = Intent(context, BuildActivity::class.java).apply {
//                                        putExtra("component_title", casing.name)
//                                        putExtra("component_data", casing) // Component sent as Parcelable
//                                    }
//                                    context.startActivity(intent)
                                },
                                onFailure = { errorMessage ->
                                    // Stop loading on failure
                                    isLoading.value = false
                                    Log.e("CasingActivity", "Failed to store casing under build title: $errorMessage")
                                },
                                onLoading = { isLoading.value = it } // Update the loading state
                            )
                        } ?: run {
                            // Stop loading if buildTitle is null
                            isLoading.value = false
                            Log.e("CasingActivity", "Build title is null; unable to store casing.")
                        }
                    }
                )
            }
        }
    }
}

