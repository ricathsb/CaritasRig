package com.superbgoal.caritasrig.ComposableScreen.homepage.settings.profilesettings

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
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.ComposableScreen.homepage.home.HomeViewModel
import com.superbgoal.caritasrig.MainActivity
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.functions.updateUserProfileData
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    viewModel: ProfileSettingsViewModel,
    homeViewModel: HomeViewModel
) {
    val firstname by viewModel.firstname.collectAsState()
    val lastname by viewModel.lastname.collectAsState()
    val username by viewModel.username.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showDatePicker by viewModel.showDatePicker.collectAsState()

    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val context = LocalContext.current

    // ActivityResultLauncher untuk image crop
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            viewModel.updateImageUri(result.uriContent)
        } else {
            val exception = result.error
            Log.d("imageCropLauncher", exception.toString())
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null && uri.scheme != null) {
            val cropOptions = CropImageContractOptions(uri, CropImageOptions().apply {
                aspectRatioX = 1
                aspectRatioY = 1
                fixAspectRatio = true
            })
            imageCropLauncher.launch(cropOptions)
        } else {
            Log.e("ImagePicker", "Invalid URI or user canceled image selection")
        }
    }


    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        var isViewingProfileImage by remember { mutableStateOf(false) } // Untuk mengontrol tampilan view foto profil

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
            Box(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                Log.d("Modifier", "Long clicked! yeay")
                            },
                            onTap = {
                                try {
                                    imagePickerLauncher.launch("image/*")
                                } catch (e: Exception) {
                                    Log.e("ImagePickerTap", "Unhandled exception during image picker launch: ${e.message}", e)
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
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
                        }
                        .zIndex(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Add/Remove Image",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                value = firstname,
                onValueChange = { viewModel.updateFirstname(it) },
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
                onValueChange = { viewModel.updateLastname(it) },
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
            onValueChange = { viewModel.updateUsername(it) },
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

        val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Picker)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

        TextField(
            value = dateOfBirth,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { },
            label = { Text(stringResource(id = R.string.date_of_birth), color = textColor) },
            modifier = Modifier.fillMaxWidth().clickable {viewModel.updateShowDatePicker(true)},
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor,
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {viewModel.updateShowDatePicker(true)}) {
                    Icon(Icons.Default.DateRange, contentDescription = stringResource(id = R.string.select_date), tint = Color.White)
                }
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = {viewModel.updateShowDatePicker(false)},
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formattedDate = millis.toLocalDate().format(formatter)
                            viewModel.updateDateOfBirth(formattedDate)
                        }
                        viewModel.updateShowDatePicker(false)
                    }) {
                        Text(stringResource(id = R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = {viewModel.updateShowDatePicker(false)}) {
                        Text(stringResource(id = R.string.cancel))
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false // Disable mode toggle to ensure only date picking
                )
            }
        }

        Button(
            onClick = {
                if (firstname.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.first_name_required), Toast.LENGTH_SHORT).show()
                } else if (lastname.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.last_name_required), Toast.LENGTH_SHORT).show()
                } else if (username.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.username_required), Toast.LENGTH_SHORT).show()
                } else if (dateOfBirth.isEmpty()) {
                    Toast.makeText(context, context.getString(R.string.date_of_birth_required), Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setLoading(true)
                    // Membuat objek User untuk disimpan
                    val user = User(
                        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                        firstName = firstname,
                        lastName = lastname,
                        username = username,
                        dateOfBirth = dateOfBirth,
                        profileImageUrl = imageUrl // Kirim imageUrl jika tidak ada gambar baru
                    )

                    // Memanggil fungsi updateUserProfileData untuk mengunggah gambar dan menyimpan data
                    updateUserProfileData(
                        user = user,
                        imageUri = imageUri,
                        imageUrl = imageUrl,
                        context = context
                    ) { success ->
                        viewModel.setLoading(false)
                        if (success) {
                            homeViewModel.loadUserData(userId = user.userId)
                            Toast.makeText(context, context.getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
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
    if (imageUri != null) {
        Log.d("RegisterProfileIcon", "Image URI: $imageUri")
        AsyncImage(
            model = imageUri,
            contentDescription = "Selected image",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
    } else {
        Log.d("RegisterProfileIcon", "Default profile image")
        AsyncImage(
            model = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSgDyR8ueTlFnqdhxAnJ3F8VvNiDm5pkNkteg&s",
            contentDescription = "Image from URL",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )
    }

}





