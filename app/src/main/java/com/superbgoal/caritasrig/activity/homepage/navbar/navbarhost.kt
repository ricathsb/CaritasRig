package com.superbgoal.caritasrig.activity.homepage.navbar

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.AsyncImage
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.buildtest.BuildListScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.BuildScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.CasingScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.CpuCoolerScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.CpuScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.HeadphoneScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.InternalHardDriveScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.KeyboardScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.MemoryScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.MotherboardScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.MouseScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.PowerSupplyScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.component.VideoCardScreen
import com.superbgoal.caritasrig.activity.homepage.home.HomeScreen
import com.superbgoal.caritasrig.activity.homepage.home.HomeViewModel
import com.superbgoal.caritasrig.activity.homepage.settings.AboutUsScreen
import com.superbgoal.caritasrig.activity.homepage.settings.SettingsScreen
import com.superbgoal.caritasrig.data.model.User

@Composable
fun NavbarHost(
    homeViewModel: HomeViewModel = viewModel(),
    buildViewModel: BuildViewModel = viewModel(),
    appController: NavController,

    ) {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) } // Default ke Home (index 0)

    Scaffold(
        topBar = {
            val currentBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry.value?.destination?.route
            val isProfileScreen = currentRoute?.startsWith("profile") == true

            val specificRoutes = listOf("settings", "about_us", "settings_profile")
            val isSpecificRoute = specificRoutes.contains(currentRoute)

            val title = when (currentRoute) {
                "home" -> "Home"
                "settings" -> "Settings"
                "about_us" -> "About Us"
                "settings_profile" -> stringResource(id = R.string.profile_settings)
                "trending" -> "Trending"
                "build" -> "Build"
                "benchmark" -> "Benchmark"
                "favorite" -> "Favorite Component"
                else -> "CaritasRig"
            }
            Log.d("NavbarHost", "Current Route: $currentRoute")

            AppTopBar(
                navigateToProfile = { user ->
                    navController.navigate("profile/${user?.username ?: "unknown"}") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                navigateToSettings = {
                    navController.navigate("settings") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                isProfileScreen = isProfileScreen,
                title = title,
                isSpecificRoute = isSpecificRoute,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },

        bottomBar = {
            BottomNavigationBar(
                selectedItem = selectedItem, // Pastikan ini adalah state yang dikelola
                onItemSelected = { index ->
                    // Update selectedItem di sini
                    selectedItem = index // Misalnya, jika Anda menggunakan state untuk menyimpan nilai ini
                    when (index) {
                        0 -> navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                            launchSingleTop = true
                        }
                        1 -> navController.navigate("trending") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        2 -> navController.navigate("build") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        3 -> navController.navigate("benchmark") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                        4 -> navController.navigate("favorite") {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
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
                SettingsScreen(navController,appController)
            }
            composable("about_us") {
                AboutUsScreen()
            }
            composable("trending") {
                Text(text = "Trending")
            }
            composable("build") {
                BuildListScreen(navController,buildViewModel)
            }
            composable("benchmark") {
                Text(text = "Benchmark")
            }
            composable("favorite") {
                Text(text = "Favorite")
            }
            composable(
                route = "build_details",
            ) { BuildScreen(buildViewModel, navController)
            }
            composable("cpu_screen") { CpuScreen(navController) }
            composable("casing_screen") { CasingScreen(navController) }
            composable("cpu_cooler_screen") { CpuCoolerScreen(navController) }
            composable("gpu_screen") { VideoCardScreen(navController) }
            composable("motherboard_screen") { MotherboardScreen(navController) }
            composable("internal_hard_drive_screen") { InternalHardDriveScreen(navController) }
            composable("power_supply_screen") { PowerSupplyScreen(navController) }
            composable("headphone_screen") { HeadphoneScreen(navController) }
            composable("keyboard_screen") { KeyboardScreen(navController) }
            composable("mouse_screen") { MouseScreen(navController) }
            composable("memory_screen") { MemoryScreen(navController) }
        }
    }
}

@Composable
fun AppTopBar(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToProfile: (User?) -> Unit,
    navigateToSettings: () -> Unit,
    onBackClick: () -> Unit = {},
    isProfileScreen: Boolean = false,
    isSpecificRoute: Boolean = false,
    title: String
) {
    val user by homeViewModel.user.collectAsState(initial = null)
    val navbarColor = Color(0xFF473947);
    TopAppBar(
        backgroundColor = navbarColor,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)

            ) {
                // Tampilkan tombol back jika berada di specific route atau profile screen
                if (isSpecificRoute || isProfileScreen) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
                Text(text = title, fontSize = 20.sp)
            }
        },
        actions = {
            if (isSpecificRoute) {
                // Tidak menampilkan aksi tambahan pada specific route (bisa diubah jika diperlukan)
            } else if (isProfileScreen) {
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
                }
            } else {
                // Jika bukan di halaman profil, tampilkan icon profil
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
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        },
        contentColor = Color.White,
        modifier = Modifier.height(60.dp),
        elevation = 4.dp
    )
}

@Composable
fun BottomNavigationBar(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit
) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Trending,
        NavigationItem.Build,
        NavigationItem.Benchmark,
        NavigationItem.Favorite
    )
    val navbarColor = Color(0xFF473947)

    BottomAppBar(
        backgroundColor = navbarColor,
        cutoutShape = CircleShape,
        modifier = Modifier.height(60.dp),
        elevation = 8.dp
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index

            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else Color.Gray, // Mengubah warna berdasarkan status dipilih
                        modifier = Modifier.size(if (isSelected) 30.dp else 24.dp) // Mengubah ukuran berdasarkan status dipilih
                    )
                },
                selected = isSelected,
                onClick = { onItemSelected(index) },
                label = {
                    Text(
                        text = item.title,
                        color = if (isSelected) Color.White else Color.Gray // Mengubah warna teks berdasarkan status dipilih
                    )
                },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.Gray,
            )
        }
    }
}


sealed class NavigationItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : NavigationItem("Home", Icons.Default.Home) // Pastikan ikon ini valid
    object Trending : NavigationItem("Trending", Icons.Filled.TrendingUp) // Ubah ke Icons.Filled agar lebih stabil
    object Build : NavigationItem("My Build", Icons.Default.DesktopWindows)
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

