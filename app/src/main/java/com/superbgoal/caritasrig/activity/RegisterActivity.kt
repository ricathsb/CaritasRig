package com.superbgoal.caritasrig.activity

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
import androidx.compose.material.icons.filled.DateRange
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
import com.superbgoal.caritasrig.auth.LoadingButton
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.saveUserData
import com.superbgoal.caritasrig.data.uploadImageToFirebase
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.IconButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.rememberDatePickerState
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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userId = (context as? RegisterActivity)?.intent?.getStringExtra("userId")
    val email = (context as? RegisterActivity)?.intent?.getStringExtra("email") ?: ""
    Log.d("image", imageUri.toString())
    val imageUrl = imageUri?.toString() ?: (context as? RegisterActivity)?.intent?.getStringExtra("imageUrl")
    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val buttonColor = Color(0xFF211321)
    val imageCropLauncher = rememberLauncherForActivityResult(
        CropImageContract()
    )
     { result ->
        if(result.isSuccessful){
            imageUri = result.uriContent
        } else {
            val exception = result.error
            Log.d("imageCropLauncher", exception.toString())
        }

     }

    if (imageUri != null) {
            val source = ImageDecoder.createSource(context.contentResolver,imageUri!!)
            bitmap = ImageDecoder.decodeBitmap(source)
        }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri : Uri? ->
        val cropOptions = CropImageContractOptions(uri, CropImageOptions().apply {
            aspectRatioX = 1 // Ubah sesuai kebutuhan
            aspectRatioY = 1
            fixAspectRatio = true // Untuk mengunci rasio
        })
        imageCropLauncher.launch(cropOptions)

    }

    Log.d("imageUrl", imageUrl.toString())
    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Register",
            style = MaterialTheme.typography.titleLarge
        )
        Box(
            modifier = Modifier.pointerInput(Unit) {
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

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        )
        {
            TextField(
                modifier = modifier.weight(1f),
                value = firstname,
                shape = MaterialTheme.shapes.medium,
                onValueChange = { firstname = it },
                label = { Text("First Name", color = textColor) },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.Transparent,
                    unfocusedLabelColor = Color.Transparent,
                    containerColor = textFieldColor,
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
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.Transparent,
                    unfocusedLabelColor = Color.Transparent,
                    containerColor = textFieldColor,

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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White,
                focusedLabelColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                containerColor = textFieldColor,

                ),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        var showDatePicker by remember { mutableStateOf(false) }
        val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

// Format tanggal
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // Fungsi untuk mengkonversi Long ke LocalDate
        fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()

        TextField(
            value = dateOfBirth,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { },
            label = { Text("Date of Birth", color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White,
                focusedLabelColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                containerColor = textFieldColor,

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


        LoadingButton(
            text = "SUBMIT",
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            textColor = Color.White,
            coroutineScope = coroutineScope,
            onClick = {
                if (userId != null) {
                    Log.d("email", "userId: $email")

                    // Cek apakah ada `imageUri`
                    imageUri?.let { uri ->
                        uploadImageToFirebase(uri) { firebaseImageUrl ->
                            Log.d("firebaseImageUrl", firebaseImageUrl)

                            saveUserData(
                                user = User(userId, firstname, lastname, username, dateOfBirth, email, firebaseImageUrl),
                                context
                            )
                        }
                    } ?: run {
                        saveUserData(
                            user = User(userId, firstname, lastname, username, dateOfBirth, email, imageUrl),
                            context
                        )
                    }

                    Log.d("imageUri1", imageUrl.toString())
                } else {
                    Toast.makeText(context, "User ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
    }

@Composable
fun RegisterProfileIcon(imageUri: Uri?, imageUrl: String?) {
    when {
        imageUrl != null -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Log.d("ProfileIcon", "Profile Image URL: $imageUrl")
        }
        imageUri != null -> {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
            Log.d("ProfileIcon", "Selected Image URI: $imageUri")
        }
        else -> {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Default Profile Icon",
                modifier = Modifier.size(100.dp)
            )
        }
    }
}



