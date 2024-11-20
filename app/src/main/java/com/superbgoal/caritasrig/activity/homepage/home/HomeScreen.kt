package com.superbgoal.caritasrig.activity.homepage.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.model.buildmanager.Build

@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Log.d("user", user.toString())
                if (user != null) {
                    UserProfile(viewModel)
                } else if (errorMessage != null) {
                    ErrorMessage(errorMessage!!)
                } else {
                    LoadingScreen()
                }
            }
        }
    }
}


@Composable
fun ProfileIcon(user: User?, onLogout: () -> Unit, showDialog: Boolean, toggleDialog: () -> Unit) {
    IconButton(onClick = toggleDialog) {
        if (user?.profileImageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.profileImageUrl)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile Icon",
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun UserProfile(viewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Bar
        TextField(
            shape = RoundedCornerShape(25.dp),
            value = viewModel.searchText.collectAsState().value,
            onValueChange = { viewModel.updateSearchText(it) },
            label = { Text(stringResource(id = R.string.search)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
            },
            trailingIcon = {
                if (viewModel.searchText.collectAsState().value.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchText("") }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
            }
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

fun getCurrentUserEmail(): String? {
    val currentUser = FirebaseAuth.getInstance().currentUser
    return currentUser?.email
}


@Composable
fun BuildList(builds: List<Build>, onBuildClick: (Build) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(builds) { build ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        // Action on card click
                        onBuildClick(build)  // Pass the clicked build to the onBuildClick function
                    },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Display build title
                    Text(
                        text = build.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Display processor name if available
                    build.components?.processor?.let { processor ->
                        Text(
                            text = "Processor: ${processor.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display casing name if available
                    build.components?.casing?.let { casing ->
                        Text(
                            text = "Casing: ${casing.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display motherboard name if available
                    build.components?.motherboard?.let { motherboard ->
                        Text(
                            text = "Motherboard: ${motherboard.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display video card name if available
                    build.components?.videoCard?.let { videoCard ->
                        Text(
                            text = "Video Card: ${videoCard.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display headphone name if available
                    build.components?.headphone?.let { headphone ->
                        Text(
                            text = "Headphone: ${headphone.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display internal hard drive name if available
                    build.components?.internalHardDrive?.let { internalHardDrive ->
                        Text(
                            text = "Internal Hard Drive: ${internalHardDrive.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Log.d("BuildList", "Displaying internal hard drive name: ${internalHardDrive.name}")
                    }

                    // Display keyboard name if available
                    build.components?.keyboard?.let { keyboard ->
                        Text(
                            text = "Keyboard: ${keyboard.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    build.components?.powerSupply?.let { powerSupply ->
                        Text(
                            text = "Power Supply: ${powerSupply.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    build.components?.mouse?.let { mouse ->
                        Text(
                            text = "Mouse: ${mouse.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}