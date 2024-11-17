package com.superbgoal.caritasrig.activity.homepage.component

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.build.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.component.InternalHardDrive
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard
import com.superbgoal.caritasrig.functions.auth.saveComponent

class InternalHardDriveActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val buildTitle = BuildManager.getBuildTitle()


        // Mengisi data dari file JSON untuk InternalHardDrive
        val typeToken = object : TypeToken<List<InternalHardDrive>>() {}.type
        val internalHardDrives: List<InternalHardDrive> = loadItemsFromResources(
            context = this,
            resourceId = R.raw.internalharddrive
        )

        setContent {
            MaterialTheme {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Menambahkan Image sebagai background
                    Image(
                        painter = painterResource(id = R.drawable.component_bg),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Konten utama dengan TopAppBar dan InternalHardDriveList
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
                                        text = "Internal Hard Drive",
                                        style = MaterialTheme.typography.subtitle1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(
                                    onClick = {
                                        val intent = Intent(this@InternalHardDriveActivity, BuildActivity::class.java)
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
                                        // Aksi untuk tombol filter
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

                        // Konten InternalHardDriveList
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = Color.Transparent
                        ) {
                            InternalHardDriveList(internalHardDrives)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun InternalHardDriveList(internalHardDrives: List<InternalHardDrive>) {
        // Get context from LocalContext
        val context = LocalContext.current

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(internalHardDrives) { hardDrive ->
                // Track loading state for each hard drive
                val isLoading = remember { mutableStateOf(false) }

                // Use ComponentCard for each hard drive
                ComponentCard(
                    title = hardDrive.name,
                    details = "Capacity: ${hardDrive.capacity}GB | Price per GB: \$${hardDrive.pricePerGb} | Type: ${hardDrive.type} | Cache: ${hardDrive.cache}MB | Form Factor: ${hardDrive.formFactor} | Interface: ${hardDrive.interfacee}",
                    context = context, // Passing context from LocalContext
                    component = hardDrive,
                    isLoading = isLoading.value, // Pass loading state to card
                    onAddClick = {
                        // Start loading when the add button is clicked
                        isLoading.value = true

                        // Get userId and buildTitle
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString()
                        val buildTitle = BuildManager.getBuildTitle()

                        // Save hard drive if buildTitle is available
                        buildTitle?.let { title ->
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "internalharddrive", // Specify component type
                                componentData = hardDrive, // Pass hard drive data
                                onSuccess = {
                                    // Stop loading on success
                                    isLoading.value = false
                                    Log.d("HardDriveActivity", "Hard Drive ${hardDrive.name} saved successfully under build title: $title")

                                    // Navigate to BuildActivity after success
                                    val intent = Intent(context, BuildActivity::class.java).apply {
                                        putExtra("component_title", hardDrive.name)
                                        putExtra("component_data", hardDrive) // Component sent as Parcelable
                                    }
                                    context.startActivity(intent)
                                },
                                onFailure = { errorMessage ->
                                    // Stop loading on failure
                                    isLoading.value = false
                                    Log.e("HardDriveActivity", "Failed to store Hard Drive under build title: $errorMessage")
                                },
                                onLoading = { isLoading.value = it } // Update loading state
                            )
                        } ?: run {
                            // Stop loading if buildTitle is null
                            isLoading.value = false
                            Log.e("HardDriveActivity", "Build title is null; unable to store Hard Drive.")
                        }
                    }
                )
            }
        }
    }
}
