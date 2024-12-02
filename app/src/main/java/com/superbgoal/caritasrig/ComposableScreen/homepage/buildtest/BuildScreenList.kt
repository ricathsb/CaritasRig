package com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.VideoSettings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.functions.SwipeToDeleteContainer
import com.superbgoal.caritasrig.functions.deleteBuild
import com.superbgoal.caritasrig.functions.editBuildTitle
import com.superbgoal.caritasrig.functions.fetchBuildsWithAuth


@Composable
fun BuildListScreen(navController: NavController? = null, viewModel: BuildViewModel) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val builds = remember { mutableStateOf<List<Build>>(emptyList()) }

    var showDialog by remember { mutableStateOf(false) }
    var editedBuildTitle by remember { mutableStateOf("") }

    // Fetch builds
    LaunchedEffect(Unit) {
        fetchBuildsWithAuth(
            onSuccess = { builds.value = it },
            onFailure = { builds.value = emptyList() }
        )
    }

    // Use a gradient background instead of a static image
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            if (builds.value.isEmpty()) {
                EmptyStateComposable()
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
                                builds.value = builds.value.filter { it.buildId != deletedBuild.buildId }
                            },
                            onFailure = { error ->
                                // Consider using a SnackBar for error messaging
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
        }

        // Floating action button with more prominent design
        FloatingActionButton(
            onClick = {
                navController?.navigate("build_details")
                viewModel.setNewBuildState(isNew = true)
            },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .size(56.dp)
                .shadow(8.dp, shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Build",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        if (showDialog) {
            EditBuildDialog(
                initialTitle = editedBuildTitle,
                onDismissRequest = { showDialog = false },
                onSave = { newTitle ->
                    val buildToEdit = builds.value.find { it.title == editedBuildTitle }
                    buildToEdit?.let { build ->
                        editBuildTitle(
                            userId = userId,
                            buildId = build.buildId,
                            newTitle = newTitle,
                            onSuccess = {
                                builds.value = builds.value.map {
                                    if (it.buildId == build.buildId) it.copy(title = newTitle) else it
                                }
                                showDialog = false
                            },
                            onFailure = { error ->
                                Log.e("EditBuild", error)
                            },
                            context = context
                        )
                    }
                }
            )
        }
    }
}


@Composable
fun EmptyStateComposable() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = "No Builds",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No builds available",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start by creating your first PC build",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
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
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(builds, key = { it.buildId }) { build ->
            SwipeToDeleteContainer(
                item = build,
                onDelete = { onDeleteBuild(it) },
                onEdit = { onEditBuild(it) }
            ) { buildItem ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onBuildClick(buildItem) }
                        .shadow(4.dp, shape = RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.surface,
                                        MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        // Build Title with more emphasis
                        Text(
                            text = buildItem.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // Components with icons and subtle styling
                        BuildComponentRow(
                            icon = Icons.Default.Memory,
                            label = "Processor",
                            value = buildItem.components?.processor?.name
                        )
                        BuildComponentRow(
                            icon = Icons.Default.Computer,
                            label = "Motherboard",
                            value = buildItem.components?.motherboard?.name
                        )
                        BuildComponentRow(
                            icon = Icons.Default.VideoSettings,
                            label = "Video Card",
                            value = buildItem.components?.videoCard?.name
                        )
                        // Add more components as needed
                    }
                }
            }
        }
    }
}

@Composable
fun BuildComponentRow(
    icon: ImageVector,
    label: String,
    value: String?
) {
    value?.let {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .size(24.dp)
                    .alpha(0.7f),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                )
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
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
    var isError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = "Edit Build Title",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = "Enter the new title for your build:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        isError = it.isBlank()
                    },
                    label = { Text("Build Title") },
                    singleLine = true,
                    isError = isError,
                    supportingText = {
                        if (isError) {
                            Text(
                                text = "Title cannot be empty",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title)
                    } else {
                        isError = true
                    }
                }
            ) {
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