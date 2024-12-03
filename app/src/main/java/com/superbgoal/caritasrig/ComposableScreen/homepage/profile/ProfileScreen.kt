package com.superbgoal.caritasrig.ComposableScreen.homepage.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aay.compose.donutChart.DonutChart
import com.aay.compose.donutChart.model.PieChartData
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.ComposableScreen.homepage.home.HomeViewModel
import com.superbgoal.caritasrig.functions.countUserBuilds
import com.superbgoal.caritasrig.functions.countUserFavorites

@Composable
fun ProfileScreen(homeViewModel: HomeViewModel) {
    val user by homeViewModel.user.collectAsState()

    // State to hold favorite data
    val categoryCounts = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val totalFavorites = remember { mutableStateOf(0) }

    // State to hold build data
    val totalBuilds = remember { mutableStateOf(0) }

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
                fontWeight = FontWeight.Normal,
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
}

