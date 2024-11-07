package com.superbgoal.caritasrig.activity.auth

import android.content.Intent
import android.graphics.Bitmap
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
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
import com.superbgoal.caritasrig.activity.homepage.HomeActivity
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.saveUserData
import com.superbgoal.caritasrig.data.uploadImageToFirebase
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CaritasRigTheme {
                Scaffold {
                    RegisterScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val buttonColor = Color(0xFF211321)
    val context = LocalContext.current
    val userId = (context as? RegisterActivity)?.intent?.getStringExtra("userId")
    val email = (context as? RegisterActivity)?.intent?.getStringExtra("email") ?: ""
    val imageUrl = imageUri?.toString() ?: (context as? RegisterActivity)?.intent?.getStringExtra("imageUrl")
    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val currentUser = FirebaseAuth.getInstance().currentUser

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
        } else {
            val exception = result.error
            Log.d("imageCropLauncher", exception.toString())
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
            Log.d("ImagePicker", "User cancelled image selection")
        }
    }

    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.titleLarge
        )

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
                modifier = modifier.weight(1f),
                value = firstname,
                shape = MaterialTheme.shapes.medium,
                onValueChange = { firstname = it },
                label = { Text("First Name", color = textColor) },
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
                modifier = modifier.weight(1f),
                value = lastname,
                shape = MaterialTheme.shapes.medium,
                onValueChange = { lastname = it },
                label = { Text("Last Name", color = textColor) },
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
            onValueChange = { username = it },
            label = { Text("Username", color = textColor) },
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

        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

        TextField(
            value = dateOfBirth,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { },
            label = { Text("Date of Birth", color = textColor) },
            modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
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
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select date", tint = Color.White)
                }
            }
        )

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            dateOfBirth = millis.toLocalDate().format(formatter)
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        Button(
            onClick = {
                // Pengecekan apakah ada field yang kosong
                when {
                    firstname.isEmpty() -> {
                        Toast.makeText(context, "First Name is required", Toast.LENGTH_SHORT).show()
                    }
                    lastname.isEmpty() -> {
                        Toast.makeText(context, "Last Name is required", Toast.LENGTH_SHORT).show()
                    }
                    username.isEmpty() -> {
                        Toast.makeText(context, "Username is required", Toast.LENGTH_SHORT).show()
                    }
                    dateOfBirth.isEmpty() -> {
                        Toast.makeText(context, "Date of Birth is required", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        isLoading = true
                        if (userId != null) {
                            imageUri?.let { uri ->
                                uploadImageToFirebase(uri) { firebaseImageUrl ->
                                    saveUserData(
                                        user = User(userId, firstname, lastname, username, dateOfBirth, email, firebaseImageUrl),
                                        context = context
                                    ) { isVerified ->
                                        isLoading = false
                                        if (isVerified) {
                                            Toast.makeText(context, "Data saved successfully and email verified.", Toast.LENGTH_SHORT).show()
                                            context.startActivity(Intent(context, HomeActivity::class.java))
                                            (context).finish()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Please verify your email ${currentUser?.email ?: "No Email"}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } ?: run {
                                saveUserData(
                                    user = User(userId, firstname, lastname, username, dateOfBirth, email, imageUrl),
                                    context = context
                                ) { isVerified ->
                                    isLoading = false
                                    if (isVerified) {
                                        Toast.makeText(context, "Data saved successfully and email verified.", Toast.LENGTH_SHORT).show()
                                        context.startActivity(Intent(context, HomeActivity::class.java))
                                        (context).finish()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Please verify your email ${currentUser?.email ?: "No Email"}",
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
                Text(text = "Register", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

    }
}

@Composable
fun RegisterProfileIcon(imageUri: Uri?, imageUrl: String?) {
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
