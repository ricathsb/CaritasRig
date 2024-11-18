package com.superbgoal.caritasrig.activity.homepage.navbar

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.home.HomeScreen
import com.superbgoal.caritasrig.activity.homepage.home.HomeViewModel
import com.superbgoal.caritasrig.activity.homepage.profileicon.AboutUsScreen
import com.superbgoal.caritasrig.activity.homepage.profileicon.SettingsScreen
import com.superbgoal.caritasrig.data.model.User


@Composable
fun NavbarHost(homeViewModel: HomeViewModel = viewModel()) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            // Ambil rute saat ini
            val currentBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry.value?.destination?.route
            val isProfileScreen = currentRoute?.startsWith("profile") == true

            // Tentukan judul berdasarkan rute
            val title = when {
                currentRoute == "home" -> "Home"
                currentRoute?.startsWith("profile") == true -> "Profile"
                currentRoute == "settings" -> "Settings"
                currentRoute == "aboutus" -> "About Us"
                else -> "CaritasRig"
            }

            AppTopBar(
                navigateToProfile = { user ->
                    navController.navigate("profile/${user?.username ?: "unknown"}")
                },
                navigateToSettings = {
                    navController.navigate("settings")
                },
                isProfileScreen = isProfileScreen,
                title = title,
                navigateToAboutUs = {
                    navController.navigate("aboutus")
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedItem = 0,
                onItemSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home")
                        // Add other cases for other screens
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(viewModel = homeViewModel)
            }
            composable("profile/{username}") {
                ProfileScreen(homeViewModel = homeViewModel)
            }
            composable("settings") {
                SettingsScreen()
            }
            composable ("aboutus"){
                AboutUsScreen()
            }
        }
    }
}



@Composable
fun AppTopBar(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToProfile: (User?) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAboutUs: () -> Unit,
    isProfileScreen: Boolean = false,
    title: String
) {
    val user by homeViewModel.user.collectAsState(initial = null)

    TopAppBar(
        title = {
            Text(text = title, fontSize = 20.sp)
        },
        actions = {
            // Menambahkan Row untuk menyusun icon bersebelahan
            if (isProfileScreen) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon Settings
                    IconButton(onClick = { navigateToSettings() }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Icon Tanda Seru
                    IconButton(onClick = { navigateToAboutUs() }) {
                        Icon(
                            imageVector = Icons.Default.Error,  // Ikon tanda seru
                            contentDescription = "Error Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                // Jika bukan di halaman profil, hanya tampilkan icon profil
                IconButton(onClick = { navigateToProfile(user) }) {
                    if (user?.profileImageUrl != null) {
                        AsyncImage(
                            model = user?.profileImageUrl,
                            contentDescription = "Profile",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(id = R.drawable.baseline_person_24),
                            error = painterResource(id = R.drawable.baseline_person_24)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default Profile Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        },
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White,
        elevation = 4.dp
    )
}



@Composable
fun BottomNavigationBar(selectedItem: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Trending,
        NavigationItem.Build,
        NavigationItem.Benchmark,
        NavigationItem.Favorite
    )

    BottomAppBar(
        cutoutShape = CircleShape,
        elevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val isMiddle = index == 2
            if (isMiddle) {
                FloatingActionButton(
                    onClick = { onItemSelected(index) },
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                }
            } else {
                BottomNavigationItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    selected = selectedItem == index,
                    onClick = { onItemSelected(index) },
                    label = { Text(text = item.title) },
                    selectedContentColor = MaterialTheme.colors.primary,
                    unselectedContentColor = Color.Gray
                )
            }
        }
    }
}

sealed class NavigationItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : NavigationItem("Home", Icons.Default.Home)
    object Trending : NavigationItem("Trending", Icons.AutoMirrored.Filled.TrendingUp)
    object Build : NavigationItem("Build", Icons.Default.Build)
    object Benchmark : NavigationItem("Benchmark", Icons.Default.BarChart)
    object Favorite : NavigationItem("Favorite", Icons.Default.Favorite)
}

@Composable
fun ProfileScreen(homeViewModel: HomeViewModel) {
    val user by homeViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadUserData("currentUserId")
    }

    val currentUser = user // Salin nilai user ke variabel lokal

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image
        if (currentUser?.profileImageUrl != null) {
            AsyncImage(
                model = currentUser.profileImageUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colors.primary, CircleShape),
                placeholder = painterResource(id = R.drawable.baseline_person_24),
                error = painterResource(id = R.drawable.baseline_person_24)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Profile Icon",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colors.primary, CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "${currentUser?.firstName ?: "First Name"} ${currentUser?.lastName ?: "Last Name"}",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "@${currentUser?.username ?: "username"}",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

    }
}

