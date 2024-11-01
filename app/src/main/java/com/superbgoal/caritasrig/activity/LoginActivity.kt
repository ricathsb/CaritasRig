package com.superbgoal.caritasrig.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.auth.AuthResponse
import com.superbgoal.caritasrig.auth.AuthenticationManager
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach


class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val emailFromSignUp = intent.getStringExtra("email") ?: ""
        setContent {
            CaritasRigTheme {
                Scaffold {
                    LoginScreen(
                        modifier = Modifier.padding(it),
                        initialEmail = emailFromSignUp
                    )
                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    initialEmail: String = ""
) {
    var email by remember {
        mutableStateOf(initialEmail)
    }
    var password by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val autheticationManager = remember {
        AuthenticationManager(context)
    }
    val coroutineScope = rememberCoroutineScope()
    Column(modifier = modifier
        .fillMaxSize()
        .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text="Sign-in",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text="Please fill in your email and password",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Email, contentDescription = null)
            },
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()

        )

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
    )

        Spacer(modifier = Modifier.height(20.dp))

        Button (onClick ={
            if(password.isBlank() and email.isBlank()){
                Toast.makeText(context, "Email and password cannot be blank", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (email.isBlank()) {
                Toast.makeText(context, "Email cannot be blank", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (password.isBlank()) {
                Toast.makeText(context, "Password cannot be blank", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if (password.length < 6) {
                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@Button
            }

            autheticationManager.loginWithEmail(email, password)
                .onEach { authResponse ->
                    when (authResponse) {
                        is AuthResponse.Success -> {
                            Log.d("login", "isinya:  $authResponse")
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            Log.d("HomeActivity", "HomeActivity started")

                            val intent = Intent(context, HomeActivity::class.java)
                            context.startActivity(intent)
                        }
                        is AuthResponse.Error -> {
                            if (authResponse.message == "Please verify your email before logging in.") {
                                Log.d("login", "isinya:  $authResponse")
                                Toast.makeText(context, "Please verify your email to proceed.", Toast.LENGTH_SHORT).show()
                            } else {
                                Log.d("login", "isinya:  $authResponse")
                                Toast.makeText(context, "Your data is incorrect", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .launchIn(coroutineScope)
        },
            modifier = Modifier.fillMaxWidth()){
            Text(
                text = "Sign in" ,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "or continue with")
        }
        OutlinedButton(
            onClick = {
                autheticationManager.signInWithGoogle()
                    .launchIn(coroutineScope)
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Image(
                painter = painterResource(id = R.drawable.logogoogle),
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )

            Text(text = "Sign in with Google",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,)
        }
        OutlinedButton(
            onClick = {
                Log.d("SignUpActivity", "SignUpActivity started")
                val intent = Intent(context, SignUpActivity::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign up")
        }
        OutlinedButton(
            onClick = {
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Forgot Password?")
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun Loginpreview(){
    CaritasRigTheme {
        LoginScreen()
    }
}


