package com.superbgoal.caritasrig.functions

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.ProcessorTrial
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

fun saveBuildTitle(
    context : Context,
    userId: String,
    buildTitle: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val database = getDatabaseReference()

    // Referensi ke lokasi builds pengguna
    val buildsRef = database.child("users").child(userId).child("builds")

    // Cek apakah sudah ada build dengan title yang sama
    buildsRef.get().addOnSuccessListener { snapshot ->
        val isTitleExists = snapshot.children.any { it.child("title").value == buildTitle }

        if (isTitleExists) {
            // Jika title sudah ada, panggil onFailure dengan pesan error
            onFailure("A build with the title '$buildTitle' already exists.")
            Toast.makeText(context, "A build with the title '$buildTitle' already exists.", Toast.LENGTH_SHORT).show()
        } else {
            // Jika title belum ada, lanjutkan menyimpan
            val buildData = mapOf("title" to buildTitle)
            buildsRef.push() // Membuat key unik untuk setiap build
                .setValue(buildData)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener { error ->
                    onFailure("Failed to save build title: ${error.message}")
                }
        }
    }.addOnFailureListener { error ->
        onFailure("Failed to check existing builds: ${error.message}")
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
                        casing = componentsSnapshot.child("casing").getValue(CasingBuild::class.java),
                        processor = componentsSnapshot.child("processor").getValue(ProcessorTrial::class.java),
                        motherboard = componentsSnapshot.child("motherboard").getValue(MotherboardBuild::class.java),
                        videoCard = componentsSnapshot.child("videoCard").getValue(GpuBuild::class.java),
                        headphone = componentsSnapshot.child("headphone").getValue(Headphones::class.java),
                        internalHardDrive = componentsSnapshot.child("internalHardDrive").getValue(InternalHardDriveBuild::class.java),
                        keyboard = componentsSnapshot.child("keyboard").getValue(Keyboard::class.java),
                        powerSupply =componentsSnapshot.child("powerSupply").getValue(PowerSupplyBuild::class.java),
                        mouse = componentsSnapshot.child("mouse").getValue(Mouse::class.java),
                        cpuCooler = componentsSnapshot.child("cpuCooler").getValue(CpuCoolerBuild::class.java),
                        memory = componentsSnapshot.child("memory").getValue(MemoryBuild::class.java)
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

fun updateBuildComponent(
    userId: String,
    buildId: String,
    componentCategory: String,
    updatedData: Map<String, Any>,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val database = getDatabaseReference()

    // Path ke komponen spesifik dalam build
    val componentPath = "users/$userId/builds/$buildId/components/$componentCategory"

    // Perbarui data komponen di Firebase
    database.child(componentPath).updateChildren(updatedData)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { error ->
            onFailure("Failed to update component: ${error.message}")
        }
}

fun deleteBuild(userId: String, buildId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
    getDatabaseReference()
        .child("users")
        .child(userId)
        .child("builds")
        .child(buildId)
        .removeValue() // Langsung menghapus data berdasarkan buildId
        .addOnSuccessListener {
            onSuccess() // Callback sukses
        }
        .addOnFailureListener { error ->
            onFailure("Failed to delete build: ${error.message}") // Callback gagal dengan pesan error
        }
}

fun editBuildTitle(
    context: Context,
    userId: String,
    buildId: String,
    newTitle: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val database = getDatabaseReference()

    // Referensi ke lokasi builds pengguna
    val buildsRef = database.child("users").child(userId).child("builds")

    // Cek apakah sudah ada build dengan title yang sama (selain build yang sedang diedit)
    buildsRef.get().addOnSuccessListener { snapshot ->
        val isTitleExists = snapshot.children.any {
            val title = it.child("title").value
            val id = it.key
            title == newTitle && id != buildId // Pastikan bukan build yang sedang diedit
        }

        if (isTitleExists) {
            // Jika title sudah ada, panggil onFailure dengan pesan error
            onFailure("A build with the title '$newTitle' already exists.")
            Toast.makeText(context, "A build with the title '$newTitle' already exists.", Toast.LENGTH_SHORT).show()
        } else {
            // Jika title belum ada, lanjutkan proses edit
            val updates = mapOf("title" to newTitle)
            buildsRef.child(buildId).updateChildren(updates)
                .addOnSuccessListener {
                    onSuccess()
                    Toast.makeText(context, "Build updated successfully.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    onFailure("Failed to update build title: ${error.message}")
                }
        }
    }.addOnFailureListener { error ->
        onFailure("Failed to check existing builds: ${error.message}")
    }
}


fun editRamQuantity(
    buildTitle: String,
    quantity: Int,
    onSuccess: (() -> Unit)? = null,
    onFailure: ((String) -> Unit)? = null,
    onLoading: ((Boolean) -> Unit)? = null
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = getDatabaseReference()

    // Set loading state to true when the process starts
    onLoading?.invoke(true)

    // Cari buildId berdasarkan buildTitle
    if (userId != null) {
        database.child("users").child(userId).child("builds")
            .orderByChild("title").equalTo(buildTitle)
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val buildId = dataSnapshot.children.firstOrNull()?.key
                    if (buildId != null) {
                        // Ambil harga satuan RAM dan monitor perubahan
                        val memoryRef = database.child("users").child(userId).child("builds").child(buildId)
                            .child("components").child("memory")

                        memoryRef.child("price").addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val unitPrice = snapshot.getValue(Double::class.java) ?: 0.0
                                val updatedPrice = unitPrice * quantity

                                // Perbarui totalPrice ketika price berubah
                                memoryRef.child("totalPrice").setValue(updatedPrice)
                                    .addOnFailureListener { error ->
                                        onFailure?.invoke("Failed to update total price: ${error.message}")
                                    }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                onFailure?.invoke("Price listener cancelled: ${error.message}")
                            }
                        })

                        // Perbarui quantity dan harga
                        val updates = mapOf(
                            "quantity" to quantity
                        )
                        memoryRef.updateChildren(updates)
                            .addOnSuccessListener {
                                onLoading?.invoke(false) // Set loading state to false on success
                                onSuccess?.invoke() // Callback sukses
                            }
                            .addOnFailureListener { error ->
                                onLoading?.invoke(false) // Set loading state to false on failure
                                onFailure?.invoke("Failed to update RAM quantity: ${error.message}")
                            }
                    } else {
                        onLoading?.invoke(false) // Set loading state to false if buildId is not found
                        onFailure?.invoke("Build ID not found.")
                    }
                } else {
                    onLoading?.invoke(false) // Set loading state to false if build is not found
                    onFailure?.invoke("Build with title \"$buildTitle\" not found.")
                }
            }
            .addOnFailureListener { error ->
                onLoading?.invoke(false) // Set loading state to false if query fails
                onFailure?.invoke("Failed to find build: ${error.message}")
            }
    }
}

