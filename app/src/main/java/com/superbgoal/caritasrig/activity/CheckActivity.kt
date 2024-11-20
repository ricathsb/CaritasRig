package com.superbgoal.caritasrig.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.activity.auth.logintest.LoginScreen
import com.superbgoal.caritasrig.activity.auth.register.RegisterViewModel
import com.superbgoal.caritasrig.activity.auth.registertest.RegisterScreen
import com.superbgoal.caritasrig.activity.auth.signup.SignUpViewModel
import com.superbgoal.caritasrig.activity.auth.signuptest.SignUpScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.BuildListScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.BuildScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.CasingScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.CpuCoolerScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.CpuScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.HeadphoneScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.InternalHardDriveScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.KeyboardScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.MotherboardScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.MouseScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.PowerSupplyScreen
import com.superbgoal.caritasrig.activity.homepage.buildtest.componenttest.VideoCardScreen
import com.superbgoal.caritasrig.activity.homepage.navbar.NavbarHost
import com.superbgoal.caritasrig.activity.homepage.navbar.ProfileScreen
import com.superbgoal.caritasrig.activity.homepage.profileicon.AboutUsScreen
import com.superbgoal.caritasrig.activity.homepage.screentest.ProfileSettingsScreen
import com.superbgoal.caritasrig.activity.homepage.screentest.SettingsScreen
import com.superbgoal.caritasrig.functions.auth.AuthenticationManager

class CheckActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var authenticationManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            CaritasRigApp()
        }
    }

    @Composable
    fun CaritasRigApp() {
        val navController = rememberNavController()
        val startDestination = remember { mutableStateOf<String?>(null) }
        val context = LocalContext.current
        val authenticationManager = remember { AuthenticationManager(context) }


        // Check user authentication state
        LaunchedEffect(auth.currentUser) {
            val user = auth.currentUser
            if (user == null) {
                setStartDestinationForUnauthenticated(navController) { destination ->
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
                    LoginScreen(navController)
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

    fun setStartDestinationForUnauthenticated(
        navController: NavController,
        callback: (String) -> Unit
    ) {
        callback("login") // Default for unauthenticated users
    }

    fun setStartDestination(
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


