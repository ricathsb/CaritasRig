package com.superbgoal.caritasrig.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.auth.LoadingButton
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.saveUserData
import com.superbgoal.caritasrig.data.uploadImageToFirebase
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme

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

@Composable
fun RegisterScreen(modifier: Modifier = Modifier) {
    var firstname by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userId = (context as? RegisterActivity)?.intent?.getStringExtra("userId")
    val email = (context as? RegisterActivity)?.intent?.getStringExtra("email") ?: ""
    Log.d("image", imageUri.toString())
    val imageUrl = imageUri?.toString() ?: (context as? RegisterActivity)?.intent?.getStringExtra("imageUrl")
    val photoprofilelauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri-> imageUri = uri }
    )
    Log.d("imageUrl", imageUrl.toString())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Register", style = MaterialTheme.typography.titleLarge)

        Button(
            onClick = {
                photoprofilelauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
                Log.d("imageUri", imageUri.toString())
            }
        ) {
            Text(text = "Upload Image")
        }

        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("First Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = lastname,
            onValueChange = { lastname = it },
            label = { Text("Last Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dateOfBirth,
            onValueChange = { dateOfBirth = it },
            label = { Text("Date of Birth (DD/MM/YYYY)") },
            modifier = Modifier.fillMaxWidth()
        )
        LoadingButton(
            text = "Submit",
            coroutineScope = coroutineScope, // Pass the coroutine scope
            onClick = {
                if (userId != null) {
                    Log.d("email", "userId: $email")



                    // Cek apakah ada `imageUri`
                    imageUri?.let { uri ->
                        uploadImageToFirebase(uri) { firebaseImageUrl ->
                            Log.d("firebaseImageUrl", firebaseImageUrl)

                            // Setelah mendapatkan URL dari Firebase Storage, simpan data pengguna
                            saveUserData(
                                user = User(userId, firstname, lastname, username, dateOfBirth, email, firebaseImageUrl),
                                context
                            )
                        }
                    } ?: run {
                        // Jika tidak ada `imageUri`, gunakan `imageUrl` dari `intent` atau null jika tidak ditemukan
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
