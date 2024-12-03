package com.superbgoal.caritasrig

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.ComposableScreen.auth.login.LoginScreen
import com.superbgoal.caritasrig.ComposableScreen.auth.login.LoginViewModel
import com.superbgoal.caritasrig.ComposableScreen.auth.register.RegisterScreen
import com.superbgoal.caritasrig.ComposableScreen.auth.register.RegisterViewModel
import com.superbgoal.caritasrig.ComposableScreen.auth.signup.SignUpScreen
import com.superbgoal.caritasrig.ComposableScreen.auth.signup.SignUpViewModel
import com.superbgoal.caritasrig.navbar.NavbarHost
import com.superbgoal.caritasrig.functions.auth.AuthenticationManager

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContent {
            CaritasRigApp()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
    }

    @Composable
    fun CaritasRigApp() {
        val loginViewModel : LoginViewModel = viewModel()
        val navController = rememberNavController()
        val startDestination = remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current
        remember { AuthenticationManager(context) }


        // Check user authentication state
        LaunchedEffect(auth.currentUser) {
            val user = auth.currentUser
            if (user == null) {
                setStartDestinationForUnauthenticated { destination ->
                    startDestination.value = destination
                }
            } else {
                setStartDestination(user.uid, navController) { destination ->
                    startDestination.value = destination
                }
            }
        }

        // Only render NavHost if startDestination is determined
        if (startDestination.value != null) {
            NavHost(
                navController = navController,
                startDestination = startDestination.value!!
            ) {
                Log.d("CaritasRigApp", "Start Destination: ${startDestination.value}")
                composable("login") {
                    LoginScreen(navController,
                        loginViewModel = loginViewModel
                    )
                }
                composable("signup") {
                    SignUpScreen(viewModel = SignUpViewModel(), navController)
                }
                composable("register") {
                    RegisterScreen(viewModel = RegisterViewModel(), navController)
                }
                composable("home") {
                    NavbarHost(appController = navController)
                }
            }
        }
    }

    private fun setStartDestinationForUnauthenticated(
        callback: (String) -> Unit
    ) {
        callback("login") // Default for unauthenticated users
    }

    private fun setStartDestination(
        userId: String,
        navController: NavController,
        callback: (String) -> Unit
    ) {
        val context = navController.context
        val authenticationManager = AuthenticationManager(context)

        authenticationManager.checkUserInDatabase(userId, navController) { destination ->
            callback(destination)
        }
    }

}


