package com.superbgoal.caritasrig.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.build.*

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
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_build),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {}, // Remove title text
                    modifier = Modifier.height(145.dp),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                val intent = Intent(context, HomeActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .padding(start = 30.dp, top = 60.dp) // Padding untuk ikon Home
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_home),
                                contentDescription = "Home",
                                modifier = Modifier.size(80.dp), // Ukuran ikon Home
                                tint = Color.White // Warna ikon
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                // Tambahkan fungsi simpan di sini
                            },
                            modifier = Modifier
                                .padding(end = 30.dp, top = 60.dp) // Padding untuk ikon Save
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = "Save",
                                modifier = Modifier.size(80.dp), // Ukuran ikon Save
                                tint = Color.White // Warna ikon
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Transparent background
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            containerColor = Color.Transparent // Make Scaffold background transparent
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp,0.dp,16.dp,16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ComponentCard(title = "CPU", activity = CpuActivity::class.java) }
                item { ComponentCard(title = "Case", activity = CasingActivity::class.java) }
                item { ComponentCard(title = "GPU", activity = VideoCardActivity::class.java) }
                item { ComponentCard(title = "Motherboard", activity = MotherboardActivity::class.java) }
                item { ComponentCard(title = "RAM", activity = MemoryActivity::class.java) }
                item { ComponentCard(title = "Storage", activity = InternalHardDriveActivity::class.java) }
                item { ComponentCard(title = "Power Supply", activity = PowerSupplyActivity::class.java) }
            }
        }
    }
}

@Composable
fun ComponentCard(title: String, activity: Class<out ComponentActivity>) {
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
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    val intent = Intent(context, activity)
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Add Component")
            }
        }
    }
}
