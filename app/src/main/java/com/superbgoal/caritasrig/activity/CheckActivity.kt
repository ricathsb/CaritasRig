package com.superbgoal.caritasrig.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.auth.AuthenticationManager

class CheckActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            val user = auth.currentUser
            val authenticationManager = AuthenticationManager(this)
            if (user != null) {
                if (isXiaomiDevice()) {
                    Log.d("XiaomiDevice", "This is a Xiaomi device")
                    navigateToLoadingActivity()
                    authenticationManager.checkUserInDatabase(user.uid)
                }
                else {
                    Log.d("XiaomiDevice", "This is not a Xiaomi device")
                    authenticationManager.checkUserInDatabase(user.uid)
                }
            }
        } else {
            navigateToLoginActivity()
        }
    }

    private fun navigateToLoadingActivity() {
        startActivity(Intent(this, LoadingActivity::class.java))
        finish()
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

private fun isXiaomiDevice(): Boolean {
    return Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)
}

