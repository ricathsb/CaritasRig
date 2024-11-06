package com.superbgoal.caritasrig.activity.homepage.profileicon

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.activity.homepage.HomeActivity
import com.superbgoal.caritasrig.data.loadUserData
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.updateUserProfileData
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme

class ProfileSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaritasRigTheme {
                Scaffold {
                    ProfileSettingsScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(modifier: Modifier = Modifier) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var dateBirth by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val context = LocalContext.current
    val activity = context as? Activity
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    // Load user data on screen creation
    LaunchedEffect(Unit) {
        if (userId != null) {
            loadUserData(
                userId = userId,
                onUserDataLoaded = { user ->
                    firstname = user.firstName ?: ""
                    lastname = user.lastName ?: ""
                    username = user.username ?: ""
                    dateBirth = user.dateOfBirth ?: ""
                    // Load profile image if available
                    if (!user.profileImageUrl.isNullOrEmpty()) {
                        imageUri = Uri.parse(user.profileImageUrl)
                    }
                },
                onFailure = { errorMessage ->
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }

    val imageCropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
        } else {
            val exception = result.error
            Log.d("imageCropLauncher", "Error: ${exception?.message ?: "Unknown error"}")
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val cropOptions = CropImageContractOptions(uri, CropImageOptions().apply {
                aspectRatioX = 1
                aspectRatioY = 1
                fixAspectRatio = true
            })
            imageCropLauncher.launch(cropOptions)
        } else {
            Log.d("imagePickerLauncher", "User did not select an image")
        }
    }

    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Profile Settings", style = MaterialTheme.typography.titleLarge)

        Box(
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onTap = { imagePickerLauncher.launch("image/*") })
            },
            contentAlignment = Alignment.Center
        ) {
            ProfileIcon(imageUri = imageUri)
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = firstname,
                onValueChange = { firstname = it },
                label = { Text("First Name", color = textColor) },
                placeholder = { Text(text = "Enter first name", color = Color.Gray) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = textFieldColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text("Last Name", color = textColor) },
                placeholder = { Text(text = "Enter last name", color = Color.Gray) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = textFieldColor,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
        }

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username", color = textColor) },
            placeholder = { Text(text = "Enter username", color = Color.Gray) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldColor,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = dateBirth,
            onValueChange = { dateBirth = it },
            label = { Text("Date of Birth", color = textColor) },
            placeholder = { Text(text = "Enter date of birth", color = Color.Gray) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = textFieldColor,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (firstname.isEmpty()) {
                    Toast.makeText(context, "First Name is required", Toast.LENGTH_SHORT).show()
                } else if (lastname.isEmpty()) {
                    Toast.makeText(context, "Last Name is required", Toast.LENGTH_SHORT).show()
                } else if (username.isEmpty()) {
                    Toast.makeText(context, "Username is required", Toast.LENGTH_SHORT).show()
                } else if (dateBirth.isEmpty()) {
                    Toast.makeText(context, "Date of Birth is required", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    // Membuat objek User untuk disimpan
                    val user = User(
                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        firstName = firstname,
                        lastName = lastname,
                        username = username,
                        dateOfBirth = dateBirth,
                        profileImageUrl = null // URL akan diperbarui setelah upload
                    )

                    // Memanggil fungsi updateUserProfileData untuk mengunggah gambar dan menyimpan data
                    updateUserProfileData(
                        user = user,
                        imageUri = imageUri,
                        context = context
                    ) { success ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                            // Arahkan ke HomepageActivity setelah berhasil update data
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Save Changes")
            }
        }
    }
}

@Composable
fun ProfileIcon(imageUri: Uri?) {
    if (imageUri != null) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
    } else {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Default Icon",
            modifier = Modifier.size(150.dp),
            tint = Color.White
        )
    }
}
