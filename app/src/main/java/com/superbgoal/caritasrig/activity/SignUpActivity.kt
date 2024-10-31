package com.superbgoal.caritasrig.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CaritasRigTheme {
                Scaffold {
                    SignUpScreen(modifier = Modifier.padding(it))
                }
            }
        }
    }
}

@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    signUpUser(email, password, context)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }
    }
}

suspend fun signUpUser(email: String, password: String, context: Context) {
    val auth = FirebaseAuth.getInstance()
    try {
        auth.createUserWithEmailAndPassword(email, password).await()
        Toast.makeText(context, "Account created successfully!", Toast.LENGTH_SHORT).show()

        // Kembali ke LoginActivity dengan membawa email
        val intent = Intent(context, LoginActivity::class.java).apply {
            putExtra("email", email)
        }
        context.startActivity(intent)
        if (context is Activity) {
            context.finish()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Failed to create account: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}