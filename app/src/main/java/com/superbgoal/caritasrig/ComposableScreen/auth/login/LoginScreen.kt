package com.superbgoal.caritasrig.ComposableScreen.auth.login

import android.widget.Toast
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.auth.AuthResponse
import com.superbgoal.caritasrig.functions.auth.AuthenticationManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LoginScreen(navController : NavController,
                loginViewModel: LoginViewModel = viewModel()
) {
    val offsetY by loginViewModel.offsetY.collectAsState()
    val animatedOffsetY by animateFloatAsState(
        targetValue = offsetY,
        animationSpec = tween(durationMillis = 600), label = ""
    )
    val backgroundColor = Color(0xFF473947)
    val sancreekFont = FontFamily(Font(R.font.sancreek))
    val sairastencilone = FontFamily(Font(R.font.sairastencilone))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg4),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Column (
            modifier = Modifier.padding(40.dp),
        ){
            Text(
                text = "CaritasRig",
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.White,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sancreekFont
            )
            Text(
                text = "Pick Parts.",
                modifier = Modifier.padding(bottom = 4.dp),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sairastencilone
            )
            Text(
                text = "Build Your PC.",
                modifier = Modifier.padding(bottom = 4.dp),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sairastencilone
            )
            Text(
                text = "Compare.",
                modifier = Modifier.padding(bottom = 4.dp),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sairastencilone
            )
            Text(
                text = "Benchmark.",
                modifier = Modifier.padding(bottom = 16.dp),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = sairastencilone
            )
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(0, animatedOffsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        loginViewModel.updateOffsetY(offsetY + dragAmount)
                    }
                }
                .background(Color.Transparent)
        ) {
            LoginScreenContent(
                onOffsetChange = { newOffsetY -> loginViewModel.updateOffsetY(newOffsetY) },
                viewModel = loginViewModel
                ,navController = navController
            )
        }
    }
}

@Composable
fun LoginScreenContent(
    onOffsetChange: (Float) -> Unit,
    viewModel: LoginViewModel,navController : NavController
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isArrowUp by viewModel.isArrowUp.collectAsState()
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
                        if (isArrowUp) {
                            onOffsetChange(0f)
                        } else {
                            onOffsetChange(1000f)
                        }
                        viewModel.toggleArrowDirection()
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
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text(stringResource(id = R.string.email), color = textColor) },
                leadingIcon = {
                    Icon(Icons.Outlined.Email, contentDescription = null, tint = textColor)
                },
                singleLine = true,
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text(stringResource(id = R.string.password), color = textColor) },
                leadingIcon = {
                    Icon(Icons.Outlined.Password, contentDescription = null, tint = textColor)
                },
                singleLine = true,
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        val icon = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                        val description = if (isPasswordVisible) stringResource(id = R.string.hide_password) else stringResource(id = R.string.show_password)
                        Icon(imageVector = icon, contentDescription = description, tint = Color.White)
                    }
                },
                colors = TextFieldDefaults.colors().copy(
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = textFieldColor,
                    unfocusedContainerColor = textFieldColor
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.setLoading(true)
                    coroutineScope.launch {
                        if (password.isBlank() || email.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                            viewModel.setLoading(false)
                        } else if (password.length < 6) {
                            Toast.makeText(context, context.getString(R.string.password_length_error), Toast.LENGTH_SHORT).show()
                            viewModel.setLoading(false)
                        } else {
                            authenticationManager.loginWithEmail(email, password, navController = navController).collect { authResponse ->
                                viewModel.setLoading(false)
                                when (authResponse) {
                                    is AuthResponse.Success -> {
                                        authenticationManager.checkUserInDatabase(userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",navController)
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
                colors = ButtonDefaults.outlinedButtonColors(containerColor = buttonColor)
            ) {
                Text(stringResource(id = R.string.login), color = Color.White)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 1.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.signup),
                    color = Color.Cyan,
                    modifier = Modifier.clickable {
                        navController.navigate("signup")
                    }.padding(top = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.forgot_password),
                    color = Color.Cyan,
                    modifier = Modifier.clickable {
                        if (email.isBlank()) {
                            Toast.makeText(context, context.getString(R.string.reset_password_prompt), Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.setLoading(true)
                            authenticationManager.resetPassword(email).onEach { authResponse ->
                                viewModel.setLoading(false)
                                when (authResponse) {
                                    is AuthResponse.Success -> Toast.makeText(context, context.getString(
                                        R.string.reset_password_success), Toast.LENGTH_SHORT).show()
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
                Text(text = stringResource(id = R.string.or_continue_with), color = Color.White)
            }

            OutlinedButton(
                onClick = {
                    viewModel.setLoading(true)
                    authenticationManager.signInWithGoogle(navController = navController).onEach {
                        viewModel.setLoading(false)
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
                        text = stringResource(id = R.string.google_sign_in),
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