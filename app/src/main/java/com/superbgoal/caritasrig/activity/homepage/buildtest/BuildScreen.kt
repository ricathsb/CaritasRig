package com.superbgoal.caritasrig.activity.homepage.buildtest

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.saveBuildTitle

@SuppressLint("SuspiciousIndentation")
@Composable
fun BuildScreen(
    buildViewModel: BuildViewModel = viewModel(),
    @SuppressLint("SuspiciousIndentation") navController: NavController? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val isNewBuild by buildViewModel.isNewBuild.collectAsState()
        if (isNewBuild) {
            buildViewModel.resetBuildTitle()
            showDialog = true
            buildViewModel.setNewBuildState(false)
        }




    val context = LocalContext.current
    val buildData by buildViewModel.buildData.observeAsState()
    val buildTitle by buildViewModel.buildTitle.observeAsState("")
    val selectedComponents by buildViewModel.selectedComponents.observeAsState(emptyMap())
    var dialogText by remember { mutableStateOf(buildTitle) }
    val loading by buildViewModel.loading.observeAsState(false)
    val sharedPreferences = context.getSharedPreferences("ScrollPrefs", Context.MODE_PRIVATE)

    // LazyListState untuk melacak posisi scroll
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = sharedPreferences.getInt("lastScrollIndex", 0),
        initialFirstVisibleItemScrollOffset = sharedPreferences.getInt("lastScrollOffset", 0)
    )

    LaunchedEffect (Unit) {
        Log.d("BuildScreen", "Fetching build by title: $buildTitle")
        buildViewModel.fetchBuildByTitle(buildTitle)
    }



    // Menyimpan posisi scroll di SharedPreferences
    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex to lazyListState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                sharedPreferences.edit().apply {
                    putInt("lastScrollIndex", index)
                    putInt("lastScrollOffset", offset)
                    apply()
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.bg_build),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Content area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Title
            Text(
                text = buildTitle.ifEmpty { "" },
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (loading) {
                // Full-screen loading indicator
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(60.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    // Activity mapping for navigation
                    val routeMap = mapOf(
                        "CPU" to "cpu_screen",
                        "Case" to "casing_screen",
                        "GPU" to "gpu_screen",
                        "Motherboard" to "motherboard_screen",
                        "RAM" to "memory_screen",
                        "InternalHardDrive" to "internal_hard_drive_screen",
                        "PowerSupply" to "power_supply_screen",
                        "CPU Cooler" to "cpu_cooler_screen",
                        "Headphone" to "headphone_screen",
                        "Keyboard" to "keyboard_screen",
                        "Mouse" to "mouse_screen"
                    )

                    selectedComponents.forEach { (title) ->
                        item {
                            val componentDetail = when (title) {
                                "CPU" -> buildData?.components?.processor?.let {
                                    "Processor: ${it.name}\nCores: ${it.core_count}\nSpeed: ${it.core_clock} GHz"
                                }

                                "Case" -> buildData?.components?.casing?.let {
                                    "Case: ${it.name}\nType: ${it.type}"
                                }

                                "GPU" -> buildData?.components?.videoCard?.let {
                                    "GPU: ${it.name}\nMemory: ${it.memory} GB"
                                }

                                "Motherboard" -> buildData?.components?.motherboard?.let {
                                    "Motherboard: ${it.name}\nChipset: ${it.formFactor}"
                                }

                                "RAM" -> buildData?.components?.memory?.let {
                                    "Memory: ${it.name}\nSize: ${it.modules} GB\nSpeed: ${it.speed} MHz"
                                }

                                "InternalHardDrive" -> buildData?.components?.internalHardDrive?.let {
                                    "Internal Hard Drive: ${it.name}\nCapacity: ${it.capacity} GB"
                                }

                                "PowerSupply" -> buildData?.components?.powerSupply?.let {
                                    "Power Supply: ${it.name}\nWattage: ${it.wattage} W"
                                }

                                "CPU Cooler" -> buildData?.components?.cpuCooler?.let {
                                    "CPU Cooler: ${it.name}\nFan Speed: ${it.rpm} RPM"
                                }

                                "Headphone" -> buildData?.components?.headphone?.let {
                                    "Headphone: ${it.name}\nType: ${it.type}"
                                }

                                "Keyboard" -> buildData?.components?.keyboard?.let {
                                    "Keyboard: ${it.name}\nType: ${it.switches}"
                                }

                                "Mouse" -> buildData?.components?.mouse?.let {
                                    "Mouse: ${it.name}\nDPI: ${it.maxDpi}"
                                }

                                else -> null
                            }

                            ComponentCard(
                                title = title,
                                componentDetail = componentDetail,
                                currentPrice = buildData?.components?.let {
                                    when (title) {
                                        "CPU" -> it.processor?.price?.toString()
                                        "Case" -> it.casing?.price?.toString()
                                        "GPU" -> it.videoCard?.price?.toString()
                                        "Motherboard" -> it.motherboard?.price?.toString()
                                        "RAM" -> it.memory?.price?.toString()
                                        "InternalHardDrive" -> it.internalHardDrive?.price?.toString()
                                        "PowerSupply" -> it.powerSupply?.price?.toString()
                                        "CPU Cooler" -> it.cpuCooler?.price?.toString()
                                        "Headphone" -> it.headphone?.price?.toString()
                                        "Keyboard" -> it.keyboard?.price?.toString()
                                        "Mouse" -> it.mouse?.price?.toString()
                                        else -> "0.0"
                                    }
                                } ?: "0.0",
                                onClick = {
                                    val route = routeMap[title]
                                    if (route != null) {
                                        navController?.navigate(route)
                                    } else {
                                        Log.e("NavigationError", "No route found for title: $title")
                                    }
                                },
                                onRemove = {
                                    buildViewModel.removeComponent(title)
                                },
                                onUpdatePrice = { newPrice ->
                                    buildViewModel.updateBuildComponentWithViewModel(
                                        category = title,
                                        updatedData = mapOf("price" to newPrice.toDouble())
                                    )
                                    Log.d("BuildActivity", "Price updated for $title: $newPrice")
                                },
                                loading = loading
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button for reset
        FloatingActionButton(
            onClick = {
                showDialog = true
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Reset",
                tint = Color.White
            )
        }
    }

    // Dialog tetap ditampilkan jika diperlukan
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Do nothing to prevent dismissing the dialog */ },
            title = { Text(text = "Enter Build Title") },
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
                                context = context,
                                buildTitle = dialogText,
                                onSuccess = {
                                    showDialog = false
                                    buildViewModel.saveBuildTitle(dialogText)
                                    buildViewModel.resetBuildData()
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
                        if (buildViewModel.buildTitle.value?.isNotEmpty() == true) {
                            showDialog = false
                        } else {
                            showDialog = false
                            navController?.navigateUp()
                        }
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
    componentDetail: String?,
    currentPrice: String,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onUpdatePrice: (String) -> Unit,
    loading : Boolean = false
) {
    // State untuk menampilkan dialog
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

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

                    if (!componentDetail.isNullOrEmpty()) {
                        Text(
                            text = componentDetail,
                            color = Color.White,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Harga Komponen
                        Text(
                            text = "Current Price: $$currentPrice",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tombol Remove
                        Button(
                            onClick = onRemove,
                            modifier = Modifier.background(Color.Transparent),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(text = "Remove Component")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tombol Configure
                        Button(
                            onClick = {
                                showDialog = true // Tampilkan dialog
                            },
                            modifier = Modifier.background(Color.Transparent),
                            elevation = ButtonDefaults.buttonElevation(0.dp)
                        ) {
                            Text(text = "Configure Component")
                        }
                    } else {
                        Text(
                            text = "No $title Selected",
                            color = Color.Gray,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Start
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Tombol Add
                        Button(
                            onClick = onClick,
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

    // Dialog untuk mengedit harga
    if (showDialog) {
        PriceEditDialog(
            category = title,
            currentPrice = currentPrice,
            onDismiss = { showDialog = false },
            onConfirm = { newPrice ->
                showDialog = false
                onUpdatePrice(newPrice)
                Log.d("BuildActivity", "Price updated for $loading")
                if (!loading) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        (context as? Activity)?.recreate()
                    }, 20)
                }
            }
        )
    }
}

@Composable
fun PriceEditDialog(
    category: String,
    currentPrice: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var newPrice by remember { mutableStateOf(currentPrice) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Edit Price for $category") },
        text = {
            Column {
                Text(text = "Current Price: $currentPrice")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newPrice,
                    onValueChange = { newPrice = it },
                    label = { Text("New Price") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(newPrice) }) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}