package com.superbgoal.caritasrig.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import kotlinx.coroutines.launch

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
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userId = (context as? RegisterActivity)?.intent?.getStringExtra("userId")
    val email = (context as? RegisterActivity)?.intent?.getStringExtra("email") ?: ""
    val isGoogleLogin = (context as? RegisterActivity)?.intent?.getBooleanExtra("isGoogleLogin", false) ?: false

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Register", style = MaterialTheme.typography.titleLarge)

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

        Button(
            onClick = {
                coroutineScope.launch {
                    if (userId != null) {
                        Log.d("email", "userId: $email")
                        saveUserData(userId, firstname, lastname, username, dateOfBirth, email, context, isGoogleLogin)
                    } else {
                        Toast.makeText(context, "User ID tidak ditemukan.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit")
        }
    }
}

fun saveUserData(
    userId: String,
    firstname: String,
    lastname: String,
    username: String,
    dateOfBirth: String,
    email: String,
    context: Context,
    isGoogleLogin: Boolean
) {
    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference

    val userMap = mapOf(
        "firstName" to firstname,
        "lastName" to lastname,
        "username" to username,
        "dateOfBirth" to dateOfBirth
    )

    database.child("users").child(userId).setValue(userMap)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Data berhasil disimpan.", Toast.LENGTH_SHORT).show()
                val intent = if (email.isNullOrEmpty()) {
                    Log.d("logingoogle", "emailnya kosong $email")
                    Intent(context, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("userId", userId)
                    }
                } else {
                    Log.d("logingoogle", "emailnya ada $email")
                    Intent(context, LoginActivity::class.java).apply {
                        putExtra("email", email)
                    }
                }
                context.startActivity(intent)
            } else {
                Log.e("RegisterActivity", "Gagal menyimpan data: ${task.exception?.message}")
            }
        }
}
