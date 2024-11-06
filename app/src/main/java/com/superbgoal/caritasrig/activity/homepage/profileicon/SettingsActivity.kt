package com.superbgoal.caritasrig.activity.homepage.profileicon

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaritasRigTheme {
                SettingsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SettingOption("Change Language") { /* Navigate to language settings or perform action */ }
            SettingOption("Change Theme") { /* Navigate to theme settings or perform action */ }

            // Use context to start the ProfileSettingsActivity
            val context = LocalContext.current
            SettingOption("Profile Settings") {
               val intent = Intent(context, ProfileSettingsActivity::class.java)
                context.startActivity(intent)

            }
        }
    }
}

@Composable
fun SettingOption(optionText: String, onClick: () -> Unit) {
    Text(
        text = optionText,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    )
}
