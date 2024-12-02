package com.superbgoal.caritasrig.ComposableScreen.homepage.sharedBuild

import android.util.Log
import androidx.lifecycle.ViewModel
import com.superbgoal.caritasrig.data.model.buildmanager.BuildComponents
import com.superbgoal.caritasrig.data.model.buildmanager.SharedBuild
import com.superbgoal.caritasrig.data.model.component.CasingBuild
import com.superbgoal.caritasrig.data.model.component.CpuCoolerBuild
import com.superbgoal.caritasrig.data.model.component.GpuBuild
import com.superbgoal.caritasrig.data.model.component.Headphones
import com.superbgoal.caritasrig.data.model.component.InternalHardDriveBuild
import com.superbgoal.caritasrig.data.model.component.Keyboard
import com.superbgoal.caritasrig.data.model.component.MemoryBuild
import com.superbgoal.caritasrig.data.model.component.MotherboardBuild
import com.superbgoal.caritasrig.data.model.component.Mouse
import com.superbgoal.caritasrig.data.model.component.PowerSupplyBuild
import com.superbgoal.caritasrig.data.model.component.ProcessorTrial
import com.superbgoal.caritasrig.functions.getDatabaseReference

class SharedBuildViewModel : ViewModel() {

    fun fetchSharedBuilds(
        onSuccess: (List<SharedBuild>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = getDatabaseReference()

        // Mengambil semua data dari shared_build node
        database.child("shared_build")
            .get()
            .addOnSuccessListener { dataSnapshot ->
                Log.d("FirebaseDebug", "Data Snapshot: ${dataSnapshot.value}")
                if (dataSnapshot.exists()) {
                    val sharedBuildList = mutableListOf<SharedBuild>()

                    // Iterasi melalui setiap user dalam shared_build
                    dataSnapshot.children.forEach { userSnapshot ->
                        Log.d(
                            "FirebaseDebug",
                            "User Snapshot Key: ${userSnapshot.key}, Value: ${userSnapshot.value}"
                        )

                        val userId = userSnapshot.key ?: return@forEach
                        Log.d("FirebaseDebug", "User ID: $userId")

                        // Iterasi melalui setiap build milik user
                        userSnapshot.children.forEach { buildSnapshot ->
                            Log.d(
                                "FirebaseDebug",
                                "Build Snapshot Key: ${buildSnapshot.key}, Value: ${buildSnapshot.value}"
                            )

                            val buildId = buildSnapshot.key ?: return@forEach
                            val title =
                                buildSnapshot.child("title").value as? String ?: return@forEach

                            // Ambil imageUris dari buildSnapshot
                            val imageUris = mutableListOf<String>()
                            val imageUrisSnapshot = buildSnapshot.child("imageuris")
                            if (imageUrisSnapshot.exists()) {
                                imageUrisSnapshot.children.forEach { imageUriSnapshot ->
                                    val imageUri = imageUriSnapshot.getValue(String::class.java)
                                    if (imageUri != null) {
                                        imageUris.add(imageUri)
                                    } else {
                                        Log.d(
                                            "FirebaseDebug",
                                            "Invalid or empty image URI: ${imageUriSnapshot.value}"
                                        )
                                    }
                                }
                            } else {
                                Log.d("FirebaseDebug", "No image URIs found in this build")
                            }

                            Log.d("FirebaseDebug", "ImageUris: $imageUris")

                            // Ambil komponen build
                            val componentsSnapshot = buildSnapshot.child("components")
                            val components = BuildComponents(
                                casing = componentsSnapshot.child("case")
                                    .getValue(CasingBuild::class.java),
                                processor = componentsSnapshot.child("processor")
                                    .getValue(ProcessorTrial::class.java),
                                motherboard = componentsSnapshot.child("motherboard")
                                    .getValue(MotherboardBuild::class.java),
                                videoCard = componentsSnapshot.child("gpu")
                                    .getValue(GpuBuild::class.java),
                                headphone = componentsSnapshot.child("headphone")
                                    .getValue(Headphones::class.java),
                                internalHardDrive = componentsSnapshot.child("internalharddrive")
                                    .getValue(InternalHardDriveBuild::class.java),
                                keyboard = componentsSnapshot.child("keyboard")
                                    .getValue(Keyboard::class.java),
                                powerSupply = componentsSnapshot.child("powersupply")
                                    .getValue(PowerSupplyBuild::class.java),
                                mouse = componentsSnapshot.child("mouse")
                                    .getValue(Mouse::class.java),
                                cpuCooler = componentsSnapshot.child("cpucooler")
                                    .getValue(CpuCoolerBuild::class.java),
                                memory = componentsSnapshot.child("memory")
                                    .getValue(MemoryBuild::class.java)
                            )

                            // Tambahkan ke daftar sharedBuildList
                            sharedBuildList.add(
                                SharedBuild(
                                    userId = userId,
                                    buildId = buildId,
                                    title = title,
                                    imageUrls = imageUris,
                                    components = components
                                )
                            )
                        }
                    }
                    onSuccess(sharedBuildList) // Kembalikan daftar shared builds
                } else {
                    onSuccess(emptyList()) // Tidak ada shared builds
                }
            }
            .addOnFailureListener { error ->
                // Log error lengkap untuk melihat penyebab masalah
                Log.e("Firebase", "Error fetching shared builds: ${error.message}", error)
                onFailure("Failed to fetch shared builds: ${error.message}")
            }
    }

    fun saveSharedBuildToUserBuilds(
        sharedUserId: String,  // User ID pemilik shared_build
        buildId: String,       // ID build yang ingin disalin
        targetUserId: String,  // User ID tujuan
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = getDatabaseReference()

        val sharedBuildRef = database.child("shared_build").child(sharedUserId).child(buildId)
        val userBuildsRef = database.child("users").child(targetUserId).child("builds").child(buildId)

        sharedBuildRef.get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    // Ambil data title dan components dari snapshot
                    val title = snapshot.child("title").value as? String
                    val components = snapshot.child("components").value

                    if (title != null && components != null) {
                        // Data yang akan ditulis ke database
                        val buildData = mapOf(
                            "title" to title,
                            "components" to components
                        )

                        // Tulis ke node builds pengguna
                        userBuildsRef.setValue(buildData)
                            .addOnSuccessListener {
                                Log.d(
                                    "Firebase",
                                    "Build $buildId successfully copied to user $targetUserId builds."
                                )
                                onSuccess()
                            }
                            .addOnFailureListener { error ->
                                Log.e("Firebase", "Failed to save build: ${error.message}", error)
                                onFailure("Failed to save build: ${error.message}")
                            }
                    } else {
                        Log.e("Firebase", "Title or components missing in shared build.")
                        onFailure("Title or components missing in shared build.")
                    }
                } else {
                    Log.e(
                        "Firebase",
                        "Shared build $buildId does not exist for user $sharedUserId."
                    )
                    onFailure("Build does not exist.")
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Failed to fetch shared build: ${error.message}", error)
                onFailure("Failed to fetch shared build: ${error.message}")
            }
    }
}