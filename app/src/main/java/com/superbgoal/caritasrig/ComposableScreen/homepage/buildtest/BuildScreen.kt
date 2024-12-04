package com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.BuildCompatibilityAccordion
import com.superbgoal.caritasrig.functions.calculatePSU
import com.superbgoal.caritasrig.functions.calculateTotalPrice
import com.superbgoal.caritasrig.functions.calculateTotalWattage
import com.superbgoal.caritasrig.functions.editRamQuantity
import com.superbgoal.caritasrig.functions.parseImageUrl
import com.superbgoal.caritasrig.functions.saveBuildTitle

@SuppressLint("SuspiciousIndentation")
@Composable
fun BuildScreen(
    buildViewModel: BuildViewModel = viewModel(),
    @SuppressLint("SuspiciousIndentation")
    navController: NavController? = null
) {
    val showDialog by buildViewModel.showNewDialog.collectAsState()
    val isNewBuild by buildViewModel.isNewBuild.collectAsState()
    Log.d("BuildScreenn", "isNewBuild: $isNewBuild")
    if (isNewBuild) {
        buildViewModel.resetBuildTitle()
        buildViewModel.setNewDialogState(true)
        buildViewModel.setNewBuildState(false)
    }
    val context = LocalContext.current
    val imagePickerDialog by buildViewModel.showShareDialog.collectAsState(false)
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val buildData by buildViewModel.buildData.observeAsState()
    val buildTitle by buildViewModel.buildTitle.observeAsState("")
    val selectedComponents by buildViewModel.selectedComponents.observeAsState(emptyMap())
    var dialogText by remember { mutableStateOf(buildTitle) }
    val loading by buildViewModel.loading.observeAsState(false)
    val sharedPreferences = context.getSharedPreferences("ScrollPrefs", Context.MODE_PRIVATE)
    val totalBuildPrice by buildViewModel.totalBuildPrice.observeAsState(0.0)
    val sancreekFont = FontFamily(Font(R.font.sancreek))
    val sairastencilone = FontFamily(Font(R.font.sairastencilone))
    val totalWattage by buildViewModel.totalWattage.observeAsState(0.0)
    val estimatedWattage = calculatePSU(totalWattage)
    buildData?.components?.let { calculateTotalPrice(it) }?.let { buildViewModel.setBuildPrice(it) }
    buildData?.components?.let { calculateTotalWattage(it) }?.let { buildViewModel.setBuildWattage(it) }


    Log.d("BuildScreen", "Build Data: $totalWattage")
    Log.d("BuildScreen", "Estimated Wattage: $estimatedWattage")

    // LazyListState untuk melacak posisi scroll
    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemIndex = sharedPreferences.getInt("lastScrollIndex", 0),
        initialFirstVisibleItemScrollOffset = sharedPreferences.getInt("lastScrollOffset", 0)
    )

    LaunchedEffect (Unit) {
        Log.d("BuildScreen", "Fetching build by title: $buildTitle")
        buildViewModel.fetchBuildByTitle(buildTitle)
    }

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

    Box(modifier = Modifier
        .fillMaxSize()
        .background(color = Color.Black.copy(alpha = 0.2f)),
    )
    {
        Image(
            painter = painterResource(id = R.drawable.component_bg),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // Background image
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f)) // Atur transparansi sesuai kebutuhan
        ){

            // Content area
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                buildData?.components?.let { BuildCompatibilityAccordion(buildComponents = it, estimatedWattage = estimatedWattage) }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.0f),
                ) {
                    Row(
                        modifier = Modifier.padding(1.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row {
                            Text(
                                text = "Total: ",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "$${totalBuildPrice}",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Green
                                )
                            )
                        }
                        Row {
                            Icon(
                                imageVector = Icons.Default.ElectricBolt,
                                contentDescription = "Estimated Wattage",
                                tint = Color.White
                            )
                            Text(
                                text = "$totalWattage W",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Cyan
                                )
                            )
                        }

                    }
                }
                if (imagePickerDialog) {
                    ImagePickerDialog(
                        onDismiss = { buildViewModel.setShareDialogState(false) }, // Menutup dialog jika dibatalkan
                        onImagesSelected = { images ->
                            selectedImages = images // Menyimpan gambar yang dipilih
                            Log.d("BuildScreen", "Selected Images: $images")
                            buildViewModel.shareBuildWithOthers(selectedImages)
                            buildViewModel.setShareDialogState(false)
                        }
                    )
                }

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
                                        "${it.name}"
                                    }

                                    "Case" -> buildData?.components?.casing?.let {
                                        "${it.name}"
                                    }

                                    "GPU" -> buildData?.components?.videoCard?.let {
                                        "${it.name}"
                                    }

                                    "Motherboard" -> buildData?.components?.motherboard?.let {
                                        "${it.name}"
                                    }

                                    "RAM" -> buildData?.components?.memory?.let {
                                        it.name
                                    }

                                    "InternalHardDrive" -> buildData?.components?.internalHardDrive?.let {
                                        it.name
                                    }

                                    "PowerSupply" -> buildData?.components?.powerSupply?.let {
                                        it.name
                                    }

                                    "CPU Cooler" -> buildData?.components?.cpuCooler?.let {
                                        it.name
                                    }

                                    "Headphone" -> buildData?.components?.headphone?.let {
                                        it.name
                                    }

                                    "Keyboard" -> buildData?.components?.keyboard?.let {
                                        it.name
                                    }

                                    "Mouse" -> buildData?.components?.mouse?.let {
                                        it.name
                                    }

                                    else -> null
                                }
                                ComponentCard(
                                    initialQuantity = buildData?.components?.memory?.quantity,
                                    title = title,
                                    componentDetail = componentDetail,
                                    totalPrice = buildData?.components?.memory?.totalPrice.toString(),
                                    imageComponent = buildData?.components?.let {
                                        when (title) {
                                            "CPU" -> it.processor?.imageUrl
                                            "Case" -> it.casing?.imageUrl
                                            "GPU" -> it.videoCard?.imageUrl
                                            "Motherboard" -> it.motherboard?.imageUrl
                                            "RAM" -> it.memory?.imageUrl
                                            "InternalHardDrive" -> it.internalHardDrive?.imageUrl
                                            "PowerSupply" -> it.powerSupply?.imageUrl
                                            "CPU Cooler" -> it.cpuCooler?.imageUrl
                                            else -> ""
                                        }
                                    } ?: "0.0",
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

                                        // Tambahkan delay sebelum memanggil fetchBuildByTitle
                                        Handler(Looper.getMainLooper()).postDelayed({
                                            buildViewModel.fetchBuildByTitle(buildTitle)
                                        }, 20) // Delay 2000ms (2 detik)
                                    }
                                    ,
                                    loading = loading,
                                    onQuantityChange = { newQuantity ->
                                        Log.d("BuildActivity", "Quantity: $newQuantity")

                                        editRamQuantity(
                                            buildTitle = buildTitle,
                                            quantity = newQuantity,
                                            onSuccess = {
                                                buildViewModel.fetchBuildByTitle(buildTitle)
                                                println("RAM quantity and total price updated successfully!")
                                            },
                                            onFailure = { errorMessage ->
                                                println("Error: $errorMessage")
                                            },
                                            onLoading = { isLoading ->
                                                if (isLoading) {
                                                    println("Updating RAM quantity and total price...")
                                                } else {
                                                    println("Update process completed.")
                                                }
                                            }
                                        )
                                    }
                                )
                                Log.d("tadd", "ComponentCard rendered for title: ${buildData?.components?.processor?.price}")
                                Log.e("BuildActivity", "Unknown component type: ${buildData?.components?.processor?.imageUrl}")

                            }
                        }
                    }
                }
            }
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
                                    buildViewModel.setNewDialogState(false)
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
                            buildViewModel.setNewDialogState(false)
                        } else {
                            buildViewModel.setNewDialogState(false)
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
    totalPrice: String? = "nooo",
    componentDetail: String?,
    currentPrice: String,
    initialQuantity: Int?,
    onClick: () -> Unit,
    onRemove: () -> Unit,
    onUpdatePrice: (String) -> Unit,
    loading: Boolean = false,
    onQuantityChange: ((Int) -> Unit)? = null,
    imageComponent: String
) {
    // Inisialisasi quantity menggunakan initialQuantity
    var quantity by rememberSaveable { mutableStateOf(initialQuantity) }
    var showDialog by remember { mutableStateOf(false) }
    val displayText = if (title == "RAM") {
        "Total Price: $$totalPrice"
    } else {
        "Price: $$currentPrice"
    }

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
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(id = R.color.brown)),
                contentAlignment = Alignment.Center
            ) {
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!componentDetail.isNullOrEmpty()) {
                        // Gambar
                        AsyncImage(
                            model = parseImageUrl(imageComponent),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(0.5f)
                                .size(75.dp)
                                .clip(shape = RoundedCornerShape(8.dp))
                        )

                        // Kolom Teks
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = componentDetail,
                                color = Color.White,
                                maxLines = 1, // Batasi hanya satu baris
                                overflow = TextOverflow.Ellipsis, // Tambahkan titik-titik
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Text(
                                text = displayText,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 10.sp
                            )

                            // Kontrol Plus-Minus untuk RAM
                            if (title.equals("RAM", ignoreCase = true)) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Tombol Minus
                                    IconButton(
                                        onClick = {
                                            if (quantity!! > 1) {
                                                quantity = quantity!! - 1
                                                onQuantityChange?.invoke(quantity!!)
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = "Minus",
                                            tint = Color.White
                                        )
                                    }

                                    // Jumlah
                                    Text(
                                        text = quantity.toString(),
                                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 16.dp)
                                    )

                                    // Tombol Plus
                                    IconButton(
                                        onClick = {
                                            quantity = quantity!! + 1
                                            onQuantityChange?.invoke(quantity!!)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Plus",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        // Kolom Tombol
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 1.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Tombol Configure
                            Button(
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                onClick = { showDialog = true },
                                modifier = Modifier.fillMaxWidth().padding(1.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Configure Icon",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Configure",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        maxLines = 1, // Batasi teks hanya satu baris
                                    )
                                }
                            }
                            Button(
                                onClick = onRemove,
                                modifier = Modifier.fillMaxWidth().padding(1.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove Icon",
                                        tint = Color.Black,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Remove",
                                        color = Color.Black,
                                        fontSize = 12.sp,
                                        maxLines = 1, // Batasi teks hanya satu baris
                                    )
                                }
                            }
                        }
                    } else {
                        // Ketika componentDetail kosong, tampilkan tombol Add Component
                        Column(
                            modifier = Modifier
                                .padding(start = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No $title Selected",
                                color = Color.Gray,
                                modifier = Modifier.padding(8.dp),
                                textAlign = TextAlign.Start
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = onClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp),
                                elevation = ButtonDefaults.buttonElevation(0.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.add_btn),
                                    contentDescription = "Add Icon",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Add Component",
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                )
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
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
                Log.d("BuildActivity", "Price updated for $title: $newPrice")
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
        title = {
            Text(
                text = "Edit Price for $category",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Current Price: $currentPrice",
                    style = MaterialTheme.typography.bodyMedium
                )
                TextField(
                    value = newPrice,
                    onValueChange = {
                        // Hanya izinkan input angka dan titik desimal
                        newPrice = it.filter { char -> char.isDigit() || char == '.' }
                    },
                    label = { Text("New Price") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // Validasi harga sebelum konfirmasi
                    if (newPrice.isNotBlank() && newPrice.toDoubleOrNull() != null) {
                        onConfirm(newPrice)
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onImagesSelected: (List<Uri>) -> Unit
) {
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Image picker launcher untuk memilih beberapa gambar
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            selectedImages = uris
        }
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Select Images",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose Images")
                }

                // Tampilan preview gambar yang dipilih
                if (selectedImages.isNotEmpty()) {
                    Text(
                        text = "Selected Images:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(selectedImages) { imageUri ->
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Selected image",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                } else {
                    Text(
                        text = "No images selected",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onImagesSelected(selectedImages)
                    onDismiss()
                },
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}