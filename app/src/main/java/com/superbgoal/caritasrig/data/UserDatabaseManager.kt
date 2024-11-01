package com.superbgoal.caritasrig.data

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.superbgoal.caritasrig.activity.HomeActivity
import com.superbgoal.caritasrig.activity.LoginActivity
import com.superbgoal.caritasrig.data.model.User
import java.io.IOException

fun saveUserData(user: User, context: Context) {
    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference

    // Map data yang akan disimpan, tanpa email
    val userMap = mapOf(
        "firstName" to user.firstName,
        "lastName" to user.lastName,
        "username" to user.username,
        "dateOfBirth" to user.dateOfBirth,
        "profileImageUrl" to user.profileImageUrl
    )
    Log.d("imageUrl", user.profileImageUrl.toString())
    database.child("users").child(user.userId).setValue(userMap)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Data berhasil disimpan.", Toast.LENGTH_SHORT).show()

                // Tentukan intent berdasarkan keberadaan email
                val intent = if (user.email.isEmpty()) {
                    Log.d("logingoogle", "emailnya kosong ${user.email}")
                    Intent(context, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("userId", user.userId)

                    }
                } else {
                    Log.d("logingoogle", "emailnya ada ${user.email}")
                    Intent(context, LoginActivity::class.java).apply {
                        putExtra("email", user.email)
                        putExtra("fromRegistration", true)
                    }
                }
                context.startActivity(intent)
            } else {
                Log.e("RegisterActivity", "Gagal menyimpan data: ${task.exception?.message}")
            }
        }
}

fun <T> loadItemsFromResources(context: Context, resourceId: Int, typeToken: TypeToken<List<T>>): List<T> {
    val inputStream = context.resources.openRawResource(resourceId)
    val jsonString: String
    try {
        jsonString = inputStream.bufferedReader().use { it.readText() }
    } catch (ioException: IOException) {
        Log.e("loadItemsFromResources", "Error reading JSON file: ${ioException.message}")
        ioException.printStackTrace()
        return emptyList()
    }

    return Gson().fromJson(jsonString, typeToken.type)
}



