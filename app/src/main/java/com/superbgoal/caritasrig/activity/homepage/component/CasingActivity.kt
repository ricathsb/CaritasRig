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
import com.superbgoal.caritasrig.data.model.component.Casing
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

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
                                        val intent = Intent(this@CasingActivity, BuildActivity::class.java)
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

                        // CasingList content
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            CasingList(casing, currentUser?.uid.toString())
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun CasingList(casing: List<Casing>, userId: String) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(casing) { casingItem ->
                Log.d("CasingActivity", "Rendering casing item: ${casingItem.name}")
                ComponentCard(
                    title = casingItem.name,
                    details = "${casingItem.type} | ${casingItem.color} | PSU: ${casingItem.psu ?: "Not included"} | Volume: ${casingItem.externalVolume} L | 3.5\" Bays: ${casingItem.internal35Bays}",
                    onAddClick = {
                        Log.d("CasingActivity", "Selected Casing: ${casingItem.name}")
                        buildTitle?.let { title ->
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "case",
                                componentData = casingItem,
                                onSuccess = {
                                    Log.d("CasingActivity", "Casing saved successfully")
                                },
                                onFailure = { errorMessage ->
                                    Log.e("CasingActivity", errorMessage)
                                }
                            )
                        } ?: run {
                            // Handle case where buildTitle is null
                            Log.e("BuildManager", "Build title is null")
                        }
                    }
                )
            }
        }
    }
}
