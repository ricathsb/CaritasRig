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
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.superbgoal.caritasrig.data.model.component.Keyboard
import com.superbgoal.caritasrig.functions.ComponentCard
import com.superbgoal.caritasrig.functions.saveComponent
import com.superbgoal.caritasrig.functions.savedFavorite

@Composable
fun KeyboardScreen(navController: NavController) {
    val context = LocalContext.current
    val keyboards: List<Keyboard> = remember {
        loadItemsFromResources(
            context = context,
            resourceId = R.raw.keyboard // Make sure this resource exists
        )
    }

    // State for search text
    var searchText by remember { mutableStateOf("") }

    // Filtered list based on search text
    val filteredKeyboards = remember(searchText, keyboards) {
        if (searchText.isBlank()) keyboards
        else keyboards.filter { it.name.contains(searchText, ignoreCase = true) }
    }

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
                            text = "Keyboards",
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
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
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search Keyboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon"
                    )
                }
            )

            // Display the filtered list of keyboards
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent
            ) {
                KeyboardList(filteredKeyboards, navController)
            }
        }
    }
}


@Composable
fun KeyboardList(keyboards: List<Keyboard>, navController: NavController) {
    // Get context from LocalContext
    val context = LocalContext.current

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(keyboards) { keyboardItem ->
            // Track loading state for each keyboard
            val isLoading = remember { mutableStateOf(false) }

            // Use ComponentCard for each keyboard
            ComponentCard(
                title = keyboardItem.name,
                details = "Type: ${keyboardItem.name} | Color: ${keyboardItem.color} | Switch: ${keyboardItem.switches}",
                // Passing context from LocalContext
                component = keyboardItem,
                isLoading = isLoading.value, // Pass loading state to card
                onFavClick = {
                    savedFavorite(keyboard = keyboardItem, context = context)
                },
                onAddClick = {
                    // Start loading when the add button is clicked
                    isLoading.value = true
                    Log.d("KeyboardActivity", "Selected Keyboard: ${keyboardItem.name}")

                    // Get the current user and build title
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val userId = currentUser?.uid.toString()
                    val buildTitle = BuildManager.getBuildTitle()

                    // Save keyboard if buildTitle is available
                    buildTitle?.let { title ->
                        saveComponent(
                            userId = userId,
                            buildTitle = title,
                            componentType = "keyboard", // Specify component type
                            componentData = keyboardItem, // Pass keyboard data
                            onSuccess = {
                                navController.navigateUp()
                                isLoading.value = false
                                Log.d("KeyboardActivity", "Keyboard ${keyboardItem.name} saved successfully under build title: $title")

                                // Navigate to BuildActivity after success
                            },
                            onFailure = { errorMessage ->
                                // Stop loading on failure
                                isLoading.value = false
                                Log.e("KeyboardActivity", "Failed to store Keyboard under build title: $errorMessage")
                            },
                            onLoading = { isLoading.value = it } // Update loading state
                        )
                    } ?: run {
                        // Stop loading if buildTitle is null
                        isLoading.value = false
                        Log.e("KeyboardActivity", "Build title is null; unable to store Keyboard.")
                    }
                }
            )
        }
    }
}