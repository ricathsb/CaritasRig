package com.superbgoal.caritasrig.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.model.buildmanager.BuildComponents
import com.superbgoal.caritasrig.data.model.component.Casing
import com.superbgoal.caritasrig.data.model.component.Headphones
import com.superbgoal.caritasrig.data.model.component.InternalHardDrive
import com.superbgoal.caritasrig.data.model.component.Keyboard
import com.superbgoal.caritasrig.data.model.component.Motherboard
import com.superbgoal.caritasrig.data.model.component.Mouse
import com.superbgoal.caritasrig.data.model.component.PowerSupply
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.VideoCard
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
                database.child("users").child(user.userId).child("userData").setValue(userMap)
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


inline fun <reified T> loadItemsFromResources(
    context: Context,
    resourceId: Int
): T {
    val inputStream = context.resources.openRawResource(resourceId)
    val reader = inputStream.bufferedReader()
    return Gson().fromJson(reader, object : TypeToken<T>() {}.type)
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

fun updateUserProfileData(
    user: User,
    imageUri: Uri?,
    imageUrl: String?,
    context: Context,
    callback: (Boolean) -> Unit
) {
    Log.d("ProfileUpdate", "Starting profile update for user: ${user.userId}")

    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    val database: DatabaseReference = FirebaseDatabase.getInstance(databaseUrl).reference

    fun saveDataToDatabase(profileImageUrl: String?) {
        Log.d("ProfileUpdate", "Saving data to database with image URL: $profileImageUrl")

        val userMap = mutableMapOf(
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "username" to user.username,
            "dateOfBirth" to user.dateOfBirth,
            "profileImageUrl" to profileImageUrl
        )

        database.child("users").child(user.userId).child("userData").setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("ProfileUpdate", "Profile data updated successfully")
                    callback(true)
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    Log.e("ProfileUpdate", "Failed to update data. Reason: $errorMessage")
                    callback(false)
                }
            }
    }

    if (imageUri != null && imageUri.scheme == "content") {
        Log.d("ProfileUpdate", "Uploading new image with valid URI: $imageUri")

        val storageRef = FirebaseStorage.getInstance().reference.child("images/${user.userId}")
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d("ProfileUpdate", "Profile image uploaded successfully, URL: ${downloadUri.toString()}")
                    saveDataToDatabase(downloadUri.toString())
                }
                    .addOnFailureListener { exception ->
                        Log.e("UploadError", "Failed to get download URL: ${exception.message}")
                        callback(false)
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("UploadError", "Failed to upload image: ${exception.message}")
                callback(false)
            }
    } else {
        Log.e("ProfileUpdate", "Invalid URI for image upload or no image selected")
        saveDataToDatabase(imageUrl) // Use existing URL if no new image is selected
    }
}






    fun loadUserData(userId: String, onUserDataLoaded: (User) -> Unit, onFailure: (String) -> Unit) {
    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    val database = FirebaseDatabase.getInstance(databaseUrl).reference

    database.child("users").child(userId).child("userData")
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        onUserDataLoaded(user)
                    } else {
                        onFailure("User data is null")
                    }
                } else {
                    onFailure("User not found")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onFailure("Failed to load data: ${error.message}")
            }
        })
}

fun getDatabaseReference(): DatabaseReference {
    val databaseUrl = "https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app"
    return FirebaseDatabase.getInstance(databaseUrl).reference
}

fun saveBuildTitle(userId: String, buildTitle: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    val database = getDatabaseReference()
    val buildData = mapOf(
        "title" to buildTitle
    )

    database.child("users").child(userId).child("builds")
        .push() // Membuat key unik untuk setiap build
        .setValue(buildData) // Menyimpan build sebagai objek dengan `title`
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { error ->
            onFailure("Failed to save build title: ${error.message}")
        }
}

fun getUserBuilds(onSuccess: (List<Build>) -> Unit, onFailure: (String) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId != null) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("users").child(userId).child("builds")
            .get()
            .addOnSuccessListener { dataSnapshot ->
                val builds = mutableListOf<Build>()
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.forEach { snapshot ->
                        val build = snapshot.getValue(Build::class.java)
                        if (build != null) {
                            builds.add(build)
                        }
                    }
                    onSuccess(builds)
                } else {
                    onFailure("No builds found")
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error fetching builds: ${error.message}")
                onFailure("Failed to fetch builds: ${error.message}")
            }
    } else {
        onFailure("User not authenticated.")
    }
}

fun fetchBuildsWithAuth(
    onSuccess: (List<Build>) -> Unit,
    onFailure: (String) -> Unit
) {
    // Ambil UID pengguna yang sedang login
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    if (userId == null) {
        onFailure("User not authenticated or UID not found.")
        return
    }

    val database = getDatabaseReference()

    // Mendapatkan semua build dari user berdasarkan UID
    database.child("users").child(userId).child("builds")
        .get()
        .addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Memetakan data dari Firebase ke daftar Build
                val buildList = dataSnapshot.children.mapNotNull { snapshot ->
                    val buildId = snapshot.key ?: return@mapNotNull null
                    val title = snapshot.child("title").value as? String ?: return@mapNotNull null

                    // Memetakan komponen ke BuildComponents
                    val componentsSnapshot = snapshot.child("components")
                    val components = BuildComponents(
                        casing = componentsSnapshot.child("case").getValue(Casing::class.java),
                        processor = componentsSnapshot.child("cpu").getValue(Processor::class.java),
                        motherboard = componentsSnapshot.child("motherboard").getValue(Motherboard::class.java),
                        videoCard = componentsSnapshot.child("gpu").getValue(VideoCard::class.java),
                        headphone = componentsSnapshot.child("headphone").getValue(Headphones::class.java),
                        internalHardDrive = componentsSnapshot.child("internalharddrive").getValue(InternalHardDrive::class.java),
                        keyboard = componentsSnapshot.child("keyboard").getValue(Keyboard::class.java),
                        powerSupply =componentsSnapshot.child("powersupply").getValue(PowerSupply::class.java),
                        mouse = componentsSnapshot.child("mouse").getValue(Mouse::class.java),
                    )

                    // Buat objek Build
                    Build(
                        buildId = buildId,
                        title = title,
                        components = components
                    )
                }

                onSuccess(buildList) // Kembalikan daftar build
            } else {
                onSuccess(emptyList()) // Tidak ada build
            }
        }
        .addOnFailureListener { error ->
            onFailure("Failed to fetch builds: ${error.message}")
        }
}

fun removeBuildComponent(
    userId: String,
    buildId: String,
    componentCategory: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val path = "users/$userId/builds/$buildId/components/$componentCategory"
    Log.d("RemoveComponent", "Removing from path: $path")

    val databaseRef = getDatabaseReference()
    val componentRef = databaseRef.child(path)

    componentRef.removeValue()
        .addOnSuccessListener {
            Log.d("RemoveComponent", "Successfully removed: $path")
            onSuccess()
        }
        .addOnFailureListener { exception ->
            Log.e("RemoveComponent", "Failed to remove: $path, Error: ${exception.message}")
            onFailure(exception.message ?: "Failed to remove component")
        }
}











