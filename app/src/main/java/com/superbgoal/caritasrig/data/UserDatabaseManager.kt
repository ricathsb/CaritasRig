package com.superbgoal.caritasrig.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.superbgoal.caritasrig.activity.HomeActivity
import com.superbgoal.caritasrig.activity.LoginActivity
import com.superbgoal.caritasrig.data.model.User
import java.io.IOException
import java.util.UUID

fun saveUserData(user: User, context: Context) {
    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference
    val currentUser = FirebaseAuth.getInstance().currentUser

    // Memuat ulang data pengguna untuk memastikan status verifikasi email terbaru
    currentUser?.reload()?.addOnCompleteListener { reloadTask ->
        if (reloadTask.isSuccessful) {
            val isEmailVerified = currentUser.isEmailVerified
            Log.d("loginStatus", "data disimpan ${isEmailVerified}")

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
                        // Tentukan intent berdasarkan status verifikasi email
                        val intent = if (isEmailVerified) {
                            Log.d("loginStatus", "Akun terverifikasi ${isEmailVerified}")
                            Intent(context, HomeActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                putExtra("userId", user.userId)
                                Toast.makeText(context, "Login Berhasil", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Log.d("loginStatus", "Akun belum terverifikasi ${isEmailVerified}")
                            Intent(context, LoginActivity::class.java).apply {
                                putExtra("email", user.email)
                                putExtra("fromRegistration", true)
                                putExtra("verificationMessage", "Please verify your account before login")
                                Toast.makeText(context, "Please verify your account before login", Toast.LENGTH_SHORT).show()
                            }
                        }
                        context.startActivity(intent)
                    } else {
                        Log.e("RegisterActivity", "Gagal menyimpan data: ${task.exception?.message}")
                    }
                }
        } else {
            Log.e("loginStatus", "Gagal memuat ulang data pengguna: ${reloadTask.exception?.message}")
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

fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("images/${UUID.randomUUID()}")

    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                onSuccess(downloadUri.toString())
            }
        }
        .addOnFailureListener {
            Log.e("UploadError", "Gagal mengupload gambar")
        }
}




