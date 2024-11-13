package com.superbgoal.caritasrig.activity.homepage.build

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.reflect.TypeToken
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.BuildActivity
import com.superbgoal.caritasrig.data.loadItemsFromResources
import com.superbgoal.caritasrig.data.model.InternalHardDrive
import com.superbgoal.caritasrig.data.model.test.BuildManager
import com.superbgoal.caritasrig.functions.auth.ComponentCard

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
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(internalHardDrives) { hardDrive ->
                // Menggunakan ComponentCard untuk setiap item hard drive
                ComponentCard(
                    title = hardDrive.name,
                    details = "Capacity: ${hardDrive.capacity}GB | Price per GB: \$${hardDrive.pricePerGb} | Type: ${hardDrive.type} | Cache: ${hardDrive.cache}MB | Form Factor: ${hardDrive.formFactor} | Interface: ${hardDrive.interfacee}",
                    onAddClick = {
                        val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
                        val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        database.child("users").child(currentUser?.uid.toString()).child("build").child("internalHardDrive").setValue(hardDrive.name)

                        setResult(RESULT_OK, intent)
                        finish()
                    }
                )
            }
        }
    }
}
