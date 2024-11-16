package com.superbgoal.caritasrig.activity.homepage.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.auth.login.LoginActivity
import com.superbgoal.caritasrig.activity.homepage.build.BuildActivity
import com.superbgoal.caritasrig.activity.homepage.profileicon.AboutUsActivity
import com.superbgoal.caritasrig.activity.homepage.profileicon.SettingsActivity
import com.superbgoal.caritasrig.data.fetchBuildsWithAuth
import com.superbgoal.caritasrig.data.getUserBuilds
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.model.User

class HomeActivity : ComponentActivity() {
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId == null) {
            // Redirect to LoginActivity if the user is not logged in
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        viewModel.loadUserData(userId)

        setContent {
            HomeScreen(viewModel = viewModel, onLogout = {
                // Hapus data SharedPreferences
                val sharedPreferences = getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                // Logout dari Firebase
                FirebaseAuth.getInstance().signOut()

                // Redirect ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            })
        }
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel, onLogout: () -> Unit) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isProfileDialogVisible by viewModel.isProfileDialogVisible.collectAsStateWithLifecycle()
    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Foreground content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (user != null) {
                    UserProfile(viewModel)
                } else if (errorMessage != null) {
                    ErrorMessage(errorMessage!!)
                } else {
                    LoadingScreen()
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
        ) {
            ProfileIcon(
                user = user,
                onLogout = onLogout,
                showDialog = isProfileDialogVisible,
                toggleDialog = viewModel::toggleProfileDialog
            )
        }
    }
}


@Composable
fun ProfileIcon(user: User?, onLogout: () -> Unit, showDialog: Boolean, toggleDialog: () -> Unit) {
    IconButton(onClick = toggleDialog) {
        if (user?.profileImageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.profileImageUrl)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile Icon",
                modifier = Modifier.size(40.dp)
            )
        }
    }

    if (showDialog) {
        ProfileDialog(user = user, onDismissRequest = toggleDialog, onLogout = onLogout)
    }
}

@Composable
fun ProfileDialog(user: User?, onDismissRequest: () -> Unit, onLogout: () -> Unit) {
    val email = remember { getCurrentUserEmail() }
    val context = LocalContext.current

    // Custom dialog layout
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Dialog content
                if (user?.profileImageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(user.profileImageUrl)
                            .build(),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                user?.let {
                    Text(it.username)
                    Text(email ?: stringResource(id = R.string.no_email_available))
                } ?: Text(stringResource(id = R.string.no_user_information_available))

                Spacer(modifier = Modifier.height(16.dp))

                TransparentIconButton(
                    text = stringResource(id = R.string.activity),
                    icon = R.drawable.icons_activity,
                    onClick = { /* Activity action */ }
                )

                TransparentIconButton(
                    text = stringResource(id = R.string.settings),
                    icon = R.drawable.icons_settings,
                    onClick = {
                        // Navigate to SettingsActivity
                        val intent = Intent(context, SettingsActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                TransparentIconButton(
                    text = stringResource(id = R.string.about_us),
                    icon = R.drawable.icons_aboutus,
                    onClick = {
                        val intent = Intent(context, AboutUsActivity::class.java)
                        context.startActivity(intent)
                    }
                )

                TransparentIconButton(
                    text = stringResource(id = R.string.log_out),
                    icon = R.drawable.icons_logout,
                    onClick = onLogout
                )
            }
        }

        // Close button positioned at the top right corner
        IconButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Dialog",
                tint = Color.Gray
            )
        }
    }
}




@Composable
fun TransparentIconButton(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = Color(171, 161, 157, 255).copy(alpha = 0.7f),
            contentColor = Color.Black
        ),
        contentPadding = PaddingValues(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "$text icon",
                modifier = Modifier.size(24.dp),
                colorFilter = ColorFilter.tint(Color.Gray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = Color.Black
            )
        }
    }
}

