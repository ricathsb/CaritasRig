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
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.superbgoal.caritasrig.activity.HomeActivity
import com.superbgoal.caritasrig.activity.LoginActivity
import com.superbgoal.caritasrig.data.model.User
import java.io.IOException
import java.util.UUID

fun saveUserData(user: User, context: Context, callback: (Boolean) -> Unit) {
    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference
    val currentUser = FirebaseAuth.getInstance().currentUser

    currentUser?.reload()?.addOnCompleteListener { reloadTask ->
        if (reloadTask.isSuccessful) {
            val isEmailVerified = currentUser.isEmailVerified
            Log.d("loginStatus", "data disimpan $isEmailVerified")

            if (isEmailVerified) {
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
                            Log.d("loginStatus", "Data saved for verified account")
                            callback(true)  // Return true if data saved and verified
                        } else {
                            Log.e("RegisterActivity", "Failed to save data: ${task.exception?.message}")
                            callback(false)  // Return false if data saving failed
                        }
                    }
            } else {
                Log.d("loginStatus", "Account not verified")
                callback(false)  // Return false if email not verified
            }
        } else {
            Log.e("loginStatus", "Failed to reload user data: ${reloadTask.exception?.message}")
            callback(false)  // Return false if reloading user data failed
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




