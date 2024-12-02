package com.superbgoal.caritasrig.ComposableScreen.homepage.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import java.util.Locale

@Composable
fun SettingsScreen(navController: NavController,appController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            SettingOption(stringResource(id = R.string.change_language)) {
                showDialog = true
            }
            SettingOption(stringResource(R.string.profile_settings)) {
                navController.navigate("profile_settings")
            }

            SettingOption("AboutUs") {
                navController.navigate("about_us")
            }
            SettingOption(stringResource(id = R.string.logout)) {
                FirebaseAuth.getInstance().signOut()

                val sharedPreferences = context.getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                appController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }

            }
        }
    }

    if (showDialog) {
        LanguageSelectionDialog(
            onDismiss = { showDialog = false },
            onLanguageSelected = { languageCode ->
                setLocale(context, languageCode)

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
                Spacer(modifier = Modifier.height(8.dp))
                LanguageOption("China", "zh", onDismiss, onLanguageSelected)
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