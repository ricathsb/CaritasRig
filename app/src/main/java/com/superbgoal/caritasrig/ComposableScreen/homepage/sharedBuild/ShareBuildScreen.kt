package com.superbgoal.caritasrig.ComposableScreen.homepage.sharedBuild

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest.BuildViewModel
import com.superbgoal.caritasrig.data.model.buildmanager.BuildComponents
import com.superbgoal.caritasrig.data.model.buildmanager.SharedBuild
import com.superbgoal.caritasrig.functions.getDatabaseReference

@Composable
fun SharedBuildScreen() {
    // State untuk menyimpan daftar shared builds
    val viewModel : SharedBuildViewModel = viewModel()
    val sharedBuilds = remember { mutableStateOf<List<SharedBuild>>(emptyList()) }
    val loading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf("") }

    // Memanggil fungsi fetchSharedBuilds untuk mengambil data build yang dibagikan
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

    // Menampilkan layar berdasarkan status loading
    if (loading.value) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (errorMessage.value.isNotEmpty()) {
        Text(
            text = "Error: ${errorMessage.value}",
            color = Color.Red,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        )
    } else {
        // Menampilkan daftar build yang telah dibagikan
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            items(sharedBuilds.value) { build ->
                BuildListItem(build = build,viewModel)
            }
        }
    }
}

@Composable
fun BuildListItem(build: SharedBuild,viewModel: SharedBuildViewModel) {
    // Mendapatkan informasi user berdasarkan userId
    val userProfile = remember { mutableStateOf<UserProfile?>(null) }
    val userId = build.userId
    Log.d("BuildListItem", "imageuris: ${build.imageUrls}")

    // Ambil data profil pengguna berdasarkan userId jika belum ada
    LaunchedEffect(userId) {
        getUserProfile(userId) { profile ->
            userProfile.value = profile
            Log.d("BuildListItem", "User Profile: ${userProfile.value}")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .shadow(4.dp)
    ) {
        // Foto profil dan username
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            // Menggunakan gambar profil dummy
            AsyncImage(
                model = userProfile.value?.profileImageUrl ?: "https://preview.redd.it/sorry-youre-not-a-sigma-v0-gd50wax6celd1.jpg?width=640&crop=smart&auto=webp&s=447b4b71985dcbf033cd29820a3fdbaa9ecb3932",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = userProfile.value?.username ?: "Loading...",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Log.d("BuildListItem", "Build Title: ${build.buildImages}")

        // Gambar build yang bisa di-scroll ke samping
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            items(build.imageUrls ?: emptyList()) { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Build Image",
                    modifier = Modifier
                        .padding(8.dp)
                        .size(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop // Untuk memastikan gambar terpotong dengan baik
                )
            }
        }


        ComponentList(components = build.components ?: BuildComponents())

        // Tombol Save Build
        Button(
            onClick = {
                viewModel.saveSharedBuildToUserBuilds(targetUserId = Firebase.auth.currentUser?.uid!!, buildId = build.buildId, sharedUserId = build.userId,
                    onSuccess = {
                        Log.d("BuildListItem", "$userId ${build.userId} $")
                        Log.d("BuildListItem", "Build saved successfully")
                    },
                    onFailure = { error ->
                        Log.e("BuildListItem", "Error saving build: $error")
                })

            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Save Build", color = Color.White)
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







