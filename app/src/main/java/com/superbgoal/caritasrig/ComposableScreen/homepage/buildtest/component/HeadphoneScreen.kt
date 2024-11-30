package com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest.component

import android.util.Log
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.Headphones
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun HeadphoneScreen(navController: NavController) {
    // Load headphone data
    val context = LocalContext.current
    val headphones: List<Headphones> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.headphones
        )
    }

    // State for search text
    var searchText by remember { mutableStateOf("") }

    // Filtered list based on search text
    val filteredHeadphones = remember(searchText, headphones) {
        if (searchText.isBlank()) headphones
        else headphones.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Main content with TopAppBar and HeadphoneList
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
                            text = "Headphones",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(start = 20.dp, top = 10.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                }
            )

            // Search Bar
            androidx.compose.material.TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search Headphones") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search), // Replace with your search icon
                        contentDescription = "Search Icon"
                    )
                }
            )

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                // Pass the filtered list to HeadphoneList
                HeadphoneList(filteredHeadphones, navController)
            }
        }
    }
}

@Composable
fun HeadphoneList(headphones: List<Headphones>, navController: NavController) {
    // Get context from LocalContext
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(headphones) { headphone ->
            // Track loading state for each headphone
            val isLoading = remember { mutableStateOf(false) }

            ComponentCard(
                title = headphone.name,
                details = "Type: ${headphone.type} | Color: ${headphone.color} | Frequency Response: ${headphone.frequencyResponse} Hz",
                // Passing context from LocalContext
                component = headphone,
                isLoading = isLoading.value, // Pass loading state to card
                onFavClick = {
                    savedFavorite(headphones = headphone, context = context)
                },
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
                            componentType = "headphone", // Specify the component type
                            componentData = headphone, // Pass headphone data
                            onSuccess = {
                                // Stop loading on success
                                isLoading.value = false
                                Log.d("HeadphoneActivity", "Headphone ${headphone.name} saved successfully under build title: $title")
                                navController.navigateUp()
                                // After success, navigate to BuildActivity
                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("HeadphoneActivity", "Failed to store headphone under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it } // Update the loading state
                        )
                    } ?: run {
                        // Stop loading if buildTitle is null
                        isLoading.value = false
                        Log.e("HeadphoneActivity", "Build title is null; unable to store headphone.")
                    }
                }
            )
        }
    }
}