@Composable
fun UserProfile(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val builds = remember { mutableStateOf<List<Build>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Fetch builds when this Composable is launched
    LaunchedEffect(Unit) {
        fetchBuildsWithAuth(
            onSuccess = { buildsList ->
                builds.value = buildsList
                isLoading.value = false
                Log.d("BuildScreen", "Builds fetched successfully: $buildsList")
            },
            onFailure = { error ->
                errorMessage.value = error
                isLoading.value = false
                Log.e("BuildScreen", "Error fetching builds: $error")
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Bar
        TextField(
            value = viewModel.searchText.collectAsState().value,
            onValueChange = { viewModel.updateSearchText(it) },
            label = { Text(stringResource(id = R.string.search)) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
            },
            trailingIcon = {
                if (viewModel.searchText.collectAsState().value.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchText("") }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal scrollable items
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Build Card
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .clickable {
                            context.startActivity(Intent(context, BuildActivity::class.java))
                        },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.icons_build),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.build),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(text = stringResource(id = R.string.create_your_own_setup))
                        }
                    }
                }
            }

            item {
                // Trending Card
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.trend),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.trending),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(text = stringResource(id = R.string.find_popular_part))
                        }
                    }
                }
            }

            item {
                // Benchmarking Card
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_hourglass_bottom_24),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = stringResource(id = R.string.benchmarking),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(text = stringResource(id = R.string.compare))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Horizontal Scrollable Cards for AMD, NVIDIA, Intel, etc.
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // AMD Card
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.amd_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                    )
                }
            }

            item {
                // NVIDIA Card
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nvidia_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                    )
                }
            }

            item {
                // Intel Card
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.intel_logo),
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .padding(4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section for displaying the list of builds
        Column(modifier = Modifier.padding(3.dp)) {
            if (isLoading.value) {
                Text("Loading...")
            } else if (errorMessage.value != null) {
                Text("Error: ${errorMessage.value}")
            } else {
                BuildList(
                    builds = builds.value,
                    onBuildClick = { build ->
                        val intent = Intent(context, BuildActivity::class.java).apply {
                            putExtra("build", build) // Pass build data
                        }
                        context.startActivity(intent)
                    }
                )
                }
            }
        }
    }

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
    }
}

fun getCurrentUserEmail(): String? {
    val currentUser = FirebaseAuth.getInstance().currentUser
    return currentUser?.email
}

//@Composable
//fun BuildList(buildIds: List<String>) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        buildIds.forEach { buildId ->
//            BuildCard(buildId = buildId)
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//    }
//}

@Composable
fun BuildList(builds: List<Build>, onBuildClick: (Build) -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(builds) { build ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                        // Action on card click
                        onBuildClick(build)  // Pass the clicked build to the onBuildClick function
                    },
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Display build title
                    Text(
                        text = build.title,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Display processor name if available
                    build.components?.processor?.let { processor ->
                        Text(
                            text = "Processor: ${processor.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display casing name if available
                    build.components?.casing?.let { casing ->
                        Text(
                            text = "Casing: ${casing.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display motherboard name if available
                    build.components?.motherboard?.let { motherboard ->
                        Text(
                            text = "Motherboard: ${motherboard.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display video card name if available
                    build.components?.videoCard?.let { videoCard ->
                        Text(
                            text = "Video Card: ${videoCard.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display headphone name if available
                    build.components?.headphone?.let { headphone ->
                        Text(
                            text = "Headphone: ${headphone.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    // Display internal hard drive name if available
                    build.components?.internalHardDrive?.let { internalHardDrive ->
                        Text(
                            text = "Internal Hard Drive: ${internalHardDrive.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                            Log.d("BuildList", "Displaying internal hard drive name: ${internalHardDrive.name}")
                    }

                    // Display keyboard name if available
                    build.components?.keyboard?.let { keyboard ->
                        Text(
                            text = "Keyboard: ${keyboard.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    build.components?.powerSupply?.let { powerSupply ->
                        Text(
                            text = "Power Supply: ${powerSupply.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    build.components?.mouse?.let { mouse ->
                        Text(
                            text = "Mouse: ${mouse.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}



