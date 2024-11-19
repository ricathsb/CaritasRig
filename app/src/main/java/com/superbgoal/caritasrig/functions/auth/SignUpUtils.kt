package com.superbgoal.caritasrig.functions.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

suspend fun signUpUser(

    email: String,
    password: String,
    confirmPassword: String,
    context: Context,
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()

    if (password != confirmPassword) {
        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val imageUri = ""
        val userId = result.user?.uid

        if (userId != null) {
            result.user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                if (verificationTask.isSuccessful) {
                    Toast.makeText(context, "Isi data diri Anda", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Gagal mengirim email verifikasi.", Toast.LENGTH_LONG).show()
                }
            }

            navController.navigate("register")

        } else {
            Log.e("SignUpActivity", "Failed to create account: User ID not found")
            Toast.makeText(context, "Failed to create account: User ID not found", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Log.e("SignUpActivity", "Failed to create account: ${e.message}")
        Toast.makeText(context, "Failed to create account: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
