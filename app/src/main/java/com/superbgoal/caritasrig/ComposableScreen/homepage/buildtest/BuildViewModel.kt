package com.superbgoal.caritasrig.ComposableScreen.homepage.buildtest

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.superbgoal.caritasrig.functions.fetchBuildsWithAuth
import com.superbgoal.caritasrig.functions.getDatabaseReference
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
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
import com.superbgoal.caritasrig.functions.removeBuildComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BuildViewModel(application: Application) : AndroidViewModel(application) {


    private val _selectedComponents = MutableLiveData<Map<String, String>>()
    val selectedComponents: LiveData<Map<String, String>> = _selectedComponents

    private val sharedPreferences = application.getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)

    private val _isNewBuild = MutableStateFlow(false)
    val isNewBuild: StateFlow<Boolean> get() = _isNewBuild

    private val _showNewDialog = MutableStateFlow(false)
    val showNewDialog: StateFlow<Boolean> get() = _showNewDialog
    private val _showShareDialog = MutableStateFlow(false)
    val showShareDialog: StateFlow<Boolean> get() = _showShareDialog

    private val _buildTitle = MutableLiveData<String>()
    val buildTitle: LiveData<String> get() = _buildTitle

    private val _buildData = MutableLiveData<Build?>()
    val buildData: LiveData<Build?> get() = _buildData

    private val _componentDetail = MutableLiveData<String?>()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _totalBuildPrice = MutableLiveData(0.0)
    val totalBuildPrice: LiveData<Double> get() = _totalBuildPrice

    private val _totalWattage = MutableLiveData(0.0)
    val totalWattage: LiveData<Double> get() = _totalWattage

    private val defaultCategories = mapOf(
        "CPU" to "No CPU Selected",
        "Case" to "No Case Selected",
        "GPU" to "No GPU Selected",
        "Motherboard" to "No Motherboard Selected",
        "RAM" to "No RAM Selected",
        "InternalHardDrive" to "No Storage Selected",
        "PowerSupply" to "No PSU Selected",
        "CPU Cooler" to "No CPU Cooler Selected",
        "Headphone" to "No Headphone Selected",
        "Keyboard" to "No Keyboard Selected",
        "Mouse" to "No Mouse Selected"
    )
    fun setNewDialogState(show: Boolean) {
        _showNewDialog.value = show
    }
    fun setShareDialogState(show: Boolean) {
        _showShareDialog.value = show
    }

    fun setNewBuildState(isNew: Boolean) {
        _isNewBuild.value = isNew
        Log.d("BuildViewModell", "New Build State Set: $isNew")
        Log.d("BuildViewModell", "Build Title: ${_isNewBuild.value}")
        Log.d("BuildViewModell", "Build Data: ${isNewBuild.value}")
    }



    fun resetBuildTitle() {
        _buildTitle.value = ""
        println("Build title has been reset.")
    }

    fun setBuildPrice(price: Double) {
        _totalBuildPrice.value = price
    }

    fun setBuildWattage(wattage: Double) {
        _totalWattage.value = wattage
    }

    fun resetBuildData() {

        // Reset components to default values
        _selectedComponents.value = defaultCategories

        // Clear build data
        _buildData.value = null

        // Optionally, clear component details
        _componentDetail.value = null

        Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Build title and components have been reset.")
    }


    fun saveBuildTitle(title: String) {
        _buildTitle.value = title
        sharedPreferences.edit().putString("buildTitle", title).apply()
        BuildManager.setBuildTitle(title)
        Log.d("viewmodel", "Build Title Saved and Set: $title")
    }

    // Memastikan `LiveData` diperbarui saat data build atau komponen diubah.
    fun setBuildData(build: Build?) {
        if (build == null) {
            // Tangani jika build null (gunakan data kosong atau default)
            _buildData.value = null
            _selectedComponents.value = defaultCategories // Tampilkan kategori kosong
            Log.w("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Build is null, setting default components.")
            return
        }

        // Jika build tidak null, perbarui LiveData
        _buildData.value = build

        // Simpan judul build
        saveBuildTitle(build.title)

        // Perbarui detail komponen
        updateComponentDetail(build)

        // Perbarui komponen di UI
        val components = build.components?.let {
            mapOf(
                "CPU" to it.processor?.let { "Processor: ${it.name}, Cores: ${it.coreCount}, ${it.performanceCoreClock} GHz" },
                "Case" to it.casing?.let { "Case: ${it.name}, Type: ${it.type}" },
                "GPU" to it.videoCard?.let { "GPU: ${it.name}, Memory: ${it.memory} GB" },
                "Motherboard" to it.motherboard?.let { "Motherboard: ${it.name}, Chipset: ${it.formFactor}" },
                "RAM" to it.memory?.let { "RAM: ${it.name}, Size: ${it.pricePerGb} GB, Speed: ${it.speed} MHz" },
                "InternalHardDrive" to it.internalHardDrive?.let { "Storage: ${it.name}, Capacity: ${it.capacity} GB" },
                "PowerSupply" to it.powerSupply?.let { "PSU: ${it.name}, Wattage: ${it.wattage} W" },
                "CPU Cooler" to it.cpuCooler?.let { "CPU Cooler: ${it.name}, Fan Speed: ${it.fanRpm} RPM" },
                "Headphone" to it.headphone?.let { "Headphone: ${it.name}, Type: ${it.type}" },
                "Keyboard" to it.keyboard?.let { "Keyboard: ${it.name}, Type: ${it.switches}" },
                "Mouse" to it.mouse?.let { "Mouse: ${it.name}, Max DPI: ${it.maxDpi}" },


                )
        } ?: emptyMap()

        // Gabungkan dengan kategori default
        _selectedComponents.value = defaultCategories.mapValues { entry ->
            components[entry.key] ?: entry.value
        }

        Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Build data and components updated.")
    }

    private fun updateComponentDetail(build: Build) {
        build.components?.let { components ->
            val detail = buildString {
                components.processor?.let {
                    append("CPU: ${it.name}, ${it.coreCount} cores, ${it.performanceCoreClock} GHz\n")
                }
                components.casing?.let {
                    append("Case: ${it.name}, Type: ${it.type}\n")
                }
                components.videoCard?.let {
                    append("GPU: ${it.name}, ${it.memory} GB\n")
                }
                components.motherboard?.let {
                    append("Motherboard: ${it.name}, Chipset: ${it.formFactor}\n")
                }
                components.memory?.let {
                    append("RAM: ${it.name}, ${it.speed} GB, ${it.speed} MHz\n")
                }
                components.internalHardDrive?.let {
                    append("Storage: ${it.name}, Capacity: ${it.capacity} GB\n")
                }
                components.powerSupply?.let {
                    append("powerSupply: ${it.name}, ${it.wattage} W\n")
                }
                components.cpuCooler?.let {
                    append("CPU Cooler: ${it.name}, Fan Speed: ${it.fanRpm} RPM\n")
                }
                components.headphone?.let {
                    append("Headphone: ${it.name}, Type: ${it.type}\n")
                }
                components.keyboard?.let {
                    append("Keyboard: ${it.name}, Type: ${it.switches}\n")
                }
                components.mouse?.let {
                    append("Mouse: ${it.name}, Max DPI: ${it.maxDpi}\n")
                }
            }
            _componentDetail.value = detail
            Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Component Detail Updated: $detail")
        } ?: run {
            _componentDetail.value = null
            Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Components are null or empty.")
        }
    }


    fun fetchBuildByTitle(title: String) {
        _loading.value = true // Start loading

        fetchBuildsWithAuth(
            onSuccess = { builds ->
                val build = builds.find { it.title == title }
                if (build != null) {
                    setBuildData(build)
                    Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Build Data Fetched by Title: $build")
                } else {
                    // Jika tidak ada build, tetap tampilkan kategori default
                    _selectedComponents.value = defaultCategories
                    Log.w("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "No Build Found with Title: $title")
                }
                _loading.value = false // Stop loading
            },
            onFailure = { errorMessage ->
                Log.e("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Error fetching build by title: $errorMessage")
                _loading.value = false // Stop loading

                _selectedComponents.value = defaultCategories
            }
        )
    }

    fun clearSharedPreferences() {
        val isCleared = sharedPreferences.edit().clear().commit()
        if (isCleared) {
            println("SharedPreferences cleared successfully.")
        } else {
            println("Failed to clear SharedPreferences.")
        }
    }




    fun removeComponent(category: String) {
        val currentBuildData = _buildData.value
        if (currentBuildData != null) {
            // Salin buildData dan hapus komponen sesuai kategori
            val updatedBuildData = currentBuildData.copy(
                components = currentBuildData.components?.copy(
                    processor = if (category == "CPU") null else currentBuildData.components.processor,
                    casing = if (category == "Case") null else currentBuildData.components.casing,
                    videoCard = if (category == "GPU") null else currentBuildData.components.videoCard,
                    motherboard = if (category == "Motherboard") null else currentBuildData.components.motherboard,
                    memory = if (category == "RAM") null else currentBuildData.components.memory,
                    internalHardDrive = if (category == "InternalHardDrive") null else currentBuildData.components.internalHardDrive,
                    powerSupply = if (category == "PowerSupply") null else currentBuildData.components.powerSupply,
                    cpuCooler = if (category == "CPU Cooler") null else currentBuildData.components.cpuCooler,
                    headphone = if (category == "Headphone") null else currentBuildData.components.headphone,
                    keyboard = if (category == "Keyboard") null else currentBuildData.components.keyboard,
                    mouse = if (category == "Mouse") null else currentBuildData.components.mouse
                )
            )
            _buildData.value = updatedBuildData // Perbarui buildData

            // Update selectedComponents dengan nilai default untuk kategori yang dihapus
            val updatedComponents = _selectedComponents.value?.toMutableMap()?.apply {
                this[category] = "No $category Selected"
            } ?: mapOf(category to "No $category Selected")

            _selectedComponents.value = updatedComponents

            val targetPath = when (category) {
                "CPU" -> "processor"
                "Case" -> "casing"
                "GPU" -> "videoCard"
                "Motherboard" -> "motherboard"
                "RAM" -> "memory"
                "InternalHardDrive" -> "internalHardDrive"
                "PowerSupply" -> "powerSupply"
                "CPU Cooler" -> "cpuCooler"
                "Headphone" -> "headphone"
                "Keyboard" -> "keyboard"
                "Mouse" -> "mouse"
                else -> category.lowercase()
            }

            // Hapus data di server
            removeBuildComponent(
                userId = Firebase.auth.currentUser?.uid ?: "",
                buildId = currentBuildData.buildId,
                componentCategory = targetPath,
                onSuccess = {
                    Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "$targetPath removed successfully from server.")
                },
                onFailure = { errorMessage ->
                    // Jika gagal, restore nilai sebelumnya
                    _selectedComponents.value = _selectedComponents.value?.toMutableMap()?.apply {
                        this[category] = "Failed to remove, restored"
                    }
                    Log.e("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Failed to remove $category: $errorMessage")
                }
            )
        } else {
            Log.w("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Current build data is null, cannot remove component.")
        }
    }

    fun updateBuildComponentWithViewModel(
        category: String,
        updatedData: Map<String, Any>
    ) {
        _loading.value = true

        val targetPath = when (category) {
            "CPU" -> "processor"
            "Case" -> "casing"
            "GPU" -> "videoCard"
            "Motherboard" -> "motherboard"
            "RAM" -> "memory"
            "InternalHardDrive" -> "internalHardDrive"
            "PowerSupply" -> "powerSupply"
            "CPU Cooler" -> "cpuCooler"
            "Headphone" -> "headphone"
            "Keyboard" -> "keyboard"
            "Mouse" -> "mouse"
            else -> category.lowercase()
        }

        val userId = Firebase.auth.currentUser?.uid
        val currentBuildData = _buildData.value

        if (userId == null || currentBuildData == null) {
            Log.e("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "User not authenticated or build data is null.")
            _loading.value = false
            return
        }

        val buildId = currentBuildData.buildId
        val componentPath = "users/$userId/builds/$buildId/components/$targetPath"

        Log.d("BuildViewModel1", "Updating $category in Firebase with data: $updatedData")

        getDatabaseReference().child(componentPath).updateChildren(updatedData)
            .addOnSuccessListener {
                Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Successfully updated $category in Firebase.")

                // Update data lokal setelah sukses
                val components = currentBuildData.components ?: return@addOnSuccessListener
                val updatedComponents = when (category) {
                    "CPU" -> components.copy(processor = updatedData["processor"] as? ProcessorTrial ?: components.processor)
                    "Case" -> components.copy(casing = updatedData["casing"] as? CasingBuild ?: components.casing)
                    "GPU" -> components.copy(videoCard = updatedData["videoCard"] as? GpuBuild ?: components.videoCard)
                    "Motherboard" -> components.copy(motherboard = updatedData["motherboard"] as? MotherboardBuild ?: components.motherboard)
                    "RAM" -> components.copy(memory = updatedData["memory"] as? MemoryBuild ?: components.memory)
                    "InternalHardDrive" -> components.copy(internalHardDrive = updatedData["internalHardDrive"] as? InternalHardDriveBuild ?: components.internalHardDrive)
                    "PowerSupply" -> components.copy(powerSupply = updatedData["powerSupply"] as? PowerSupplyBuild ?: components.powerSupply)
                    "CPU Cooler" -> components.copy(cpuCooler = updatedData["cpuCooler"] as? CpuCoolerBuild ?: components.cpuCooler)
                    "Headphone" -> components.copy(headphone = updatedData["headphone"] as? Headphones ?: components.headphone)
                    "Keyboard" -> components.copy(keyboard = updatedData["keyboard"] as? Keyboard ?: components.keyboard)
                    "Mouse" -> components.copy(mouse = updatedData["mouse"] as? Mouse ?: components.mouse)
                    else -> components
                }.copy()

                val updatedBuildData = currentBuildData.copy(components = updatedComponents)

                _buildData.value = null
                _buildData.value = updatedBuildData

                val updatedSelectedComponents = _selectedComponents.value?.toMutableMap()?.apply {
                    this[category] = "Updated $category Successfully"
                } ?: mapOf(category to "Updated $category Successfully")
                _selectedComponents.value = updatedSelectedComponents

                _loading.value = false
            }
            .addOnFailureListener { error ->
                Log.e("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Failed to update $category in Firebase: ${error.message}")
                _selectedComponents.value = _selectedComponents.value?.toMutableMap()?.apply {
                    this[category] = "Failed to update $category"
                }
                _loading.value = false
            }
    }

    fun shareBuildWithOthers(imageUris: List<Uri>) {
        val currentBuildData = _buildData.value  // Ambil data build saat ini

        if (currentBuildData == null) {
            Log.e("BuildViewModel", "No build data to share.")
            return
        }

        // Ambil User ID saat ini dari Firebase Authentication
        val userId = Firebase.auth.currentUser?.uid ?: return

        // Lokasi tujuan di Firebase Realtime Database
        val sharedBuildRef = getDatabaseReference().child("shared_build/$userId/${currentBuildData.buildId}")

        // Jika ada gambar yang dipilih, upload ke Firebase Storage
        if (imageUris.isNotEmpty()) {
            uploadImagesToFirebaseStorage(imageUris) { imageUrls ->
                if (imageUrls.isNotEmpty()) {
                    // Simpan URL gambar yang sudah di-upload ke dalam database
                    val updatedBuildData = currentBuildData.copy(imageuris = imageUrls)

                    // Simpan data build ke node baru di Firebase Realtime Database
                    sharedBuildRef.setValue(updatedBuildData)
                        .addOnSuccessListener {
                            Log.d("BuildViewModel", "Build data shared successfully.")
                        }
                        .addOnFailureListener { error ->
                            Log.e("BuildViewModel", "Failed to share build: ${error.message}")
                        }
                } else {
                    Log.e("BuildViewModel", "No images uploaded, build data not shared.")
                }
            }
        } else {
            // Simpan data build tanpa gambar
            sharedBuildRef.setValue(currentBuildData)
                .addOnSuccessListener {
                    Log.d("BuildViewModel", "Build data shared successfully without images.")
                }
                .addOnFailureListener { error ->
                    Log.e("BuildViewModel", "Failed to share build: ${error.message}")
                }
        }
    }

    fun uploadImagesToFirebaseStorage(imageUris: List<Uri>, onComplete: (List<String>) -> Unit) {
        val uploadedImageUrls = mutableListOf<String>()
        val storage = FirebaseStorage.getInstance()

        // Loop untuk upload setiap gambar
        val uploadTasks = imageUris.map { uri ->
            val imageRef = storage.reference.child("build_images/${System.currentTimeMillis()}_${uri.lastPathSegment}")

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        uploadedImageUrls.add(downloadUri.toString())
                        // Jika sudah semua gambar ter-upload, panggil callback
                        if (uploadedImageUrls.size == imageUris.size) {
                            onComplete(uploadedImageUrls)
                        }
                    }
                        .addOnFailureListener { exception ->
                            Log.e("UploadError", "Failed to get download URL: ${exception.message}")
                            if (uploadedImageUrls.size == imageUris.size) {
                                onComplete(uploadedImageUrls)
                            }
                        }
                }
                .addOnFailureListener { exception ->
                    Log.e("UploadError", "Failed to upload image: ${exception.message}")
                    if (uploadedImageUrls.size == imageUris.size) {
                        onComplete(uploadedImageUrls)
                    }
                }
        }

        // Menunggu semua task selesai
        uploadTasks.forEach { it.addOnFailureListener { exception -> Log.e("UploadError", exception.message.toString()) } }
    }


}