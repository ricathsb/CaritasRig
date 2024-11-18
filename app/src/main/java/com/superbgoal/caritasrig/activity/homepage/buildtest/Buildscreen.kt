package com.superbgoal.caritasrig.activity.homepage.buildtest

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.superbgoal.caritasrig.data.fetchBuildsWithAuth
import com.superbgoal.caritasrig.data.model.buildmanager.Build


@Composable
fun BuildListScreen(navController: NavController? = null) {
    // State untuk menyimpan daftar build
    val buildsState = produceState<List<Build>>(initialValue = emptyList()) {
        fetchBuildsWithAuth(
            onSuccess = { value = it },
            onFailure = { value = emptyList() }
        )
    }

    val builds = buildsState.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // Padding untuk isi layar
    ) {
        if (builds.isEmpty()) {
            // Tampilkan pesan jika tidak ada build
            Text(
                text = "No builds available",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color = Color.Gray
            )
        } else {
            // Aksi saat build diklik
            val onBuildClick: (Build) -> Unit = { build ->
                navController?.navigate("build_details/${build.title}")
                println("Clicked on build: ${build.title}")
            }
            // Panggil fungsi BuildList untuk menampilkan daftar build
            BuildList(
                builds = builds,
                onBuildClick = onBuildClick
            )
        }
    }
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