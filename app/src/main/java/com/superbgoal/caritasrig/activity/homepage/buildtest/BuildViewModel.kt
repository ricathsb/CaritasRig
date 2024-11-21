package com.superbgoal.caritasrig.activity.homepage.buildtest

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.superbgoal.caritasrig.data.fetchBuildsWithAuth
import com.superbgoal.caritasrig.data.getDatabaseReference
import com.superbgoal.caritasrig.data.model.buildmanager.Build
import com.superbgoal.caritasrig.data.model.buildmanager.BuildManager
import com.superbgoal.caritasrig.data.model.component.Casing
import com.superbgoal.caritasrig.data.model.component.CpuCooler
import com.superbgoal.caritasrig.data.model.component.Headphones
import com.superbgoal.caritasrig.data.model.component.InternalHardDrive
import com.superbgoal.caritasrig.data.model.component.Keyboard
import com.superbgoal.caritasrig.data.model.component.Memory
import com.superbgoal.caritasrig.data.model.component.Motherboard
import com.superbgoal.caritasrig.data.model.component.Mouse
import com.superbgoal.caritasrig.data.model.component.PowerSupply
import com.superbgoal.caritasrig.data.model.component.Processor
import com.superbgoal.caritasrig.data.model.component.VideoCard
import com.superbgoal.caritasrig.data.removeBuildComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BuildViewModel(application: Application) : AndroidViewModel(application) {


    private val _selectedComponents = MutableLiveData<Map<String, String>>()
    val selectedComponents: LiveData<Map<String, String>> = _selectedComponents

    private val sharedPreferences = application.getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)

    private val _isNewBuild = MutableStateFlow(false)
    val isNewBuild: StateFlow<Boolean> get() = _isNewBuild

    private val _buildTitle = MutableLiveData<String>()
    val buildTitle: LiveData<String> get() = _buildTitle

    private val _buildData = MutableLiveData<Build?>()
    val buildData: LiveData<Build?> get() = _buildData

    private val _componentDetail = MutableLiveData<String?>()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

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


    fun setNewBuildState(isNew: Boolean) {
        _isNewBuild.value = isNew
    }

    fun resetBuildTitle() {
        _buildTitle.value = ""
        println("Build title has been reset.")
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
                "CPU" to it.processor?.let { "Processor: ${it.name}, Cores: ${it.core_count}, ${it.core_clock} GHz" },
                "Case" to it.casing?.let { "Case: ${it.name}, Type: ${it.type}" },
                "GPU" to it.videoCard?.let { "GPU: ${it.name}, Memory: ${it.memory} GB" },
                "Motherboard" to it.motherboard?.let { "Motherboard: ${it.name}, Chipset: ${it.formFactor}" },
                "RAM" to it.memory?.let { "RAM: ${it.name}, Size: ${it.pricePerGb} GB, Speed: ${it.speed} MHz" },
                "InternalHardDrive" to it.internalHardDrive?.let { "Storage: ${it.name}, Capacity: ${it.capacity} GB" },
                "PowerSupply" to it.powerSupply?.let { "PSU: ${it.name}, Wattage: ${it.wattage} W" },
                "CPU Cooler" to it.cpuCooler?.let { "CPU Cooler: ${it.name}, Fan Speed: ${it.rpm} RPM" },
                "Headphone" to it.headphone?.let { "Headphone: ${it.name}, Type: ${it.type}" },
                "Keyboard" to it.keyboard?.let { "Keyboard: ${it.name}, Type: ${it.switches}" },
                "Mouse" to it.mouse?.let { "Mouse: ${it.name}, Max DPI: ${it.maxDpi}" }
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
                    append("CPU: ${it.name}, ${it.core_count} cores, ${it.core_clock} GHz\n")
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
                    append("CPU Cooler: ${it.name}, Fan Speed: ${it.rpm} RPM\n")
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
                this[category] = "No $category Selected" // Tampilkan kondisi awal
            } ?: mapOf(category to "No $category Selected")

            _selectedComponents.value = updatedComponents // Perbarui selectedComponents

            // Hapus data di server
            removeBuildComponent(
                userId = Firebase.auth.currentUser?.uid ?: "",
                buildId = currentBuildData.buildId,
                componentCategory = category.lowercase(),
                onSuccess = {
                    Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "$category removed successfully from server.")
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

        val userId = Firebase.auth.currentUser?.uid
        val currentBuildData = _buildData.value

        if (userId == null || currentBuildData == null) {
            Log.e("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "User not authenticated or build data is null.")
            _loading.value = false
            return
        }

        val buildId = currentBuildData.buildId
        val componentPath = "users/$userId/builds/$buildId/components/${category.lowercase()}"

        getDatabaseReference().child(componentPath).updateChildren(updatedData)
            .addOnSuccessListener {
                Log.d("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Successfully updated $category in Firebase.")

                // Update data lokal setelah sukses
                val components = currentBuildData.components ?: return@addOnSuccessListener
                val updatedComponents = when (category) {
                    "CPU" -> components.copy(processor = updatedData["processor"] as? Processor ?: components.processor)
                    "Case" -> components.copy(casing = updatedData["casing"] as? Casing ?: components.casing)
                    "GPU" -> components.copy(videoCard = updatedData["videoCard"] as? VideoCard ?: components.videoCard)
                    "Motherboard" -> components.copy(motherboard = updatedData["motherboard"] as? Motherboard ?: components.motherboard)
                    "RAM" -> components.copy(memory = updatedData["memory"] as? Memory ?: components.memory)
                    "InternalHardDrive" -> components.copy(internalHardDrive = updatedData["internalHardDrive"] as? InternalHardDrive ?: components.internalHardDrive)
                    "PowerSupply" -> components.copy(powerSupply = updatedData["powerSupply"] as? PowerSupply ?: components.powerSupply)
                    "CPU Cooler" -> components.copy(cpuCooler = updatedData["cpuCooler"] as? CpuCooler ?: components.cpuCooler)
                    "Headphone" -> components.copy(headphone = updatedData["headphone"] as? Headphones ?: components.headphone)
                    "Keyboard" -> components.copy(keyboard = updatedData["keyboard"] as? Keyboard ?: components.keyboard)
                    "Mouse" -> components.copy(mouse = updatedData["mouse"] as? Mouse ?: components.mouse)
                    else -> components
                }.copy() // Buat salinan baru untuk memastikan perubahan referensi

                val updatedBuildData = currentBuildData.copy(components = updatedComponents)

                // Paksa perubahan agar terdeteksi Jetpack Compose
                _buildData.value = null
                _buildData.value = updatedBuildData

                // Update selectedComponents untuk UI
                val updatedSelectedComponents = _selectedComponents.value?.toMutableMap()?.apply {
                    this[category] = "Updated $category Successfully"
                } ?: mapOf(category to "Updated $category Successfully")
                _selectedComponents.value = updatedSelectedComponents

                _loading.value = false // Selesai loading
            }
            .addOnFailureListener { error ->
                Log.e("com.superbgoal.caritasrig.activity.homepage.buildtest.BuildViewModel", "Failed to update $category in Firebase: ${error.message}")
                _selectedComponents.value = _selectedComponents.value?.toMutableMap()?.apply {
                    this[category] = "Failed to update $category"
                }
                _loading.value = false // Selesai loading, meskipun gagal
            }
    }
}
