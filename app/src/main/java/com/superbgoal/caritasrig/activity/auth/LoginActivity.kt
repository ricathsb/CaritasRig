package com.superbgoal.caritasrig.activity.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.activity.homepage.HomeActivity
import com.superbgoal.caritasrig.functions.auth.AuthResponse
import com.superbgoal.caritasrig.functions.auth.AuthenticationManager
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(durationMillis = 600) // Durasi animasi 500ms
    )
    val backgroundColor = Color(0xFF473947)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        offsetY += dragAmount
                        offsetY = offsetY.coerceIn(0f, 1000f)
                    }
                }
                .background(Color.Transparent)
        ) {
            // Panggil LoginScreenContent dengan dua argumen
            LoginScreenContent(
                initialEmail = initialEmail,
                onOffsetChange = { newOffsetY -> offsetY = newOffsetY }
            )
        }
    }
}

@Composable
fun LoginScreenContent(initialEmail: String = "", onOffsetChange: (Float) -> Unit) {
    var email by remember { mutableStateOf(initialEmail) }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isArrowUp by remember { mutableStateOf(true) } // State untuk ikon
    val context = LocalContext.current
    val authenticationManager = remember { AuthenticationManager(context) }
    val coroutineScope = rememberCoroutineScope()
    val textColor = Color(0xFF1e1e1e)
    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val buttonColor = Color(0xFF211321)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .clip(shape = MaterialTheme.shapes.extraLarge)
                .fillMaxSize()
                .background(backgroundColor)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (isArrowUp) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(22.dp)
                    .clickable {
                        // Ubah offsetY saat ikon ditekan
                        if (isArrowUp) {
                            onOffsetChange(0f) // Geser ke atas
                        } else {
                            onOffsetChange(1000f) // Kembali ke posisi awal
                        }
                        // Ubah status ikon
                        isArrowUp = !isArrowUp
                    }
            )
            Image(
                painter = painterResource(id = R.drawable.loginicon),
                contentDescription = "Login Icon",
                modifier = Modifier
                    .size(144.dp)
                    .padding(top = 1.dp, bottom = 1.dp)
            )

            TextField(
                value = email,
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = textColor)
                },
                singleLine = true,
                onValueChange = { email = it },
                label = { Text("Email Address", color = textColor) },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Outlined.Password, contentDescription = null, tint = textColor)
                },
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = textColor) },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor
                ),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        val icon = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        val description = if (isPasswordVisible) "Hide password" else "Show password"
                        Icon(
                            imageVector = icon,
                            contentDescription = description,
                            tint = Color.White
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    isLoading = true
                    coroutineScope.launch {
                        if (password.isBlank() || email.isBlank()) {
                            Toast.makeText(context, "Email and password cannot be blank", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        } else if (password.length < 6) {
                            Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        } else {
                            authenticationManager.loginWithEmail(email, password).collect { authResponse ->
                                isLoading = false
                                when (authResponse) {
                                    is AuthResponse.Success -> {
                                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                        context.startActivity(Intent(context, HomeActivity::class.java))
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

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sign up",
                    color = Color.Cyan,
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, SignUpActivity::class.java))
                    }.padding(top = 8.dp)
                )

                Text(
                    text = "Forgot Password?",
                    color = Color.Cyan,
                    modifier = Modifier.clickable {
                        if (email.isBlank()) {
                            Toast.makeText(context, "Please enter your email to reset password", Toast.LENGTH_SHORT).show()
                        } else {
                            isLoading = true
                            authenticationManager.resetPassword(email).onEach { authResponse ->
                                isLoading = false
                                when (authResponse) {
                                    is AuthResponse.Success -> Toast.makeText(context, "Password reset link has been sent to your email.", Toast.LENGTH_SHORT).show()
                                    is AuthResponse.Error -> Toast.makeText(context, "Failed to send reset link: ${authResponse.message}", Toast.LENGTH_SHORT).show()
                                }
                            }.launchIn(coroutineScope)
                        }
                    }.padding(top = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "or continue with", color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    isLoading = true
                    authenticationManager.signInWithGoogle().onEach {
                        isLoading = false
                    }.launchIn(coroutineScope)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logogoogle),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp).padding(end = 8.dp)
                    )
                    Text(
                        text = "Sign in with Google",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}
