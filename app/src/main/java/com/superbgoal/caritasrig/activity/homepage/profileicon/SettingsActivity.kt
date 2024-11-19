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
import androidx.navigation.NavController
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.profileicon.profilesettings.ProfileSettingsActivity
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import java.util.*

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val savedLanguage = getSavedLanguage(this)
//        setLocale(this, savedLanguage)

        setContent {
            CaritasRigTheme {
//                SettingsScreen()
            }
        }
    }
}






