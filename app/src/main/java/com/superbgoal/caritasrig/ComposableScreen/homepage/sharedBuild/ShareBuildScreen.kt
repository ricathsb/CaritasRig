package com.superbgoal.caritasrig.ComposableScreen.homepage.sharedBuild

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.buildmanager.BuildComponents
import com.superbgoal.caritasrig.data.model.buildmanager.SharedBuild
import com.superbgoal.caritasrig.functions.getDatabaseReference

@Composable
fun SharedBuildScreen() {
    val viewModel: SharedBuildViewModel = viewModel()
    val sharedBuilds = remember { mutableStateOf<List<SharedBuild>>(emptyList()) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf("") }

    viewModel.fetchSharedBuilds(
        onSuccess = { builds ->
            sharedBuilds.value = builds
            loading.value = false
        },
        onFailure = { error ->
            errorMessage.value = error
            loading.value = false
        }
    )
    // Elegant loading and error handling
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        when {
            loading.value -> CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(50.dp)
            )
            errorMessage.value.isNotEmpty() ->
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Error: ${errorMessage.value}",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            else ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .padding(horizontal = 8.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    items(sharedBuilds.value) { build ->
                        BuildListItem(build = build, viewModel)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
        }
    }
}

@Composable
fun BuildListItem(build: SharedBuild, viewModel: SharedBuildViewModel) {
    val userProfile = remember { mutableStateOf<UserProfile?>(null) }
    val userId = build.userId

    LaunchedEffect(userId) {
        getUserProfile(userId) { profile ->
            userProfile.value = profile
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // User Profile Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                val profileImageUrl = userProfile.value?.profileImageUrl
                val defaultProfileImage = "https://firebasestorage.googleapis.com/v0/b/caritas-rig.appspot.com/o/images%2F8MneE2YJoJXt1D2oSdepZuYWrvm2?alt=media&token=dec480bb-7223-47c7-9d01-af5609bad8fe"
                AsyncImage(
                    model = if(profileImageUrl.isNullOrEmpty()) defaultProfileImage else profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF473947), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = userProfile.value?.username ?: "Loading...",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = build.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
            )
            // Build Images
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(build.imageUrls ?: emptyList()) { imageUrl ->
                    Card(
                        modifier = Modifier
                            .width(200.dp)
                            .height(150.dp)
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Build Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Components List
            Text(
                text = "Build Components",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ComponentList(components = build.components ?: BuildComponents())

            Spacer(modifier = Modifier.height(16.dp))

            // Save Build Button
            Button(
                onClick = {
                    viewModel.saveSharedBuildToUserBuilds(
                        targetUserId = Firebase.auth.currentUser?.uid!!,
                        buildId = build.buildId,
                        sharedUserId = build.userId,
                        onSuccess = { Log.d("BuildListItem", "Build saved successfully") },
                        onFailure = { error -> Log.e("BuildListItem", "Error saving build: $error") }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF473947)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save Build")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Build", color = Color.White)
            }
        }
    }
}

fun getUserProfile(userId: String, onResult: (UserProfile?) -> Unit) {
    // Ambil data profil user berdasarkan userId
    val database = getDatabaseReference()
    database.child("users").child(userId).child("userData").get()
        .addOnSuccessListener { snapshot ->
            val profileImageUrl = snapshot.child("profileImageUrl").value as? String ?: ""
            val username = snapshot.child("username").value as? String ?: "Unknown"
            onResult(UserProfile(profileImageUrl, username))
        }
        .addOnFailureListener { error ->
            onResult(null)
            Log.e("Firebase", "Error fetching user profile: ${error.message}", error)
        }
}

data class UserProfile(
    val profileImageUrl: String,
    val username: String,
)

@Composable
fun ComponentList(components: BuildComponents) {
    // Daftar komponen dengan pengecekan null dan hanya menampilkan yang tidak null
    val componentData = listOf(
        "Processor" to components.processor?.name,
        "GPU" to components.videoCard?.name,
        "Motherboard" to components.motherboard?.name,
        "Casing" to components.casing?.name,
        "Memory" to components.memory?.name,
        "CPU Cooler" to components.cpuCooler?.name,
        "Internal Hard Drive" to components.internalHardDrive?.name,
        "Power Supply" to components.powerSupply?.name,
        "Keyboard" to components.keyboard?.name,
        "Mouse" to components.mouse?.name,
        "Headphones" to components.headphone?.name
    ).filter { it.second != null } // Hanya menyertakan komponen yang tidak null

    // Iterasi hanya pada komponen yang memiliki nilai
    componentData.forEach { (name, value) ->
        Text(
            text = "- $name = $value",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}






