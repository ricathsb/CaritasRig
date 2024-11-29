package com.superbgoal.caritasrig.ComposableScreen.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.superbgoal.caritasrig.R
import com.superbgoal.caritasrig.functions.LoadingButton
import com.superbgoal.caritasrig.functions.auth.signUpUser

@Composable
fun SignUpScreen(viewModel: SignUpViewModel = SignUpViewModel(),navController:NavController) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val confirmPassword by viewModel.confirmPassword.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val backgroundColor = Color(0xFF473947)
    val textFieldColor = Color(0xFF796179)
    val textColor = Color(0xFF1e1e1e)
    val buttonColor = Color(0xFF211321)
    var isPasswordVisible by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = stringResource(id = R.string.sign_up_title), style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 30.dp))

        TextField(
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Outlined.Email, contentDescription = null, tint = textColor)
            },
            value = email,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { viewModel.updateEmail(it) },
            label = { Text(stringResource(id = R.string.email_label), color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor,
            ),

            )

        TextField(
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Outlined.Password, contentDescription = null, tint = textColor)
            },
            value = password,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { viewModel.updatePassword(it) },
            label = { Text(stringResource(id = R.string.password_label), color = textColor) },
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor,
            ),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    val icon = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    val description = if (isPasswordVisible) stringResource(id = R.string.hide_password) else stringResource(id = R.string.show_password)
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        tint = Color.White
                    )
                }
            },

            )
        TextField(
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Outlined.Password, contentDescription = null, tint = textColor)
            },
            value = confirmPassword,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { viewModel.updateConfirmPassword(it) },
            label = { Text(stringResource(id = R.string.confirm_password_label), color = textColor) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors().copy(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                cursorColor = Color.White,
                focusedContainerColor = textFieldColor,
                unfocusedContainerColor = textFieldColor,
            ),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    val icon = if (isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                    val description = if (isPasswordVisible) stringResource(id = R.string.hide_password) else stringResource(id = R.string.show_password)
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        tint = Color.White
                    )
                }
            },
        )

        LoadingButton(
            text = stringResource(id = R.string.create_account),
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            textColor = Color.White,
            coroutineScope = coroutineScope,
            onClick = {
                signUpUser(email, password, confirmPassword ,context,navController)
            },
            modifier = Modifier.fillMaxWidth(),

            )
        TextButton(
            onClick = {
                navController.navigate("login")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.already_have_account), color = Color.White)
        }
    }
}