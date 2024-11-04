package com.superbgoal.caritasrig.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbgoal.caritasrig.R

class AboutUsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                AboutUsContent(onBackPressed = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsContent(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "About Us") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileCard(
                    imageRes = R.drawable.profile1,
                    name = "Hotbaen Eliezer",
                    instagram = "@exaudi._",
                    github = "exaudi26",
                    email = "eliezerstmrg@gmail.com"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.profile2,
                    name = "Ferry Sirait",
                    instagram = "@ferry_srt",
                    github = "ferrysrt",
                    email = "ferrypb123pb123@gmail.com"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.profile3,
                    name = "Richard Hasibuan",
                    instagram = "@ricathsb",
                    github = "ricathsb",
                    email = "ricat1907111@gmail.com"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.profile4,
                    name = "Samuel Sitanggang",
                    instagram = "@samuel_bryan_ps",
                    github = "SamuelSitanggang125",
                    email = "samuelsitanggang04@gmail.com"
                )
            }
        }
    }
}

@Composable
fun ProfileCard(
    imageRes: Int,
    name: String,
    instagram: String,
    github: String,
    email: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize()
        ) {
            // Gambar di sebelah kiri
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = "$name Profile Image",
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )
            // Informasi di sebelah kanan
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = name, style = MaterialTheme.typography.bodyLarge, fontSize = 16.sp)
                Text(text = "Instagram: $instagram", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(text = "GitHub: $github", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Text(text = "Email: $email", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}
