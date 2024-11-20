package com.superbgoal.caritasrig.activity.auth.registertest

import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.auth.register.RegisterViewModel
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.saveUserData
import com.superbgoal.caritasrig.data.uploadImageToFirebase
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: RegisterViewModel,navController: NavController) {
    val firstname by viewModel.firstname.collectAsState()
    val lastname by viewModel.lastname.collectAsState()
    val username by viewModel.username.collectAsState()
    val dateOfBirth by viewModel.dateOfBirth.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showDatePicker by viewModel.showDatePicker.collectAsState()
    val buttonColor = Color(0xFF211321)
    val context = LocalContext.current
    val imageUrl = imageUri?.toString() ?: Firebase.auth.currentUser?.photoUrl?.toString()
    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val  currentUser = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = stringResource(id = R.string.register),
            style = MaterialTheme.typography.titleLarge
        )

        var isViewingProfileImage by remember { mutableStateOf(false) }


        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            Log.d("Modifier", "Long clicked! yeay")
                        },
                        onTap = {

                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            RegisterProfileIcon(imageUri, imageUrl)

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
                    .zIndex(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (imageUri != null) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = if (imageUri != null) "Remove Profile Photo" else "Add Profile Photo",
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
                modifier = Modifier.weight(1f),
                value = firstname,
                shape = MaterialTheme.shapes.medium,
                onValueChange = { viewModel.updateFirstname(it) },
                label = { Text(stringResource(id = R.string.first_name), color = textColor) },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor,
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )

            Spacer(modifier = Modifier.width(16.dp))

            TextField(
                modifier = Modifier.weight(1f),
                value = lastname,
                shape = MaterialTheme.shapes.medium,
                onValueChange = { viewModel.updateLastname(it) },
                label = { Text(stringResource(id = R.string.last_name), color = textColor) },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor,
                ),
                textStyle = LocalTextStyle.current.copy(color = Color.White)
            )
        }

        TextField(
            value = username,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { viewModel.updateUsername(it) },
            label = { Text(stringResource(id = R.string.username), color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor,
            ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
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
                // Pengecekan apakah ada field yang kosong
                when {
                    firstname.isEmpty() -> {
                        Toast.makeText(context, context.getString(R.string.first_name_required), Toast.LENGTH_SHORT).show()
                    }
                    lastname.isEmpty() -> {
                        Toast.makeText(context, context.getString(R.string.last_name_required), Toast.LENGTH_SHORT).show()
                    }
                    username.isEmpty() -> {
                        Toast.makeText(context, context.getString(R.string.username_required), Toast.LENGTH_SHORT).show()
                    }
                    dateOfBirth.isEmpty() -> {
                        Toast.makeText(context, context.getString(R.string.date_of_birth_required), Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        viewModel.setLoading(true)
                        val userId = currentUser?.uid
                        if (userId != null) {
                            imageUri?.let { uri ->
                                uploadImageToFirebase(uri) { firebaseImageUrl ->
                                    saveUserData(
                                        user = User(userId, firstname, lastname, username, dateOfBirth, firebaseImageUrl),
                                        context = context
                                    ) { isVerified ->
                                        viewModel.setLoading(false)
                                        if (isVerified) {
                                            Toast.makeText(context, context.getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
                                            navController.navigate("home")
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.verify_email, currentUser.email),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } ?: run {
                                saveUserData(
                                    user = User(userId, firstname, lastname, username, dateOfBirth, imageUrl),
                                    context = context
                                ) { isVerified ->
                                    viewModel.setLoading(false)
                                    if (isVerified) {
                                        Toast.makeText(context, context.getString(R.string.data_saved), Toast.LENGTH_SHORT).show()
                                        navController.navigate("home")
                                    } else {
                                        Toast.makeText(context,
                                            context.getString(R.string.verify_email, currentUser.email),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            },
            enabled = !isLoading,
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = buttonColor,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text(text = stringResource(id = R.string.register), fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        TextButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.already_have_account), color = Color.White)
        }

    }
}

@Composable
fun RegisterProfileIcon(imageUri: Uri?, imageUrl: String?) {
    when {
        imageUri != null -> {
            Log.d("RegisterProfileIcon", "Image URI: $imageUri")
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