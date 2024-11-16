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
        val context = LocalContext.current
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(internalHardDrives) { hardDrive ->
                // Menggunakan ComponentCard untuk setiap item hard drive
                ComponentCard(
                    title = hardDrive.name,
                    details = "Capacity: ${hardDrive.capacity}GB | Price per GB: \$${hardDrive.pricePerGb} | Type: ${hardDrive.type} | Cache: ${hardDrive.cache}MB | Form Factor: ${hardDrive.formFactor} | Interface: ${hardDrive.interfacee}",
                    context = context,
                    onAddClick = {
                        Log.d("HardDriveActivity", "Selected Hard Drive: ${hardDrive.name}")

                        // Get the current user and build title
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        val userId = currentUser?.uid.toString()

                        // Use the BuildManager singleton to get the current build title
                        val buildTitle = BuildManager.getBuildTitle()

                        // Check if buildTitle is available before storing data in Firebase
                        buildTitle?.let { title ->
                            // Menyimpan hard drive menggunakan fungsi saveComponent
                            saveComponent(
                                userId = userId,
                                buildTitle = title,
                                componentType = "internalharddrive", // Menyimpan hard drive dengan tipe "internalHardDrive"
                                componentData = hardDrive, // Nama hard drive
                                onSuccess = {
                                    Log.d("HardDriveActivity", "Hard Drive ${hardDrive.name} saved successfully under build title: $title")
                                },
                                onFailure = { errorMessage ->
                                    Log.e("HardDriveActivity", "Failed to store Hard Drive under build title: ${errorMessage}")
                                }
                            )
                        } ?: run {
                            // Handle the case where buildTitle is null
                            Log.e("HardDriveActivity", "Build title is null; unable to store Hard Drive.")
                        }

                        // Return to the previous activity
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                )

            }
        }
    }
}
