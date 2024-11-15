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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.component.CasingActivity
import com.superbgoal.caritasrig.activity.homepage.component.CpuActivity
import com.superbgoal.caritasrig.activity.homepage.component.CpuCoolerActivity
import com.superbgoal.caritasrig.activity.homepage.component.HeadphoneActivity
import com.superbgoal.caritasrig.activity.homepage.component.InternalHardDriveActivity
import com.superbgoal.caritasrig.activity.homepage.component.KeyboardActivity
import com.superbgoal.caritasrig.activity.homepage.component.MemoryActivity
import com.superbgoal.caritasrig.activity.homepage.component.MotherboardActivity
import com.superbgoal.caritasrig.activity.homepage.component.MouseActivity
import com.superbgoal.caritasrig.activity.homepage.component.PowerSupplyActivity
import com.superbgoal.caritasrig.activity.homepage.component.VideoCardActivity
import com.superbgoal.caritasrig.activity.homepage.home.HomeActivity
import com.superbgoal.caritasrig.data.getDatabaseReference
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.saveBuildTitle


class BuildActivity : ComponentActivity() {
    private val buildViewModel: BuildViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BuildScreen(buildViewModel)
        }

        // Tangani Intent saat aktivitas dibuat
        processIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Tangani Intent jika aktivitas kembali dari background
        intent?.let { processIntent(it) }
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
    val buildTitle by remember { mutableStateOf(buildViewModel.buildTitle) } // Get the build title
    var showDialog by remember { mutableStateOf(buildTitle.isEmpty()) }
    var dialogText by remember { mutableStateOf(buildTitle) }

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

            val components = listOf(
                "CPU" to CpuActivity::class.java,
                "Case" to CasingActivity::class.java,
                "GPU" to VideoCardActivity::class.java,
                "Motherboard" to MotherboardActivity::class.java,
                "RAM" to MemoryActivity::class.java,
                "Storage" to InternalHardDriveActivity::class.java,
                "Power Supply" to PowerSupplyActivity::class.java,
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
                components.forEach { (title, activityClass) ->
                    item {
                        val componentDetail = when (title) {
                            "CPU" -> buildViewModel.buildData.value?.components?.processor?.let {
                                "Processor: ${it.name}\nCores: ${it.core_count}\nSpeed: ${it.core_clock} GHz"
                            }
                            "GPU" -> buildViewModel.buildData.value?.components?.videoCard?.let {
                                "GPU: ${it.name}\nMemory: ${it.memory} GB"
                            }
                            "Motherboard" -> buildViewModel.buildData.value?.components?.motherboard?.let {
                                "Motherboard: ${it.name}\nChipset: ${it.formFactor}"
                            }
                            "RAM" -> buildViewModel.buildData.value?.components?.memory?.let {
                                "RAM: ${it.name}\nSize: ${it.pricePerGb} GB\nSpeed: ${it.speed} MHz"
                            }
                            "Storage" -> buildViewModel.buildData.value?.components?.internalHardDrive?.let {
                                "Storage: ${it.name}\nSize: ${it.capacity} GB"
                            }
                            "Power Supply" -> buildViewModel.buildData.value?.components?.powerSupply?.let {
                                "Power Supply: ${it.name}\nWattage: ${it.wattage} W"
                            }
                            "CPU Cooler" -> buildViewModel.buildData.value?.components?.cpuCooler?.let {
                                "CPU Cooler: ${it.name}\nFan Speed: ${it.rpm} RPM"
                            }
                            "Headphone" -> buildViewModel.buildData.value?.components?.headphone?.let {
                                "Headphone: ${it.name}\nType: ${it.type}"
                            }
                            "Keyboard" -> buildViewModel.buildData.value?.components?.keyboard?.let {
                                "Keyboard: ${it.name}\nType: ${it.switches}"
                            }
                            "Mouse" -> buildViewModel.buildData.value?.components?.mouse?.let {
                                "Mouse: ${it.name}\nType: ${it.maxDpi}"
                            }
                            "Case" -> buildViewModel.buildData.value?.components?.casing?.let {
                                "Case: ${it.name}\nForm Factor: ${it.type}"
                            }
                            else -> null
                        }

                        ComponentCard(
                            title = title,
                            onClick = {
                                val intent = Intent(context, activityClass)
                                context.startActivity(intent)
                            },
                            componentDetail = componentDetail,
                            category = title // Menambahkan nilai untuk parameter 'category'
                        )


                        Log.d("BuildScreen", "Component Detail for $title: $componentDetail")
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
    category: String, // Tambahkan kategori untuk membedakan komponen
    onClick: () -> Unit,
    componentDetail: String? // Ubah detail menjadi String yang sudah diolah
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

                    // Tampilkan detail komponen berdasarkan kategori
                    if (!componentDetail.isNullOrEmpty() && category == title) {
                        Text(
                            text = componentDetail,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Start
                        )
                    } else {
                        Button(
                            onClick = { onClick() },
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





