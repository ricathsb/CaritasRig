package com.superbgoal.caritasrig.activity.homepage.navbar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.benchmark.BenchmarkScreen
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
import com.superbgoal.caritasrig.activity.homepage.newsApi.HomeScreen2
import com.superbgoal.caritasrig.activity.homepage.newsApi.HomeViewModel2
import com.superbgoal.caritasrig.activity.homepage.newsApi.NewsArticleScreen
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
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            val currentBackStackEntry = navController.currentBackStackEntryAsState()
            val currentRoute = currentBackStackEntry.value?.destination?.route
            val isProfileScreen = currentRoute?.startsWith("profile") == true
            Log.d("NavbarHost", "Current Route: $currentRoute")

            val specificRoutes = listOf("settings", "about_us", "settings_profile")
            val isSpecificRoute = specificRoutes.contains(currentRoute)

            val title = when (currentRoute) {
                "profile/{username}" -> "Profile"
                "home" -> "Home"
                "settings" -> "Settings"
                "about_us" -> "About Us"
                "settings_profile" -> stringResource(id = R.string.profile_settings)
                "trending" -> "Trending"
                "build" -> "Build"
                "benchmark" -> "Benchmark"
                "favorite" -> "Favorite Component"
                "build_details" -> "Building :3"
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
                navController = navController,
                onItemSelected = { route ->
                    navController.navigate(route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
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
                HomeScreen2(navController = navController)
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
                BenchmarkScreen(navController)
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
            composable("news_article_screen") { NewsArticleScreen()}
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
                            imageVector = Icons.Default.ArrowBackIosNew,
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
                                .size(35.dp)
                                .clip(CircleShape),
                            placeholder = painterResource(id = R.drawable.baseline_person_24),
                            error = painterResource(id = R.drawable.baseline_person_24)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Default Profile Icon",
                            modifier = Modifier.size(35.dp)
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
    navController: NavController,
    onItemSelected: (String) -> Unit
) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Trending,
        NavigationItem.Build,
        NavigationItem.Benchmark,
        NavigationItem.Favorite
    )
    val currentRoute = currentRoute(navController)
    val navbarColor = Color(0xFF473947)

    NavigationBar(
        containerColor = navbarColor,
        tonalElevation = 8.dp,
        modifier = Modifier.height(75.dp)
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(if (isSelected) 30.dp else 24.dp)
                    )
                },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        onItemSelected(item.route)
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.White else Color.Gray
                    )
                },

                alwaysShowLabel = false,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}



sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    data object Home : NavigationItem("home", Icons.Default.Home, "Home")
    data object Trending : NavigationItem("trending", Icons.Default.TrendingUp, "Trending")
    data object Build : NavigationItem("build", Icons.Default.DesktopWindows, "Build")
    data object Benchmark : NavigationItem("benchmark", Icons.Default.BarChart, "Benchmark")
    data object Favorite : NavigationItem("favorite", Icons.Default.Favorite, "Favorite")
}



@Composable
fun ProfileScreen(homeViewModel: HomeViewModel) {
    val user by homeViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadUserData("currentUserId")
    }

    val currentUser = user

    // Dark-themed background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark background
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp) // Move content towards the top
        ) {
            // Profile Image
            if (currentUser?.profileImageUrl != null) {
                AsyncImage(
                    model = currentUser.profileImageUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .border(3.dp, Color(0xFFBB86FC), CircleShape), // Accent color border
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
                        .border(3.dp, Color(0xFFBB86FC), CircleShape), // Accent color border
                    tint = Color(0xFFBB86FC) // Accent color
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Full Name
            Text(
                text = "${currentUser?.firstName ?: "First Name"} ${currentUser?.lastName ?: "Last Name"}",
                style = MaterialTheme.typography.h5,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Username
            Text(
                text = "@${currentUser?.username ?: "username"}",
                style = MaterialTheme.typography.body1,
                color = Color(0xFFBB86FC) // Accent color
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth
            if (currentUser?.dateOfBirth?.isNotEmpty() == true) {
                Text(
                    text = "Born on ${currentUser.dateOfBirth}",
                    style = MaterialTheme.typography.body2,
                    color = Color(0xFFB0BEC5) // Muted gray
                )
            }
        }
    }
}



