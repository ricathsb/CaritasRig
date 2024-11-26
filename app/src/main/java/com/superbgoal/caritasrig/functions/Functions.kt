@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.superbgoal.caritasrig.functions

import android.content.Context
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
    title: String,
    details: String? = null,
    // Komponen yang akan dikirim melalui Intent
    context : Context? = null,
    component : Any?= null,
    imageUrl: String? = null, // URL untuk gambar (opsional)
    isLoading: Boolean, // Status loading untuk tombol
    onAddClick: () -> Unit, // Callback dengan aksi sukses
    backgroundColor: Color = Color(0xFF3E2C47), // Warna latar belakang kartu
    buttonColor: Color = Color(0xFF6E5768), // Warna tombol
    navController: NavController? = null
) {
    Log.d("ComponentCard", "NavController: $navController")

    Card(
        elevation = 4.dp,
        backgroundColor = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tampilkan gambar jika URL diberikan
            if (!imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .padding(end = 16.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            }

            // Kolom untuk judul dan detail
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = details?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            // Tampilkan indikator loading atau tombol Add
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Button(
                    onClick = {
                        Log.d("ComponentCard", "onAddClick triggered for component: $title")
                        onAddClick() // Panggil callback ketika tombol ditekan
                    },
                    enabled = !isLoading, // Nonaktifkan tombol saat loading
                    colors = ButtonDefaults.buttonColors(buttonColor)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_btn),
                        contentDescription = "Add Icon",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add", color = Color.White)
                }
            }
        }
    }
}





fun putExtra(s: String, title: String) {

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











