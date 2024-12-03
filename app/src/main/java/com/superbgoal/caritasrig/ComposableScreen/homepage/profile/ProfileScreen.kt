package com.superbgoal.caritasrig.ComposableScreen.homepage.profile

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.aay.compose.donutChart.DonutChart
import com.aay.compose.donutChart.model.PieChartData
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.ComposableScreen.homepage.home.HomeViewModel
import com.superbgoal.caritasrig.functions.countUserBuilds
import com.superbgoal.caritasrig.functions.countUserFavorites
import java.util.Locale

@Composable
fun ProfileScreen(homeViewModel: HomeViewModel, appController: NavController, navController: NavController) {
    val user by homeViewModel.user.collectAsState()

    // State to hold favorite and build data
    val categoryCounts = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val totalFavorites = remember { mutableStateOf(0) }
    val totalBuilds = remember { mutableStateOf(0) }

    // State for controlling the visibility of additional details
    val isExpanded = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        homeViewModel.loadUserData("currentUserId")
        countUserFavorites { counts, total ->
            categoryCounts.value = counts
            totalFavorites.value = total
        }
        countUserBuilds { builds ->
            totalBuilds.value = builds
        }
    }

    val currentUser = user

    // Colors from values/colors.xml
    val overallBackgroundColor = colorResource(id = R.color.brown2)
    val cardBackgroundColor = colorResource(id = R.color.brown)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(overallBackgroundColor)
            .padding(0.dp)
    ) {
        Image(
        painter = painterResource(id = R.drawable.component_bg),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )
        // Use LazyColumn to make the entire screen scrollable
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Card Section
            item {
                Card(
                    backgroundColor = cardBackgroundColor,
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 8.dp
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        // Profile Section
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Profile Image
                            if (currentUser?.profileImageUrl != null) {
                                AsyncImage(
                                    model = currentUser.profileImageUrl,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, Color(0xFFBB86FC), CircleShape),
                                    placeholder = painterResource(id = R.drawable.baseline_person_24),
                                    error = painterResource(id = R.drawable.baseline_person_24)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Default Profile Icon",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .border(3.dp, Color(0xFFBB86FC), CircleShape),
                                    tint = Color(0xFFBB86FC)
                                )
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            // Full Name and Username
                            Column() {
                                Text(
                                    text = "${currentUser?.firstName ?: "First Name"} ${currentUser?.lastName ?: "Last Name"}",
                                    style = MaterialTheme.typography.h5,
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "@${currentUser?.username ?: "username"}",
                                    style = MaterialTheme.typography.body1,
                                    color = Color(0xFFBB86FC)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Expandable Details
                        AnimatedVisibility(visible = isExpanded.value) {
                            Column {
                                // Display Total Builds
                                Text(
                                    text = "Total Builds: ${totalBuilds.value}",
                                    style = MaterialTheme.typography.body1,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // Display Favorite Data
                                Text(
                                    text = "Total Favorites: ${totalFavorites.value}",
                                    style = MaterialTheme.typography.body1,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                // Donut Chart for Favorite Categories
                                val pieChartData = categoryCounts.value.map { (category, count) ->
                                    PieChartData(
                                        partName = category,
                                        data = count.toDouble(),
                                        color = Color((0xFF000000 + (0xFFFFFF * Math.random())).toLong()) // Random colors
                                    )
                                }

                                if (pieChartData.isNotEmpty()) {
                                    DonutChart(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(350.dp),
                                        descriptionStyle = TextStyle(color = Color.White),
                                        textRatioStyle = TextStyle(color = Color.White),
                                        pieChartData = pieChartData,
                                        centerTitle = "Favorites",
                                        centerTitleStyle = TextStyle(
                                            color = Color(0xFFBB86FC),
                                            fontWeight = FontWeight.Bold
                                        ),
                                        outerCircularColor = Color.LightGray,
                                        innerCircularColor = Color.Gray,
                                        ratioLineColor = Color.White
                                    )
                                }
                            }
                        }

                        // Show/Hide Button
                        Button(
                            onClick = { isExpanded.value = !isExpanded.value },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = cardBackgroundColor,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.elevation(0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Ikon Kiri
                                Icon(
                                    painter = painterResource(
                                        id = if (isExpanded.value) R.drawable.ic_up else R.drawable.ic_down
                                    ),
                                    contentDescription = if (isExpanded.value) "Collapse" else "Expand",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Teks
                                Text(
                                    text = if (isExpanded.value) "Hide" else "Show",
                                    style = MaterialTheme.typography.button,
                                    color = Color.White
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Ikon Kanan
                                Icon(
                                    painter = painterResource(
                                        id = if (isExpanded.value) R.drawable.ic_up else R.drawable.ic_down
                                    ),
                                    contentDescription = if (isExpanded.value) "Collapse" else "Expand",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Settings Options
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth().background(color = Color.Black.copy(alpha = 0.3f))
                ) {
                    Divider(
                        color = Color.Gray, // Warna garis
                        thickness = 1.dp
                    )
                    SettingOption(stringResource(id = R.string.change_language)) {
                        showDialog = true
                    }
                    Divider(
                        color = Color.Gray, // Warna garis
                        thickness = 1.dp
                    )
                    SettingOption(stringResource(R.string.profile_settings)) {
                        navController.navigate("profile_settings")
                    }
                    Divider(
                        color = Color.Gray, // Warna garis
                        thickness = 1.dp
                    )
                    SettingOption("About Us") {
                        navController.navigate("about_us")
                    }
                    Divider(
                        color = Color.Gray, // Warna garis
                        thickness = 1.dp
                    )
                    SettingOption(stringResource(id = R.string.logout)) {
                        // Sign out user
                        FirebaseAuth.getInstance().signOut()

                        // Clear Shared Preferences
                        val sharedPreferences = context.getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().clear().apply()

                        // Navigate to login screen
                        appController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                    Divider(
                        color = Color.Gray, // Warna garis
                        thickness = 1.dp
                    )
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
fun SettingOption(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = Color.White
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_right),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun LanguageSelectionDialog(onDismiss: () -> Unit, onLanguageSelected: (String) -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { androidx.compose.material3.Text(text = stringResource(id = R.string.select_language)) },
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
    androidx.compose.material3.Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onDismiss()
                onLanguageSelected(languageCode)
            }
            .padding(16.dp),
        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
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