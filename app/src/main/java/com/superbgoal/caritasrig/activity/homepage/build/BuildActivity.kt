@file:Suppress("NAME_SHADOWING")

package com.superbgoal.caritasrig.activity.homepage.build

import BuildViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.component.*
import com.superbgoal.caritasrig.activity.homepage.home.HomeActivity
import com.superbgoal.caritasrig.data.getDatabaseReference
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.saveBuildTitle


class BuildActivity : ComponentActivity() {
    private val buildViewModel: BuildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BuildScreen(buildViewModel)
            Log.d("BuildActivity", "onCreate called")
        }


        processIntent(intent)
    }
    override fun onResume() {
        super.onResume()
        intent?.let { processIntent(it) }
        buildViewModel.refreshBuildData()
        Log.d("BuildActivity", "onResume called")
    }


    private fun processIntent(intent: Intent?) {
        val buildData = intent?.getParcelableExtra<Build>("build")
        buildData?.let {
            buildViewModel.setBuildData(it)
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildScreen(buildViewModel: BuildViewModel = viewModel()) {
    getDatabaseReference()
    val context = LocalContext.current
    val loading by buildViewModel.loading.observeAsState(false) // Observe the loading state
    val buildTitle by buildViewModel.buildTitle.observeAsState("")
    var showDialog by remember { mutableStateOf(buildTitle.isEmpty()) }
    var dialogText by remember { mutableStateOf(buildTitle) }
    val selectedComponents by buildViewModel.selectedComponents.observeAsState(emptyMap())




    if (loading) {
        // Full-screen loading indicator
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )
        }
    } else {
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
                        title = {
                            Text(text = buildTitle.ifEmpty { "Build Name" })
                        },
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
                                    // Show the dialog to enter a new title
                                    showDialog = true
                                    dialogText = "" // Clear the input for a new title
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

                val activityMap = mapOf(
                    "CPU" to CpuActivity::class.java,
                    "Case" to CasingActivity::class.java,
                    "GPU" to VideoCardActivity::class.java,
                    "Motherboard" to MotherboardActivity::class.java,
                    "RAM" to MemoryActivity::class.java,
                    "InternalHardDrive" to InternalHardDriveActivity::class.java,
                    "PowerSupply" to PowerSupplyActivity::class.java,
                    "CPU Cooler" to CpuCoolerActivity::class.java,
                    "Headphone" to HeadphoneActivity::class.java,
                    "Keyboard" to KeyboardActivity::class.java,
                    "Mouse" to MouseActivity::class.java
                )


                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Log.d("BuildScreen", "Rendering LazyColumn with components: $selectedComponents")

                    // Iterasi melalui komponen yang dipilih
                    selectedComponents.forEach { (title, activityClass) ->
                        Log.d("BuildScreen", "Rendering component: $title with activity: $activityClass")

                        item {
                            // Dapatkan detail komponen berdasarkan kategori
                            val componentDetail = when (title) {
                                "CPU" -> buildViewModel.buildData.value?.components?.processor?.let {
                                    Log.d("BuildScreen", "CPU detail: ${it.name}")
                                    "Processor: ${it.name}\nCores: ${it.core_count}\nSpeed: ${it.core_clock} GHz"
                                }

                                "Case" -> buildViewModel.buildData.value?.components?.casing?.let {
                                    Log.d("BuildScreen", "Case detail: ${it.name}")
                                    "Case: ${it.name}\nType: ${it.type}"
                                }

                                "GPU" -> buildViewModel.buildData.value?.components?.videoCard?.let {
                                    Log.d("BuildScreen", "GPU detail: ${it.name}")
                                    "GPU: ${it.name}\nMemory: ${it.memory} GB"
                                }

                                "Motherboard" -> buildViewModel.buildData.value?.components?.motherboard?.let {
                                    Log.d("BuildScreen", "Motherboard detail: ${it.name}")
                                    "Motherboard: ${it.name}\nChipset: ${it.formFactor}"
                                }

                                "RAM" -> buildViewModel.buildData.value?.components?.memory?.let {
                                    Log.d("BuildScreen", "RAM detail: ${it.name}")
                                    "Memory: ${it.name}\nSize: ${it.pricePerGb} GB\nSpeed: ${it.speed} MHz"
                                }

                                "InternalHardDrive" -> buildViewModel.buildData.value?.components?.internalHardDrive?.let {
                                    Log.d("BuildScreen", "InternalHardDrive detail: ${it.name}")
                                    "InternalHardDrive: ${it.name}\nCapacity: ${it.capacity} GB"
                                }

                                "PowerSupply" -> buildViewModel.buildData.value?.components?.powerSupply?.let {
                                    Log.d("BuildScreen", "PowerSupply detail: ${it.name}")
                                    "Power Supply: ${it.name}\nWattage: ${it.wattage} W"
                                }

                                "CPU Cooler" -> buildViewModel.buildData.value?.components?.cpuCooler?.let {
                                    Log.d("BuildScreen", "CPU Cooler detail: ${it.name}")
                                    "CPU Cooler: ${it.name}\nFan Speed: ${it.rpm} RPM"
                                }

                                "Headphone" -> buildViewModel.buildData.value?.components?.headphone?.let {
                                    Log.d("BuildScreen", "Headphone detail: ${it.name}")
                                    "Headphone: ${it.name}\nType: ${it.type}"
                                }

                                "Keyboard" -> buildViewModel.buildData.value?.components?.keyboard?.let {
                                    Log.d("BuildScreen", "Keyboard detail: ${it.name}")
                                    "Keyboard: ${it.name}\nType: ${it.switches}"
                                }

                                "Mouse" -> buildViewModel.buildData.value?.components?.mouse?.let {
                                    Log.d("BuildScreen", "Mouse detail: ${it.name}")
                                    "Mouse: ${it.name}\nType: ${it.maxDpi}"
                                }

                                else -> {
                                    Log.d("BuildScreen", "No detail found for $title")
                                    null
                                }
                            }

                            // Log untuk komponen
                            Log.d("BuildScreen", "ComponentCard: $title, Detail: $componentDetail")

                            // Tampilkan ComponentCard
                            ComponentCard(
                                title = title,
                                onClick = {
                                    val activityClass = activityMap[title]
                                    if (activityClass != null) {
                                        context.startActivity(Intent(context, activityClass))
                                    } else {
                                        Log.e("BuildScreen", "Activity not found for $title")
                                    }
                                },
                                componentDetail = componentDetail,
                                category = title,
                                onRemove = {
                                    Log.d("BuildScreen", "$title Remove Button Clicked")
                                    buildViewModel.removeComponent(title)
                                }
                            )
                        }
                    }
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
                                    buildViewModel.saveBuildTitle(dialogText)
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
        )

    }
}



