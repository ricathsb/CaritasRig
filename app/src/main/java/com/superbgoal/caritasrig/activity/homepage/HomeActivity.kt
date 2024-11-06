package com.superbgoal.caritasrig.activity.homepage

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.profileicon.AboutUsActivity
import com.superbgoal.caritasrig.activity.auth.LoginActivity
import com.superbgoal.caritasrig.activity.homepage.profileicon.SettingsActivity
import com.superbgoal.caritasrig.data.model.User

class HomeActivity : ComponentActivity() {
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

        setContent {
            HomeScreen(userId, onLogout = {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            })
        }
    }
}

@Composable
fun HomeScreen(userId: String, onLogout: () -> Unit) {
    var user by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Firebase database initialization
    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    val database = FirebaseDatabase.getInstance(databaseUrl).reference

    LaunchedEffect(userId) {
        database.child("users").child(userId).child("userData").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getValue(User::class.java)
                } else {
                    errorMessage = "User not found"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                errorMessage = "Failed to load data: ${error.message}"
            }
        })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop, // Adjusts how the image scales
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
                    UserProfile()
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
            ProfileIcon(user = user, onLogout = onLogout)
        }
    }
}

@Composable
fun ProfileIcon(user: User?, onLogout: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    IconButton(onClick = { showDialog = true }) {
        if (user?.profileImageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.profileImageUrl)
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Log.d("ProfileIcon", "Profile Image URL: ${user.profileImageUrl}")
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Default Profile Icon",
                modifier = Modifier.size(40.dp)
            )
        }
    }

    if (showDialog) {
        ProfileDialog(
            user = user,
            onDismissRequest = { showDialog = false },
            onLogout = onLogout
        )
    }
}

@Composable
fun ProfileDialog(user: User?, onDismissRequest: () -> Unit, onLogout: () -> Unit) {
    val email = remember { getCurrentUserEmail() }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismissRequest,
        containerColor = Color.Transparent,
        textContentColor = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier,
        text = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Close (X) button at the top right
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

                // Dialog content
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
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
                            Text(email ?: "No email available")
                        } ?: Text("No user information available")

                        Spacer(modifier = Modifier.height(16.dp))

                        TransparentIconButton(
                            text = "Activity",
                            icon = R.drawable.icons_activity,
                            onClick = { /* Activity action */ }
                        )

                        TransparentIconButton(
                            text = "Settings",
                            icon = R.drawable.icons_settings,
                            onClick = {
                                // Navigate to SettingsActivity
                                val intent = Intent(context, SettingsActivity::class.java)
                                context.startActivity(intent)
                            }
                        )

                        TransparentIconButton(
                            text = "About Us",
                            icon = R.drawable.icons_aboutus,
                            onClick = {
                                val intent = Intent(context, AboutUsActivity::class.java)
                                context.startActivity(intent)
                            }
                        )

                        TransparentIconButton(
                            text = "Log Out",
                            icon = R.drawable.icons_logout,
                            onClick = onLogout
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
    )
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
fun UserProfile() {
    val context = LocalContext.current // Memindahkan context ke dalam lingkup fungsi UserProfile

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Search Bar
        var searchText by remember { mutableStateOf("") }
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription = null)
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(Icons.Filled.Close, contentDescription = null)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between cards
        ) {
            item {
                // Build Card
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .clickable {
                            context.startActivity(Intent(context, BuildActivity::class.java)) // Navigate to BuildActivity
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
                                text = "Build",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(text = "Create your own setup")
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
//                        .clickable {
//                            context.startActivity(Intent(context, TrendingActivity::class.java))
//                        },
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
                                text = "Trending",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(text = "Find popular part")
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
//
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
                                text = "Benchmarking",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                            Text(text = "Compare")
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(30.dp))

        // Horizontal Scrollable Cards for AMD, NVIDIA, Intel, etc. without text, with icons sized 20x20 dp
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
//                        .clickable {
//                            context.startActivity(Intent(context, AmdActivity::class.java))
//                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.amd_logo), // Replace with your AMD icon resource
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp) // Set icon size to 20x20 dp
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
//                        .clickable {
//                            context.startActivity(Intent(context, NvidiaActivity::class.java))
//                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nvidia_logo), // Replace with your NVIDIA icon resource
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp) // Set icon size to 20x20 dp
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
//                        .clickable {
//                            context.startActivity(Intent(context, IntelActivity::class.java))
//                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.intel_logo), // Replace with your Intel icon resource
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp) // Set icon size to 20x20 dp
                            .padding(4.dp)
                    )
                }
            }

            // Add more items for other brands if needed
        }

        Spacer(modifier = Modifier.height(16.dp))
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
