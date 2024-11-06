package com.superbgoal.caritasrig.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.activity.auth.LoginActivity
import com.superbgoal.caritasrig.functions.auth.AuthenticationManager

class CheckActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            val user = auth.currentUser
            val authenticationManager = AuthenticationManager(this)
            if (user != null) {
                authenticationManager.checkUserInDatabase(user.uid)
            }
        } else {
            navigateToLoginActivity()
        }
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}