package com.superbgoal.caritasrig.activity.homepage.profileicon

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.profileicon.profilesettings.ProfileSettingsActivity
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import java.util.*

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedLanguage = getSavedLanguage(this)
        setLocale(this, savedLanguage)

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
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.settings)) },
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
            SettingOption(stringResource(id = R.string.change_language)) {
                showDialog = true // Show language selection dialog
            }
            SettingOption(stringResource(id = R.string.change_theme)) {
                // Navigate to theme settings or perform action
            }
            SettingOption(stringResource(id = R.string.profile_settings)) {
                context.startActivity(Intent(context, ProfileSettingsActivity::class.java))
            }
        }
    }

    if (showDialog) {
        LanguageSelectionDialog(
            onDismiss = { showDialog = false },
            onLanguageSelected = { language ->
                setLocale(context, language)
                // Restart activity to apply changes
                context.startActivity(Intent(context, SettingsActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        )
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

@Composable
fun LanguageSelectionDialog(onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.select_language)) },
        confirmButton = {},
        text = {
            Column {
                LanguageOption("English", "en", onDismiss, onLanguageSelected)
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption("Indonesian", "in", onDismiss, onLanguageSelected)
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption("Germany", "de", onDismiss, onLanguageSelected)
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption("France", "fr", onDismiss, onLanguageSelected)
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption("Spanish", "es", onDismiss, onLanguageSelected)
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption("Japanese", "ja", onDismiss, onLanguageSelected)
            }
        }
    )
}

@Composable
fun LanguageOption(
    text: String,
    languageCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onDismiss()
                onLanguageSelected(languageCode)
            }
            .padding(16.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}

fun setLocale(context: Context, language: String) {
    val locale = Locale(language)
    Locale.setDefault(locale)

    val resources = context.resources
    val config = resources.configuration
    config.setLocale(locale)
    config.setLayoutDirection(locale)

    saveLanguagePreference(context, language)

    val updatedContext = context.applicationContext.createConfigurationContext(config)
    resources.updateConfiguration(config, resources.displayMetrics)

}

fun saveLanguagePreference(context: Context, language: String) {
    val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("app_language", language).apply()
}

fun getSavedLanguage(context: Context): String {
    val prefs = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)
    return prefs.getString("app_language", "en") ?: "en"
}

