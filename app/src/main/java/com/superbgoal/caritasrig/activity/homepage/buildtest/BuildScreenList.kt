package com.superbgoal.caritasrig.activity.homepage.buildtest

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.data.deleteBuild
import com.superbgoal.caritasrig.data.fetchBuildsWithAuth
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.functions.auth.SwipeToDeleteContainer


@Composable
fun BuildListScreen(navController: NavController? = null,viewModel: BuildViewModel) {
    val buildsState = produceState<List<Build>>(initialValue = emptyList()) {
        fetchBuildsWithAuth(
            onSuccess = { value = it },
            onFailure = { value = emptyList() }
        )
    }
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var builds = buildsState.value

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
                navController?.navigate("build_details")
                viewModel.saveBuildTitle(build.title)
            }
            // Panggil fungsi BuildList untuk menampilkan daftar build
            BuildList(
                builds = builds,
                onBuildClick = onBuildClick,
                onDeleteBuild = { deletedBuild ->
                    deleteBuild(
                        userId = userId,
                        buildId = deletedBuild.buildId,
                        onSuccess = {
                            Log.d("DeleteBuild", "Build deleted successfully")
                        },
                        onFailure = { error ->
                            Log.e("DeleteBuild", error)
                        }
                    )
                    builds = builds.filter { it.buildId != deletedBuild.buildId}
                    Log.d("MyScreen", "Deleted build: ${deletedBuild.buildId}")
                }

            )
        }

        FloatingActionButton(
            onClick = {
                println("FAB clicked!")
                // Navigasi ke layar dengan title "new"
                navController?.navigate("build_details")
                viewModel.setNewBuildState(isNew = true)
            },
            shape = RoundedCornerShape(8.dp), // Membuat tombol berbentuk kotak
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // Padding dari tepi layar
                .size(48.dp) // Ukuran kecil
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Build",
                tint = Color.White
            )
        }
    }
}




@Composable
fun BuildList(
    builds: List<Build>,
    onBuildClick: (Build) -> Unit,
    onDeleteBuild: (Build) -> Unit // Tambahkan callback untuk menghapus build
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(builds, key = { it.buildId }) { build ->
            SwipeToDeleteContainer(
                item = build,
                onDelete = { onDeleteBuild(it) }
            ) { buildItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onBuildClick(buildItem)
                        },
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Display build title
                        Text(
                            text = buildItem.title,
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Display processor name if available
                        buildItem.components?.processor?.let { processor ->
                            Text(
                                text = "Processor: ${processor.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Display casing name if available
                        buildItem.components?.casing?.let { casing ->
                            Text(
                                text = "Casing: ${casing.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Display motherboard name if available
                        buildItem.components?.motherboard?.let { motherboard ->
                            Text(
                                text = "Motherboard: ${motherboard.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Display video card name if available
                        buildItem.components?.videoCard?.let { videoCard ->
                            Text(
                                text = "Video Card: ${videoCard.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Display headphone name if available
                        buildItem.components?.headphone?.let { headphone ->
                            Text(
                                text = "Headphone: ${headphone.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        // Display internal hard drive name if available
                        buildItem.components?.internalHardDrive?.let { internalHardDrive ->
                            Text(
                                text = "Internal Hard Drive: ${internalHardDrive.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Log.d("BuildList", "Displaying internal hard drive name: ${internalHardDrive.name}")
                        }

                        // Display keyboard name if available
                        buildItem.components?.keyboard?.let { keyboard ->
                            Text(
                                text = "Keyboard: ${keyboard.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        buildItem.components?.powerSupply?.let { powerSupply ->
                            Text(
                                text = "Power Supply: ${powerSupply.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }

                        buildItem.components?.mouse?.let { mouse ->
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
}
