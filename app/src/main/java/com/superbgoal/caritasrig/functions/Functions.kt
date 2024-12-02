@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.superbgoal.caritasrig.functions

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.buildmanager.BuildComponents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun LoadingButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean = false,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    textColor: Color = Color.White,
    coroutineScope: CoroutineScope,
    onClick: suspend () -> Unit
) {
    var loadingState by remember { mutableStateOf(isLoading) }

    Box(modifier = modifier) {
        Button(
            onClick = {
                loadingState = true
                coroutineScope.launch {
                    onClick()
                    loadingState = false
                }
            },
            enabled = !loadingState,
            modifier = Modifier.fillMaxWidth(),
            colors = colors
        ) {
            Text(
                text = text,
                color = textColor
            )
        }

        if (loadingState) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun ComponentCard(
    title: String? = "",
    details: String? = null,
    price: Double? = null,
    context: Context? = null,
    component: Any? = null,
    imageUrl: String? = null,
    isLoading: Boolean,
    onAddClick: (() -> Unit)? = null, // Callback opsional untuk tombol Add
    onFavClick: (() -> Unit)? = null, // Callback opsional untuk tombol Favorite
    backgroundColor: Color = Color(0xFF3E2C47), // Warna latar belakang kartu
    buttonColor: Color = Color(0xFF6E5768), // Warna tombol
    navController: NavController? = null // Tidak digunakan, tetap dipertahankan
) {
    var isExpanded by remember { mutableStateOf(false) }

    val imageSize by animateDpAsState(targetValue = if (isExpanded) 150.dp else 100.dp)
    val titleOffset by animateDpAsState(targetValue = if (isExpanded) 16.dp else 0.dp)
    val detailsOffset by animateDpAsState(targetValue = if (isExpanded) 16.dp else 0.dp)

    Card(
        elevation = 4.dp,
        backgroundColor = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded } // Toggle ekspansi
            .animateContentSize(animationSpec = tween(300)) // Animasi perubahan ukuran
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (isExpanded) {
                // Layout saat mode detail
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gambar ditampilkan di tengah dengan animasi ukuran
                    if (!imageUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageSize) // Ukuran gambar dinamis
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Judul dengan animasi offset
                    Text(
                        text = title ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = titleOffset) // Animasi pergeseran judul
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Harga atau informasi tambahan
                    Text(
                        text = price?.let { "Price: $${it}" } ?: "Price: $100",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.offset(y = detailsOffset) // Animasi pergeseran detail
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    // Detail teks
                    Text(
                        text = details ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween, // Mengatur elemen di kiri dan kanan
                        verticalAlignment = Alignment.CenterVertically // Menjaga elemen tetap di tengah secara vertikal
                    ) {
                        if (!isLoading) {
                            Button(
                                onClick = {
                                    onAddClick?.invoke()
                                },
                                colors = ButtonDefaults.buttonColors(buttonColor),
                                modifier = Modifier.padding(start = 16.dp)
                            ) {
                                Text(text = "Add", color = Color.White)
                            }
                        } else {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = { onFavClick?.invoke() },
                            modifier = Modifier.align(Alignment.CenterVertically) // Menjaga di posisi vertikal
                        ) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = Color.White
                            )
                        }
                    }
                }
            } else {
                // Layout saat mode ringkas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Gambar di sebelah kiri dengan animasi ukuran
                    if (!imageUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(model = imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .size(imageSize) // Ukuran gambar dinamis
                                .clip(MaterialTheme.shapes.medium),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Kolom untuk judul dan detail singkat
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = title ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = price?.let { "Price: $${it}" } ?: "Price: $100",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }

                    // Tombol aksi
                    if (!isLoading) {
                        Button(
                            onClick = {
                                onAddClick?.invoke()
                            },
                            colors = ButtonDefaults.buttonColors(buttonColor),
                            modifier = Modifier.padding(start = 16.dp)
                        ) {
                            Text(text = "Add", color = Color.White)
                        }
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}


fun saveComponent(
    userId: String,
    buildTitle: String,
    componentType: String,
    componentData: Any,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit,
    onLoading: ((Boolean) -> Unit)? = null // Membuat parameter opsional dengan nilai default null
) {
    val database = getDatabaseReference()

    // Set loading state to true when the save process starts
    onLoading?.invoke(true)

    database.child("users").child(userId).child("builds").orderByChild("title").equalTo(buildTitle)
        .get()
        .addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val buildId = dataSnapshot.children.firstOrNull()?.key
                if (buildId != null) {
                    database.child("users").child(userId).child("builds").child(buildId)
                        .child("components").child(componentType)
                        .setValue(componentData)
                        .addOnSuccessListener {
                            onLoading?.invoke(false) // Set loading state to false on success
                            onSuccess() // Panggil callback sukses
                        }
                        .addOnFailureListener { error ->
                            onLoading?.invoke(false) // Set loading state to false on failure
                            onFailure("Failed to save component: ${error.message}")
                        }
                } else {
                    onLoading?.invoke(false) // Set loading state to false if buildId is not found
                    onFailure("Build ID not found.")
                }
            } else {
                onLoading?.invoke(false) // Set loading state to false if build is not found
                onFailure("Build with title \"$buildTitle\" not found.")
            }
        }
        .addOnFailureListener { error ->
            onLoading?.invoke(false) // Set loading state to false if there is a failure in the database query
            onFailure("Failed to find build: ${error.message}")
        }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> SwipeToDeleteContainer(
    item: T,
    onDelete: (T) -> Unit,
    onEdit: ((T) -> Unit)? = null, // Jadikan nullable
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember { mutableStateOf(false) }
    val state = rememberDismissState(
        confirmStateChange = { value ->
            when (value) {
                DismissValue.DismissedToStart -> {
                    isRemoved = true
                    true
                }
                DismissValue.DismissedToEnd -> {
                    onEdit?.let { edit ->
                        edit(item)
                        false
                    } ?: false
                }
                else -> false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved) {
        if (isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismiss(
            state = state,
            background = {
                EditOrDeleteBackground(
                    swipeDismissState = state,
                    enableEdit = onEdit != null // Tentukan apakah swipe untuk edit diaktifkan
                )
            },
            dismissContent = { content(item) },
            directions = if (onEdit != null) {
                setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart)
            } else {
                setOf(DismissDirection.EndToStart)
            } // Matikan StartToEnd jika onEdit null
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditOrDeleteBackground(
    swipeDismissState: DismissState,
    enableEdit: Boolean // Parameter baru untuk menentukan apakah edit diaktifkan
) {
    val color = when (swipeDismissState.dismissDirection) {
        DismissDirection.StartToEnd -> if (enableEdit) Color.Blue else Color.Transparent
        DismissDirection.EndToStart -> Color.Red
        else -> Color.Transparent
    }

    val icon = when (swipeDismissState.dismissDirection) {
        DismissDirection.StartToEnd -> if (enableEdit) Icons.Default.Edit else null
        DismissDirection.EndToStart -> Icons.Default.Delete
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = if (swipeDismissState.dismissDirection == DismissDirection.StartToEnd) {
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
        }
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditOrDeleteBackground(
    swipeDismissState: DismissState
) {
    val color = when (swipeDismissState.dismissDirection) {
        DismissDirection.StartToEnd -> Color.Blue // Untuk edit
        DismissDirection.EndToStart -> Color.Red // Untuk hapus
        else -> Color.Transparent
    }

    val icon = when (swipeDismissState.dismissDirection) {
        DismissDirection.StartToEnd -> Icons.Default.Edit // Ikon edit
        DismissDirection.EndToStart -> Icons.Default.Delete // Ikon hapus
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = if (swipeDismissState.dismissDirection == DismissDirection.StartToEnd) {
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
        }
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

fun calculateTotalPrice(it: BuildComponents): Double {
    val totalPrice = listOfNotNull(
        it.processor?.price,
        it.casing?.price,
        it.videoCard?.price,
        it.motherboard?.price,
        it.memory?.totalPrice,
        it.internalHardDrive?.price,
        it.powerSupply?.price,
        it.cpuCooler?.price,
        it.headphone?.price,
        it.keyboard?.price,
        it.mouse?.price
    ).sumOf { price -> price ?: 0.0 }

    return ceil(totalPrice) // Membulatkan ke atas
}

@Composable
fun BuildCompatibilityAccordion(
    buildComponents: BuildComponents,
    estimatedWattage: Double
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Menghitung status kompatibilitas
    val compatibilityStatus = calculateCompatibilityStatus(buildComponents, estimatedWattage)

    // Accordion Header
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { isExpanded = !isExpanded },
        elevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Kompatibilitas Build",
                style = MaterialTheme.typography.bodyLarge
            )
            if (compatibilityStatus != null) {
                Text(
                    text = "${compatibilityStatus.compatibleCount}/${compatibilityStatus.totalCount} kompatibel",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    // Accordion Content
    if (isExpanded) {
        Column(modifier = Modifier.padding(8.dp)) {
            if (compatibilityStatus != null) {
                compatibilityStatus.details.forEach { detail ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = detail.componentName)
                        Text(
                            text = if (detail.isCompatible) "[compatible]" else "[tidak compatible]",
                            color = if (detail.isCompatible) Color.Green else Color.Red
                        )
                    }
                }
            }

            // Rekomendasi
            if (compatibilityStatus != null) {
                if (compatibilityStatus.recommendation.isNotEmpty()) {
                    Text(
                        text = "Rekomendasi:",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = compatibilityStatus.recommendation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

data class CompatibilityDetail(
    val componentName: String,
    val isCompatible: Boolean
)

data class CompatibilityStatus(
    val compatibleCount: Int,
    val totalCount: Int,
    val details: List<CompatibilityDetail>,
    val recommendation: String
)

fun calculateCompatibilityStatus(
    buildComponents: BuildComponents,
    estimatedWattage: Double
): CompatibilityStatus? {
    val details = mutableListOf<CompatibilityDetail>()
    var recommendation = ""

    // PSU Compatibility
    val powerSupplyName = buildComponents.powerSupply?.name.orEmpty()
    val powerSupplyWattage = parseWattage(buildComponents.powerSupply?.wattage)
    val psuCompatible = buildComponents.powerSupply == null || powerSupplyWattage >= estimatedWattage
    if (buildComponents.powerSupply != null) {
        details.add(CompatibilityDetail(powerSupplyName.ifEmpty { "Powersupply" }, psuCompatible))
        if (!psuCompatible) {
            recommendation += "Ganti PSU dengan wattage lebih tinggi. "
        }
    }

    // Processor and Motherboard Socket Compatibility
    val processorName = buildComponents.processor?.name.orEmpty()
    val motherboardName = buildComponents.motherboard?.name.orEmpty()
    val processorSocket = buildComponents.processor?.socket
    val motherboardSocket = buildComponents.motherboard?.socketCpu
    val socketCompatible = buildComponents.processor == null || buildComponents.motherboard == null || processorSocket == motherboardSocket
    if (buildComponents.processor != null) {
        details.add(CompatibilityDetail(processorName, socketCompatible))
    }
    if (buildComponents.motherboard != null) {
        details.add(CompatibilityDetail(motherboardName, socketCompatible))
    }
    if (!socketCompatible && buildComponents.processor != null && buildComponents.motherboard != null) {
        recommendation += "Ganti motherboard atau processor agar socket cocok. "
    }

    // Memory Compatibility
    val ramName = buildComponents.memory?.name.orEmpty()
    val ramType = buildComponents.memory?.let { extractRamType(it.speed) }
    val motherboardRamType = buildComponents.motherboard?.memoryType
    val ramCompatible = buildComponents.memory == null || buildComponents.motherboard == null || ramType == motherboardRamType
    if (buildComponents.memory != null) {
        details.add(CompatibilityDetail(ramName, ramCompatible))
        if (!ramCompatible) {
            recommendation += "Ganti RAM agar cocok dengan motherboard. "
        }
    }

    // Motherboard and Case Compatibility (Form Factor)
    val casingName = buildComponents.casing?.name.orEmpty()
    val motherboardFormFactor = buildComponents.motherboard?.formFactor
    val casingSupportedSizes = buildComponents.casing?.motherboardFormFactor.orEmpty()
    val casingCompatible = buildComponents.casing == null || buildComponents.motherboard == null || motherboardFormFactor.toString() in casingSupportedSizes
    if (buildComponents.casing != null) {
        details.add(CompatibilityDetail(casingName, casingCompatible))
        if (!casingCompatible) {
            recommendation += "Ganti casing agar mendukung form factor motherboard (${motherboardFormFactor}). "
        }
    }

    // Jika tidak ada komponen yang dipilih, kembalikan null
    if (details.isEmpty()) {
        return null
    }

    // Count Total and Compatible
    val compatibleCount = details.count { it.isCompatible }
    val totalCount = details.size

    return CompatibilityStatus(
        compatibleCount = compatibleCount,
        totalCount = totalCount,
        details = details,
        recommendation = recommendation.trim()
    )
}






@Composable
fun <T> GenericCard(
    item: T,
    modifier: Modifier = Modifier,
    onClick: () -> Unit, // Aksi saat card diklik
    onFavoriteClick: () -> Unit, // Aksi saat tombol favorite diklik
    content: @Composable ColumnScope.(T) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween, // Mengatur isi ke kiri dan kanan
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f) // Kolom isi mengambil ruang sisa
            ) {
                content(item)
            }
            IconButton(
                onClick = onFavoriteClick // Aksi tombol favorite
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SearchBarForComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onFilterClick: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { androidx.compose.material.Text("Search CPU") },
        placeholder = { androidx.compose.material.Text("Search by name") },
        singleLine = true,
        modifier = modifier,
        leadingIcon = {
            androidx.compose.material.Icon(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = "Search"
            )
        },
        trailingIcon = {
            androidx.compose.material.IconButton(onClick = onFilterClick) {
                androidx.compose.material.Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter"
                )
            }
        }
    )
}

fun calculateTotalWattage(it: BuildComponents): Double {
    // Menghitung total TDP dari prosesor dan kartu grafis
    val estimatedWattage = listOfNotNull(
        parseWattage(it.processor?.tdp),
        it.videoCard?.tdp // Pastikan untuk memproses TDP kartu grafis
    ).sum()

    // Menambahkan estimasi wattage komponen lain jika ada
    var additionalWattage = 0.0

    // Menambahkan 50 watt jika motherboard ada
    if (it.motherboard != null) {
        additionalWattage += 50.0
    }

    // Menambahkan 8 watt jika RAM ada (diasumsikan 2 modul)
    if (it.memory != null) {
        additionalWattage += 8.0
    }

    // Menambahkan 3 watt jika SSD ada
    if (it.internalHardDrive != null) {
        additionalWattage += 3.0
    }

    // Menambahkan 12 watt jika cooling system ada (misalnya 2 kipas)
    if (it.cpuCooler != null) {
        additionalWattage += 12.0
    }

    // Menambahkan 5 watt untuk periferal jika ada (keyboard, mouse, dll.)
    if (it.keyboard != null) {
        additionalWattage += 1.0
    }

    // Menambahkan 3 watt untuk headphone jika ada
    if (it.headphone != null) {
        additionalWattage += 1.0
    }

    if (it.mouse != null) {
        additionalWattage += 1.0
    }

    // Mengembalikan total wattage
    return estimatedWattage + additionalWattage
}


fun parseWattage(wattString: String?): Double {
    // Menghapus satuan "W" dan mengonversi string menjadi Double
    return wattString?.replace(" W", "")?.toDoubleOrNull() ?: 0.0
}
fun calculatePSU(estimatedWattage: Double): Double {
    // Menghitung kapasitas PSU dengan margin 30%
    return estimatedWattage * 1.3
}

fun extractRamType(speed: String): String {
    return speed.split("-")[0] // Mengambil bagian sebelum tanda "-"
}














