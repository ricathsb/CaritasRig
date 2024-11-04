package com.superbgoal.caritasrig.activity


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.auth.AuthResponse
import com.superbgoal.caritasrig.auth.AuthenticationManager
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val auth = FirebaseAuth.getInstance()

        val fromRegistration = intent.getBooleanExtra("fromRegistration", false)

        if (!fromRegistration && auth.currentUser != null) {
            val user = auth.currentUser
            if (user != null) {
                val userId = user.uid
                val authenticationManager = AuthenticationManager(this)
                authenticationManager.checkUserInDatabase(userId)

                if (user.isEmailVerified) {
                    Log.d("cekemail", "emailverivied")
                    authenticationManager.checkUserInDatabase(userId)
                } else {
                    Toast.makeText(this, "Please verify your email before proceeding.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val emailFromSignUp = intent.getStringExtra("email") ?: ""
        setContent {
            CaritasRigTheme {
                Scaffold { paddingValues ->
                    SwipeableLoginScreen(
                        modifier = Modifier.padding(paddingValues),
                        initialEmail = emailFromSignUp
                    )
                }
            }
        }
    }
}

@Composable
fun SwipeableLoginScreen(
    modifier: Modifier = Modifier,
    initialEmail: String = ""
) {
    var offsetY by remember { mutableStateOf(1000f) }
    val animatedOffsetY by animateFloatAsState(targetValue = offsetY)
    val backgroundColor = Color(0xFF473947)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor) // Bisa disesuaikan untuk background utama
    ) {
        // Background layer
        Image(
            painter = painterResource(id = R.drawable.background), // Pastikan background.png ada di res/drawable
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )

        // Login screen layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                //.clip(shape = MaterialTheme.shapes.extraLarge)
                .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume() // Menandakan gesture telah diproses
                        offsetY += dragAmount

                        // Batas swipe
                        offsetY = offsetY.coerceIn(0f, 1000f)
                    }
                }
                .background(Color.Transparent) // Warna background login screen
        ) {
            LoginScreenContent(initialEmail)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(initialEmail: String = "") {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val autheticationManager = remember { AuthenticationManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val buttonColor = Color(0xFF211321)

    Column(
        modifier = Modifier
            .clip(shape = MaterialTheme.shapes.extraLarge)
            .fillMaxSize()
            .background(backgroundColor)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.loginicon),
            contentDescription = "Login Icon",
            modifier = Modifier
                .size(160.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        )

        TextField(
            value = email,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { email = it },
            label = { Text("Email Address", color = textColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White,
                focusedLabelColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                containerColor = textFieldColor,
            ),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        TextField(
            value = password,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { password = it },
            label = { Text("Password", color = textColor) },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White,
                focusedLabelColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                containerColor = textFieldColor,
            ),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        //login button
        Button(
            onClick = {
                if (password.isBlank() || email.isBlank()) {
                    Toast.makeText(context, "Email and password cannot be blank", Toast.LENGTH_SHORT).show()
                } else if (password.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                } else {
                    coroutineScope.launch {
                        autheticationManager.loginWithEmail(email, password).collect { authResponse ->
                            when (authResponse) {
                                is AuthResponse.Success -> {
                                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(context, HomeActivity::class.java)
                                    context.startActivity(intent)
                                }
                                is AuthResponse.Error -> {
                                    Toast.makeText(context, authResponse.message, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = buttonColor,
            )
        ) {
            Text(text = "LOGIN", fontWeight = FontWeight.Bold, color = Color.White)
        }

        // additional options and Google sign-in button
        // ...
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 1.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sign Up Text
            Text(
                text = "Sign up",
                color = Color.Cyan,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .clickable {
                        Log.d("SignUpActivity", "SignUpActivity started")
                        val intent = Intent(context, SignUpActivity::class.java)
                        context.startActivity(intent)
                    }
                    .padding(top = 8.dp)
            )

            // Forgot Password Text
            Text(
                text = "Forgot Password?",
                color = Color.Cyan,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .clickable {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Please enter your email to reset password", Toast.LENGTH_SHORT).show()
                        } else {
                            autheticationManager.resetPassword(email)
                                .onEach { authResponse ->
                                    when (authResponse) {
                                        is AuthResponse.Success -> {
                                            Toast.makeText(context, "Password reset link has been sent to your email.", Toast.LENGTH_SHORT).show()
                                        }
                                        is AuthResponse.Error -> {
                                            Toast.makeText(context, "Failed to send reset link: ${authResponse.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                .launchIn(coroutineScope)
                        }
                    }
                    .padding(top = 8.dp)
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "or continue with",
                color = Color.White  // Sesuaikan warna teks dengan tema
            )
        }
        //sign in google
        OutlinedButton(
            onClick = {
                autheticationManager.signInWithGoogle()
                    .launchIn(coroutineScope)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White  // Warna background button
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logogoogle),
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = "Sign in with Google",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black  // Warna teks button
                )
            }
        }
    }
}
