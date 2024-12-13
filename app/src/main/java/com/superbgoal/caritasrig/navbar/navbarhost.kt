package com.superbgoal.caritasrig.navbar

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.screen.homepage.benchmark.BenchmarkScreen
import com.superbgoal.caritasrig.screen.homepage.build.BuildListScreen
import com.superbgoal.caritasrig.screen.homepage.build.BuildScreen
import com.superbgoal.caritasrig.screen.homepage.build.BuildViewModel
import com.superbgoal.caritasrig.screen.homepage.build.component.CasingScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.CpuCoolerScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.CpuScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.HeadphoneScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.InternalHardDriveScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.KeyboardScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.MemoryScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.MotherboardScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.MouseScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.PowerSupplyScreen
import com.superbgoal.caritasrig.screen.homepage.build.component.VideoCardScreen
import com.superbgoal.caritasrig.screen.homepage.compare.ComparisonScreen
import com.superbgoal.caritasrig.screen.homepage.favorites.FavoriteScreen
import com.superbgoal.caritasrig.screen.homepage.home.HomeViewModel
import com.superbgoal.caritasrig.screen.homepage.homepage.HomeScreen2
import com.superbgoal.caritasrig.screen.homepage.homepage.NewsArticleScreen
import com.superbgoal.caritasrig.screen.homepage.profile.ProfileScreen
import com.superbgoal.caritasrig.screen.homepage.settings.AboutUsScreen
import com.superbgoal.caritasrig.screen.homepage.settings.profilesettings.ProfileSettingsScreen
import com.superbgoal.caritasrig.screen.homepage.settings.profilesettings.ProfileSettingsViewModel
import com.superbgoal.caritasrig.screen.homepage.sharedBuild.SharedBuildScreen
import com.superbgoal.caritasrig.data.model.User

@Composable
fun NavbarHost(
    appController: NavController,
) {
    val homeViewModel: HomeViewModel = viewModel()
    val buildViewModel: BuildViewModel = viewModel()
    val profileViewModel: ProfileSettingsViewModel = viewModel()
    val navController = rememberNavController()
    val buildTitle by buildViewModel.buildTitle.observeAsState("")
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
                "profile/{username}" -> stringResource(id = R.string.profile)
                "home" -> stringResource(id = R.string.home)
                "settings" -> stringResource(id = R.string.settings)
                "about_us" -> stringResource(id = R.string.about_us)
                "profile_settings" -> stringResource(id = R.string.profile_settings)
                "compare" -> stringResource(id = R.string.compare)
                "build" -> stringResource(id = R.string.build)
                "benchmark" -> stringResource(id = R.string.benchmark)
                "favorite" -> stringResource(id = R.string.favorite_component)
                "build_details" -> { buildTitle.ifEmpty { stringResource(id = R.string.new_build) } }
                "cpu_screen" -> stringResource(id = R.string.cpu)
                "casing_screen" -> stringResource(id = R.string.casing)
                "cpu_cooler_screen" -> stringResource(id = R.string.cpu_cooler)
                "gpu_screen" -> stringResource(id = R.string.gpu)
                "motherboard_screen" -> stringResource(id = R.string.motherboard)
                "internal_hard_drive_screen" -> stringResource(id = R.string.internal_hard_drive)
                "power_supply_screen" -> stringResource(id = R.string.power_supply)
                "headphone_screen" -> stringResource(id = R.string.headphone)
                "keyboard_screen" -> stringResource(id = R.string.keyboard)
                "mouse_screen" -> stringResource(id = R.string.mouse)
                "memory_screen" -> stringResource(id = R.string.memory)
                "news_article_screen" -> stringResource(id = R.string.news_article)
                "shared_build_screen" -> stringResource(id = R.string.shared_build)
                else -> stringResource(id = R.string.caritasrig)
            }
            Log.d("NavbarHost", "Current Route: $currentRoute")

            AppTopBar(
                navigateToProfile = { user ->
                    navController.navigate("profile/${user?.username ?: "unknown"}") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                isProfileScreen = isProfileScreen,
                title = title,
                isSpecificRoute = isSpecificRoute,
                onBackClick = {
                    navController.popBackStack()
                },
                currentRoute = currentRoute,
                buildViewModel = buildViewModel
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
                HomeScreen2(navController = navController,buildViewModel)
            }
            composable("profile/{username}") {
                ProfileScreen(homeViewModel = homeViewModel,appController=appController,navController=navController)
            }
            composable("about_us") {
                AboutUsScreen()
            }
            composable("compare") {
                ComparisonScreen()
            }
            composable("build") {
                BuildListScreen(navController,buildViewModel)
            }
            composable("benchmark") {
                BenchmarkScreen(navController)
            }
            composable("favorite") {
                FavoriteScreen()
            }
            composable(
                route = "build_details",
            ) { BuildScreen(buildViewModel, navController)
            }
            //component for add comoponent
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
            composable("profile_settings"){
                ProfileSettingsScreen (homeViewModel = homeViewModel, viewModel = profileViewModel)
            }
            composable("shared_build_screen"){
                SharedBuildScreen()
            }
        }
    }
}

@Composable
fun AppTopBar(
    homeViewModel: HomeViewModel = viewModel(),
    navigateToProfile: (User?) -> Unit,
    onBackClick: () -> Unit = {},
    isProfileScreen: Boolean = false,
    isSpecificRoute: Boolean = false,
    title: String,
    currentRoute: String?,
    buildViewModel: BuildViewModel
) {
    val user by homeViewModel.user.collectAsState(initial = null)
    val navbarColor = Color(0xFF473947)
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
                Text(
                    text = title,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f) // Berikan weight agar title tidak tertindih
                )
            }
        },
        actions = {
            when {
                isSpecificRoute -> {
                    // Tidak menampilkan aksi tambahan pada specific route (bisa diubah jika diperlukan)
                }
                isProfileScreen -> {
                    // Tidak menampilkan aksi tambahan pada specific route (bisa diubah jika diperlukan)
                }
                currentRoute == "build_details" -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            modifier = Modifier.fillMaxHeight(),
                            onClick = {
                                buildViewModel.setNewDialogState(true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Build Icon",
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        IconButton(
                            modifier = Modifier.fillMaxHeight(),
                            onClick = {
                                buildViewModel.setShareDialogState(true)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                modifier = Modifier.size(22.dp)
                            )
                        }
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
                }
                else -> {
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
                        contentDescription = null,
                        tint = if (isSelected) Color.Black else Color.Gray,
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
                        text = stringResource(id = item.title.toInt()),
                        fontSize = 10.sp,
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
    data object Home : NavigationItem("home", Icons.Default.Home, R.string.home.toString())
    data object Trending : NavigationItem("compare", Icons.Default.Balance, R.string.compare.toString())
    data object Build : NavigationItem("build", Icons.Default.Construction, R.string.build.toString())
    data object Benchmark : NavigationItem("benchmark", Icons.Default.StackedBarChart, R.string.benchmark.toString())
    data object Favorite : NavigationItem("favorite", Icons.Default.Star, R.string.favorite.toString())
}