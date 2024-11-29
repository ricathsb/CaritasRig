package com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.functions.SwipeToDeleteContainer
import com.superbgoal.caritasrig.functions.deleteBuild
import com.superbgoal.caritasrig.functions.editBuildTitle
import com.superbgoal.caritasrig.functions.fetchBuildsWithAuth


@Composable
fun BuildListScreen(navController: NavController? = null, viewModel: BuildViewModel) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val builds = remember { mutableStateOf<List<Build>>(emptyList()) } // Gunakan state

    var showDialog by remember { mutableStateOf(false) }
    var editedBuildTitle by remember { mutableStateOf("") }

    // Fetch builds menggunakan efek samping
    LaunchedEffect(Unit) {
        fetchBuildsWithAuth(
            onSuccess = { builds.value = it },
            onFailure = { builds.value = emptyList() }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (builds.value.isEmpty()) {
            Text(
                text = "No builds available",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                color = Color.Gray
            )
        } else {
            val onBuildClick: (Build) -> Unit = { build ->
                navController?.navigate("build_details")
                viewModel.saveBuildTitle(build.title)
            }
            BuildList(
                builds = builds.value,
                onBuildClick = onBuildClick,
                onDeleteBuild = { deletedBuild ->
                    deleteBuild(
                        userId = userId,
                        buildId = deletedBuild.buildId,
                        onSuccess = {
                            Log.d("DeleteBuild", "Build deleted successfully")
                            builds.value = builds.value.filter { it.buildId != deletedBuild.buildId }
                        },
                        onFailure = { error ->
                            Log.e("DeleteBuild", error)
                        }
                    )
                },
                onEditBuild = { editedBuild ->
                    editedBuildTitle = editedBuild.title
                    showDialog = true
                }
            )
        }
        FloatingActionButton(
            onClick = {
                navController?.navigate("build_details")
                viewModel.setNewBuildState(isNew = true)
            },
            shape = RoundedCornerShape(8.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Build",
                tint = Color.White
            )
        }

        if (showDialog) {
            EditBuildDialog(
                initialTitle = editedBuildTitle,
                onDismissRequest = { showDialog = false },
                onSave = { newTitle ->
                    val buildToEdit = builds.value.find { it.title == editedBuildTitle }
                    if (buildToEdit != null) {
                        editBuildTitle(
                            userId = userId,
                            buildId = buildToEdit.buildId,
                            newTitle = newTitle,
                            onSuccess = {
                                Log.d("EditBuild", "Title updated successfully to: $newTitle")
                                builds.value = builds.value.map {
                                    if (it.buildId == buildToEdit.buildId) it.copy(title = newTitle) else it
                                }
                                showDialog = false
                            },
                            onFailure = { error ->
                                Log.e("EditBuild", error)
                            },
                            context = context
                        )
                    } else {
                        Log.e("EditBuild", "Build not found for title: $editedBuildTitle")
                        showDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun BuildList(
    builds: List<Build>,
    onBuildClick: (Build) -> Unit,
    onDeleteBuild: (Build) -> Unit,
    onEditBuild: (Build) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(builds, key = { it.buildId }) { build ->
            SwipeToDeleteContainer(
                item = build,
                onDelete = { onDeleteBuild(it) },
                onEdit = {onEditBuild(it)}
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
@Composable
fun EditBuildDialog(
    initialTitle: String,
    onDismissRequest: () -> Unit,
    onSave: (String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Edit Build Title")
        },
        text = {
            Column {
                Text(text = "Enter the new title for your build:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = "Build Title") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(title) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