@Composable
fun ComponentCard(
    title: String,
    category: String, // Kategori komponen
    onClick: () -> Unit,
    componentDetail: String?,
    onRemove: () -> Unit // Callback untuk remove
) {
    val tag = "ComponentCard"

    Log.d(tag, "Rendering ComponentCard for $title with category $category")
    Log.d(tag, "Component detail: ${componentDetail ?: "No details available"}")

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
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontSize = 20.sp),
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )

            // Detail Section
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

                    // Periksa apakah detail komponen ada
                    if (!componentDetail.isNullOrEmpty()) {
                        Log.d(tag, "Displaying component detail for $title: $componentDetail")

                        // Tampilkan detail komponen
                        Text(
                            text = componentDetail,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Tombol Remove
                        Button(
                            onClick = {
                                Log.d(tag, "Remove button clicked for $title")
                                onRemove()
                            },
                            modifier = Modifier.background(Color.Transparent),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(text = "Remove Component")
                        }
                    } else {
                        Log.d(tag, "No component detail found for $title. Showing default view.")

                        // Tampilkan teks default untuk kondisi awal
                        Text(
                            text = "No $title Selected",
                            color = Color.Gray,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Tombol Add
                        Button(
                            onClick = {
                                Log.d(tag, "Add button clicked for $title")
                                onClick()
                            },
                            modifier = Modifier.background(Color.Transparent),
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
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}









