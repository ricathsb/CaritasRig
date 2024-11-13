package com.superbgoal.caritasrig.activity.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.build.CasingActivity
import com.superbgoal.caritasrig.activity.homepage.build.CpuActivity
import com.superbgoal.caritasrig.activity.homepage.build.CpuCoolerActivity
import com.superbgoal.caritasrig.activity.homepage.build.HeadphoneActivity
import com.superbgoal.caritasrig.activity.homepage.build.InternalHardDriveActivity
import com.superbgoal.caritasrig.activity.homepage.build.KeyboardActivity
import com.superbgoal.caritasrig.activity.homepage.build.MemoryActivity
import com.superbgoal.caritasrig.activity.homepage.build.MotherboardActivity
import com.superbgoal.caritasrig.activity.homepage.build.MouseActivity
import com.superbgoal.caritasrig.activity.homepage.build.PowerSupplyActivity
import com.superbgoal.caritasrig.activity.homepage.build.VideoCardActivity
import com.superbgoal.caritasrig.data.getDatabaseReference
import com.superbgoal.caritasrig.data.model.test.BuildManager
import com.superbgoal.caritasrig.data.saveBuildTitle

class BuildActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            BuildScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen() {
    getDatabaseReference()
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf((BuildManager.getBuildTitle() ?: "").isEmpty()) }
    var dialogText by remember { mutableStateOf("") }

    // Main UI
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_build),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    modifier = Modifier.height(145.dp),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                context.startActivity(Intent(context, HomeActivity::class.java))
                            },
                            modifier = Modifier
                                .padding(start = 30.dp, top = 60.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_home),
                                contentDescription = "Home",
                                modifier = Modifier.size(80.dp),
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                // Show the dialog and clear the input field for a new title
                                showDialog = true
                                dialogText = "" // Clear the input for a new entry
                            },
                            modifier = Modifier
                                .padding(end = 30.dp, top = 60.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "Reset Build Title",
                                tint = Color.White,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp, 0.dp, 16.dp, 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ComponentCard(
                        title = "CPU",
                        onClick = {
                            val intent = Intent(context, CpuActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "Case",
                        onClick = {
                            val intent = Intent(context, CasingActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "GPU",
                        onClick = {
                            val intent = Intent(context, VideoCardActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "Motherboard",
                        onClick = {
                            val intent = Intent(context, MotherboardActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "RAM",
                        onClick = {
                            val intent = Intent(context, MemoryActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "Storage",
                        onClick = {
                            val intent = Intent(context, InternalHardDriveActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "Power Supply",
                        onClick = {
                            val intent = Intent(context, PowerSupplyActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "CPU Cooler",
                        onClick = {
                            val intent = Intent(context, CpuCoolerActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "Headphone",
                        onClick = {
                            val intent = Intent(context, HeadphoneActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "Keyboard",
                        onClick = {
                            val intent = Intent(context, KeyboardActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
                item {
                    ComponentCard(
                        title = "Mouse",
                        onClick = {
                            val intent = Intent(context, MouseActivity::class.java)
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    // Show dialog only if showDialog is true
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Do nothing to prevent dismissing the dialog */ },
            title = {
                Text(text = "Enter Build Title")
            },
            text = {
                Column {
                    Text(text = "Please enter a title for your build:")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = dialogText,
                        onValueChange = { dialogText = it },
                        placeholder = { Text(text = "Build Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (dialogText.isNotEmpty()) {
                            saveBuildTitle(
                                userId = Firebase.auth.currentUser?.uid ?: "",
                                buildTitle = dialogText,
                                onSuccess = {
                                    showDialog = false
                                    BuildManager.setBuildTitle(dialogText)
                                },
                                onFailure = { errorMessage ->
                                    Log.e("BuildScreen", errorMessage)
                                }
                            )
                        }
                    },
                    enabled = dialogText.isNotEmpty()
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Dismiss the dialog and navigate to HomeActivity
                        context.startActivity(Intent(context, HomeActivity::class.java))
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}



@Composable
fun ComponentCard(
    title: String,
    onClick: () -> Unit // Menambahkan parameter onClick
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(id = R.color.brown)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Spacer(modifier = Modifier.height(5.dp))

                    Button(
                        onClick = { onClick() }, // Menambahkan onClick ke Button
                        modifier = Modifier
                            .background(Color.Transparent),
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.add_btn),
                            contentDescription = "Add Icon",
                            tint = Color.Unspecified,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Add Component")
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

