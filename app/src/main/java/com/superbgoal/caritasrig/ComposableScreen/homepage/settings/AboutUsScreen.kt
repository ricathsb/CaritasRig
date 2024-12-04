package com.superbgoal.caritasrig.ComposableScreen.homepage.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.superbgoal.caritasrig.R

@Composable
fun ProfileCard(
    imageRes: Int,
    name: String,
    instagram: String,
    github: String
) {
    val context = LocalContext.current

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

                val annotatedText = buildAnnotatedString {
                    append("Instagram: ")

                    // Tambahkan teks dengan warna berbeda
                    withStyle(style = SpanStyle(color = Color.Magenta)) {
                        append(instagram)
                    }
                    append("\nGitHub: ")
                    // Tambahkan teks dengan warna berbeda
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append(github)
                    }
                }

                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable {
                        val instagramIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.instagram.com/${instagram.removePrefix("@")}")
                        )
                        context.startActivity(instagramIntent)
                        val githubIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/$github")
                        )
                        context.startActivity(githubIntent)
                    }
                )
            }
        }
    }
}

@Composable
fun AboutUsScreen() {
    Image(
        painter = painterResource(id = R.drawable.component_bg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Judul
        Text(
            text = "About Us",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Daftar kartu
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                ProfileCard(
                    imageRes = R.drawable.profile2,
                    name = "Ferry Sirait \n(Project Manager)",
                    instagram = "@ferry_srt",
                    github = "ferrysrt"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.hotbaennotsigma,
                    name = "Hotbaen Eliezer\n(Back-end)",
                    instagram = "@exaudi._",
                    github = "exaudi26"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.profile3,
                    name = "Richard Hasibuan \n(Back-end)",
                    instagram = "@ricathsb",
                    github = "ricathsb"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.profile4,
                    name = "Samuel Sitanggang \n(Back-end)",
                    instagram = "@samuel_bryan_ps",
                    github = "SamuelSitanggang125"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.profile5,
                    name = "Brian Silitonga \n(Front-end)",
                    instagram = "@gigachad_bryan",
                    github = "BrianTzr"
                )
            }
            item {
                ProfileCard(
                    imageRes = R.drawable.profile6,
                    name = "Rakha Aditya \n(Front-end)",
                    instagram = "@_rkhadt",
                    github = "rakhaad"
                )
            }
        }
    }
}


