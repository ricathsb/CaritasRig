package com.superbgoal.caritasrig.activity.auth

import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.superbgoal.caritasrig.functions.auth.LoadingButton
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.superbgoal.caritasrig.functions.auth.signUpUser
import com.superbgoal.caritasrig.ui.theme.CaritasRigTheme

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val buttonColor = Color(0xFF211321)

    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.titleLarge)

        TextField(
            value = email,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { email = it },
            label = { Text("Email Address", color = textColor) },
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
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)

        )
        TextField(
            value = confirmPassword,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password", color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                cursorColor = Color.White,
                focusedLabelColor = Color.Transparent,
                unfocusedLabelColor = Color.Transparent,
                containerColor = textFieldColor,
            ),
            visualTransformation = PasswordVisualTransformation(),
            textStyle = LocalTextStyle.current.copy(color = Color.White)
        )

        LoadingButton(
            text = "Create Account",
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            textColor = Color.White,
            coroutineScope = coroutineScope, // Pass the coroutine scope
            onClick = {
                signUpUser(email, password, confirmPassword, context)
            },
            modifier = Modifier.fillMaxWidth(),

        )
        TextButton(
            onClick = {
                context.startActivity(Intent(context, LoginActivity::class.java))
                (context as SignUpActivity)
                    .finish()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Already have an account? Log in", color = Color.White)
        }
    }
}