fun savedFavorite(
    processor: ProcessorTrial? = null,
    videoCard: GpuBuild? = null,
    casing: CasingBuild? = null,
    cpuCooler: CpuCoolerBuild? = null,
    headphones: Headphones? = null,
    internalHardDrive: InternalHardDriveBuild? = null,
    keyboard: Keyboard? = null,
    memory: MemoryBuild? = null,
    motherboard: MotherboardBuild? = null,
    mouse: Mouse? = null,
    powerSupply: PowerSupplyBuild? = null,
    context : Context? = null
) {
    val database = FirebaseDatabase.getInstance("https://caritas-rig-default-rtdb.asia-southeast1.firebasedatabase.app").reference
    val userId = FirebaseAuth.getInstance().currentUser?.uid


    if (userId != null) {
        val favoriteRef = database.child("users").child(userId).child("favorites")

        fun <T> saveComponent(
            component: T,
            type: String,
            idGenerator: () -> String,
            dataMapper: (T) -> Map<String, Any>
        ) {
            favoriteRef.child(type).get().addOnSuccessListener { snapshot ->
                val exists = snapshot.children.any { it.child("name").value == dataMapper(component)["name"] }
                if (exists) {
                    Log.d("saveFavorite", "$type is already in the favorite list.")
                    if (context != null){
                    Toast.makeText(context, "$type is already in the favorite list.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val id = idGenerator()
                    favoriteRef.child(type).child(id).setValue(dataMapper(component))
                        .addOnSuccessListener {
                            Log.d("saveFavorite", "$type added to favorites.")
                            if (context != null){
                            Toast.makeText(context, "$type added to favorites.", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener { error ->
                            Log.e("saveFavorite", "Failed to add $type: ${error.message}")
                            if (context != null){
                            Toast.makeText(context, "Failed to add $type: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener { error ->
                Log.e("saveFavorite", "Failed to check $type: ${error.message}")
            }.addOnCompleteListener {
            }
        }

        processor?.let {
            saveComponent(it, "processors", { UUID.randomUUID().toString() }) { proc ->
                mapOf(
                    "name" to proc.name,
                    "price" to proc.price,
                    "boost_clock" to proc.socket,
                    "core_clock" to proc.tdp,
                    "core_count" to proc.socket,
                    "graphics" to proc.partNumber,
                    "smt" to proc.smt,
                    "tdp" to proc.tdp
                )
            }
        }

        videoCard?.let {
            saveComponent(it, "videoCards", { UUID.randomUUID().toString() }) { vCard ->
                mapOf(
                    "name" to vCard.name,
                    "price" to vCard.price,
                    "boostClock" to vCard.boostClock,
                    "coreClock" to vCard.coreClock,
                    "memory" to vCard.memory,
                    "chipset" to vCard.chipset,
                    "color" to vCard.color,
                    "length" to vCard.length
                )
            }
        }

        casing?.let {
            saveComponent(it, "casings", { UUID.randomUUID().toString() }) { casing ->
                mapOf(
                    "name" to casing.name,
                    "price" to casing.price,
                    "sidePanel" to casing.sidePanel,
                    "externalVolume" to casing.dimensions,
                    "color" to casing.color
                )
            }
        }

        cpuCooler?.let {
            saveComponent(it, "cpuCoolers", { UUID.randomUUID().toString() }) { cooler ->
                mapOf(
                    "name" to cooler.name,
                    "price" to cooler.price,
                    "rpm" to cooler.fanRpm,
                    "noise_level" to cooler.noiseLevel,
                    "color" to cooler.color,
                    "size" to cooler.height
                )
            }
        }

        headphones?.let {
            saveComponent(it, "headphones", { UUID.randomUUID().toString() }) { hp ->
                mapOf(
                    "name" to hp.name,
                    "price" to hp.price,
                    "type" to hp.type,
                    "frequencyResponse" to hp.frequencyResponse,
                    "microphone" to hp.microphone,
                    "wireless" to hp.wireless,
                    "enclosureType" to hp.enclosureType,
                    "color" to hp.color
                )
            }
        }

        internalHardDrive?.let {
            saveComponent(it, "internalHardDrives", { UUID.randomUUID().toString() }) { hdd ->
                mapOf(
                    "name" to hdd.name,
                    "price" to hdd.price,
                    "capacity" to hdd.capacity,
                    "pricePerGb" to hdd.pricePerGB,
                    "type" to hdd.type,
                    "cache" to hdd.cache,
                    "formFactor" to hdd.formFactor,
                    "interfacee" to hdd.interfaceType
                )
            }
        }

        keyboard?.let {
            saveComponent(it, "keyboards", { UUID.randomUUID().toString() }) { kb ->
                mapOf<String, Any>(
                    "name" to kb.name,
                    "price" to kb.price,
                    "style" to kb.style,
                    "switches" to (kb.switches ?: ""), // Ensure null safety by providing a default empty string
                    "backlit" to (kb.backlit ?: ""),   // Ensure null safety by providing a default empty string
                    "tenkeyless" to kb.tenkeyless,
                    "connectionType" to kb.connectionType,
                    "color" to kb.color
                )
            }
        }


        memory?.let {
            saveComponent(it, "memory", { UUID.randomUUID().toString() }) { mem ->
                mapOf(
                    "name" to mem.name,
                    "price" to mem.price,
                    "speed" to mem.speed,
                    "modules" to mem.modules,
                    "pricePerGb" to mem.pricePerGb,
                    "color" to mem.color,
                    "firstWordLatency" to mem.firstWordLatency,
                    "casLatency" to mem.casLatency,
                    "socket" to mem.speed,
                )
            }
        }

        motherboard?.let {
            saveComponent(it, "motherboards", { UUID.randomUUID().toString() }) { mb ->
                mapOf(
                    "name" to mb.name,
                    "price" to mb.price,
                    "socket" to mb.socketCpu,
                    "formFactor" to mb.formFactor,
                    "maxMemory" to mb.memoryMax,
                    "memorySlots" to mb.memorySlots,
                    "color" to mb.color,
                    "imageUrl" to mb.imageUrl
                )
            }
        }

        mouse?.let {
            saveComponent(it, "mice", { UUID.randomUUID().toString() }) { ms ->
                mapOf(
                    "name" to ms.name,
                    "price" to ms.price,
                    "trackingMethod" to ms.trackingMethod,
                    "connectionType" to ms.connectionType,
                    "maxDpi" to ms.maxDpi,
                    "handOrientation" to ms.handOrientation,
                    "color" to ms.color
                )
            }
        }

        powerSupply?.let {
            saveComponent(it, "powerSupplies", { UUID.randomUUID().toString() }) { psu ->
                mapOf(
                    "name" to psu.name,
                    "price" to psu.price,
                    "type" to psu.type,
                    "efficiency" to psu.efficiencyRating,
                    "wattage" to psu.wattage,
                    "modular" to psu.modular,
                    "color" to psu.color
                )
            }
        }
    }
}

fun countUserFavorites(onComplete: (Map<String, Int>, Int) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        Log.e("countUserFavorites", "User ID is null, exiting function.")
        onComplete(emptyMap(), 0)
        return
    }

    val database = getDatabaseReference()
    val userFavoritesRef = database.child("users").child(userId).child("favorites")

    userFavoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) {
                Log.w("countUserFavorites", "No favorites found for user: $userId")
                onComplete(emptyMap(), 0)
                return
            }

            val categoryCounts = mutableMapOf<String, Int>()
            var totalFavorites = 0

            snapshot.children.forEach { category ->
                // Setiap kategori memiliki children berupa item favorit
                val categoryKey = category.key ?: "Unknown"
                val count = category.childrenCount.toInt()

                Log.d("countUserFavorites", "Category: $categoryKey, Count: $count")
                categoryCounts[categoryKey] = count
                totalFavorites += count
            }

            Log.d("countUserFavorites", "Final category counts: $categoryCounts")
            Log.d("countUserFavorites", "Total favorites: $totalFavorites")

            onComplete(categoryCounts, totalFavorites)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("countUserFavorites", "Error fetching data: ${error.message}")
            onComplete(emptyMap(), 0)
        }
    })
}

fun countUserBuilds(onComplete: (Int) -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    if (userId == null) {
        Log.e("countUserBuilds", "User ID is null, exiting function.")
        onComplete(0)
        return
    }

    val database = getDatabaseReference()
    val userBuildsRef = database.child("users").child(userId).child("builds")

    userBuildsRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (!snapshot.exists()) {
                Log.w("countUserBuilds", "No builds found for user: $userId")
                onComplete(0)
                return
            }

            val totalBuilds = snapshot.childrenCount.toInt()
            Log.d("countUserBuilds", "Total builds for user $userId: $totalBuilds")

            onComplete(totalBuilds)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("countUserBuilds", "Error fetching builds: ${error.message}")
            onComplete(0)
        }
    })
}


































