// Function.kt
package com.superbgoal.caritasrig.functions.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.build.BuildActivity
import com.superbgoal.caritasrig.data.getDatabaseReference
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.VideoCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Maintenance() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Under Maintenance :3",
                        textAlign = TextAlign.Center
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Add a simple content box to ensure something renders
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text= "Oops! We’re brewing up some cool updates. The site will be back online shortly. Thanks for hanging tight!",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ComponentCard(
    title: String,
    details: String,
    component: Parcelable, // Komponen yang akan dikirim melalui Intent
    imageUrl: String? = null, // URL untuk gambar (opsional)
    context: Context,
    isLoading: Boolean, // Status loading untuk tombol
    onAddClick: (onSuccess: () -> Unit) -> Unit, // Callback dengan aksi sukses
    backgroundColor: Color = Color(0xFF3E2C47), // Warna latar belakang kartu
    buttonColor: Color = Color(0xFF6E5768) // Warna tombol
) {
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
                    text = details,
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
                        Log.d("ComponentCard", "onAddClick callback triggered")
                        onAddClick {
                            // Aksi setelah sukses, pindahkan ke BuildActivity
                            Log.d("ComponentCard", "onSuccess triggered, navigating to BuildActivity")
                            val intent = Intent(context, BuildActivity::class.java).apply {
                                putExtra("component_title", title)
                                putExtra("component_data", component)
                            }
                            context.startActivity(intent)
                        }
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











