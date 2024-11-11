package com.superbgoal.caritasrig.activity.homepage.profileicon

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
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
    var imageUrl by remember { mutableStateOf("") }

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
                    firstname = user.firstName
                    lastname = user.lastName
                    username = user.username
                    dateBirth = user.dateOfBirth
                    imageUrl = user.profileImageUrl ?: ""
                    Log.d("ProfileSettingsScreen",imageUrl)
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
            Toast.makeText(context, context.getString(R.string.user_not_authenticated), Toast.LENGTH_SHORT).show()
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
        Text(text = stringResource(id = R.string.profile_settings), style = MaterialTheme.typography.titleLarge)

        var isViewingProfileImage by remember { mutableStateOf(false) } // Untuk mengontrol tampilan view foto profil

//        val imagePickerLauncher = rememberLauncherForActivityResult(
//            contract = ActivityResultContracts.GetContent(),
//            onResult = { uri: Uri? ->
//                if (uri != null) {
//                    imageUri = uri
//                } else {
//                    Log.d("ImagePicker", "User cancelled image selection")
//                }
//            }
//        )

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            Log.d("Modifier", "Long clicked! yeay")
                        },
                        onTap = {
                            imagePickerLauncher.launch("image/*")
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            ProfileIcon(imageUri, imageUrl)

            // Icon add/remove overlay
            Box(
                modifier = Modifier
                    .offset(x = 50.dp, y = 50.dp)
                    .size(32.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = CircleShape
                    )
                    .clickable {
                        if (imageUri != null) {
                            // Hanya klik pada ikon remove yang menghapus gambar
                            imageUri = null
                        } else {
                            // Jika belum ada gambar, buka picker untuk memilih gambar
                            imagePickerLauncher.launch("image/*")
                        }
                    }
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (imageUri != null) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = if (imageUri != null) stringResource(id = R.string.remove_photo_profile) else stringResource(id = R.string.add_photo_profile),
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

// Dialog untuk menampilkan foto profil jika isViewingProfileImage = true
        if (isViewingProfileImage && imageUri != null) {
            AlertDialog(
                onDismissRequest = { isViewingProfileImage = false }, // Tutup dialog saat diketuk di luar
                buttons = {},
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Transparent)
                            .padding(0.dp) // Hilangkan padding
                    ) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop, // Mengisi seluruh area dengan crop
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f) // Mengatur rasio tampilan gambar
                        )
                    }
                }
            )
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
                label = { Text(stringResource(id = R.string.first_name), color = textColor) },
                placeholder = { Text(text = stringResource(id = R.string.enter_first_name), color = Color.Gray) },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                value = lastname,
                onValueChange = { lastname = it },
                label = { Text(stringResource(id = R.string.last_name), color = textColor) },
                placeholder = { Text(text = stringResource(id = R.string.enter_last_name), color = Color.Gray) },
                colors = TextFieldDefaults.colors().copy(
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
        }

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.username), color = textColor) },
            placeholder = { Text(text = stringResource(id = R.string.enter_username), color = Color.Gray) },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = dateBirth,
            onValueChange = { dateBirth = it },
            label = { Text(stringResource(id = R.string.date_of_birth), color = textColor) },
            placeholder = { Text(text = stringResource(id = R.string.enter_date_of_birth), color = Color.Gray) },
            colors = TextFieldDefaults.colors().copy(
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (firstname.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.first_name_required), Toast.LENGTH_SHORT).show()
                } else if (lastname.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.last_name_required), Toast.LENGTH_SHORT).show()
                } else if (username.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.username_required), Toast.LENGTH_SHORT).show()
                } else if (dateBirth.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.date_of_birth_required), Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true
                    // Membuat objek User untuk disimpan
                    val user = User(
                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        firstName = firstname,
                        lastName = lastname,
                        username = username,
                        dateOfBirth = dateBirth,
                        profileImageUrl = imageUrl // Kirim imageUrl jika tidak ada gambar baru
                    )

                    // Memanggil fungsi updateUserProfileData untuk mengunggah gambar dan menyimpan data
                    updateUserProfileData(
                        user = user,
                        imageUri = imageUri,
                        imageUrl = imageUrl,
                        context = context
                    ) { success ->
                        isLoading = false
                        if (success) {
                            Toast.makeText(context, context.getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                            // Arahkan ke HomepageActivity setelah berhasil update data
                            val intent = Intent(context, HomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(intent)
                            activity?.finish()
                        } else {
                            Toast.makeText(context, context.getString(R.string.profile_updated_failed), Toast.LENGTH_SHORT).show()
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
                Text(stringResource(id = R.string.save_changes))
            }
        }
    }
}

@Composable
fun ProfileIcon(imageUri: Uri?, imageUrl: String?) {
    when {
        imageUri != null -> {
            // Jika imageUri ada, tampilkan gambar dari Uri
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = "Selected image",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
        }
        imageUrl != null -> {
            // Jika imageUrl ada, tampilkan gambar dari URL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = "Image from URL",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
            )
        }
        else -> {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Icon",
                modifier = Modifier.size(150.dp),
                tint = Color.White
            )
        }
    }
}
