package com.superbgoal.caritasrig.ComposableScreen.homepage.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.functions.getDatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoriteViewModel : ViewModel() {
    // Processors
    private val _processors = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val processors: StateFlow<List<Map<String, Any>>> = _processors

    // Video Cards
    private val _videoCards = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val videoCards: StateFlow<List<Map<String, Any>>> = _videoCards

    // Motherboards
    private val _motherboards = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val motherboards: StateFlow<List<Map<String, Any>>> = _motherboards

    // Memory
    private val _memory = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val memory: StateFlow<List<Map<String, Any>>> = _memory

    // Power Supplies
    private val _powerSupplies = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val powerSupplies: StateFlow<List<Map<String, Any>>> = _powerSupplies

    // CPU Coolers
    private val _cpuCoolers = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val cpuCoolers: StateFlow<List<Map<String, Any>>> = _cpuCoolers

    // Casings
    private val _casings = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val casings: StateFlow<List<Map<String, Any>>> = _casings

    // Headphones
    private val _headphones = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val headphones: StateFlow<List<Map<String, Any>>> = _headphones

    // Internal Hard Drives
    private val _internalHardDrives = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val internalHardDrives: StateFlow<List<Map<String, Any>>> = _internalHardDrives

    // Keyboards
    private val _keyboards = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val keyboards: StateFlow<List<Map<String, Any>>> = _keyboards

    // Mice
    private val _mice = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val mice: StateFlow<List<Map<String, Any>>> = _mice


    fun fetchFavorites() {
        val database = getDatabaseReference()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val favoriteRef = database.child("users").child(userId).child("favorites")

            // Daftar komponen yang akan diambil
            val components = listOf(
                "processors",
                "videoCards",
                "motherboards",
                "memory",
                "powerSupplies",
                "cpuCoolers",
                "casings",
                "headphones",
                "internalHardDrives",
                "keyboards",
                "mice"
            )

            components.forEach { component ->
                favoriteRef.child(component).get().addOnSuccessListener { snapshot ->
                    // Ambil data setiap komponen sebagai Map
                    val fetchedData = snapshot.children.mapNotNull { dataSnapshot ->
                        dataSnapshot.value as? Map<String, Any>
                    }

                    // Debug log
                    Log.d("fetchFavorites", "Fetched $component: $fetchedData")

                    // Simpan data ke LiveData atau variabel state sesuai nama komponen
                    when (component) {
                        "processors" -> _processors.value = fetchedData
                        "videoCards" -> _videoCards.value = fetchedData
                        "motherboards" -> _motherboards.value = fetchedData
                        "memory" -> _memory.value = fetchedData
                        "powerSupplies" -> _powerSupplies.value = fetchedData
                        "cpuCoolers" -> _cpuCoolers.value = fetchedData
                        "casings" -> _casings.value = fetchedData
                        "headphones" -> _headphones.value = fetchedData
                        "internalHardDrives" -> _internalHardDrives.value = fetchedData
                        "keyboards" -> _keyboards.value = fetchedData
                        "mice" -> _mice.value = fetchedData
                    }
                }.addOnFailureListener {
                    Log.e("fetchFavorites", "Failed to fetch $component: ${it.message}")
                }
            }
        }
    }



    fun deleteProcessor(processorName: String) {
        val database = getDatabaseReference()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("FavoriteViewModel", "Deleting processor with name: $processorName")

        if (userId != null) {
            val favoriteRef = database.child("users").child(userId).child("favorites").child("processors")

            // Log to ensure the reference is correct
            Log.d("FavoriteViewModel", "Reference: ${favoriteRef.toString()}")

            // Menghapus processor berdasarkan name
            favoriteRef.orderByChild("name").equalTo(processorName).get().addOnSuccessListener { snapshot ->
                Log.d("FavoriteViewModel", "Snapshot children count: ${snapshot.childrenCount}")

                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        Log.d("FavoriteViewModel", "Deleting processor with name: ${it.child("name").value}")
                        it.ref.removeValue().addOnSuccessListener {
                            Log.d("FavoriteViewModel", "Processor deleted successfully")
                            // Update StateFlow setelah menghapus item dari Firebase
                            _processors.value = _processors.value.filterNot { processor ->
                                processor["name"] == processorName
                            }
                        }.addOnFailureListener { error ->
                            Log.e("FavoriteViewModel", "Failed to delete processor: ${error.message}")
                        }
                    }
                } else {
                    Log.d("FavoriteViewModel", "No matching processor found to delete.")
                }
            }.addOnFailureListener { error ->
                Log.e("FavoriteViewModel", "Failed to query processors: ${error.message}")
            }
        }
    }

    fun deleteVideoCard(videoCardName: String) {
        val database = getDatabaseReference()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("FavoriteViewModel", "Deleting video card with name: $videoCardName")

        if (userId != null) {
            val favoriteRef = database.child("users").child(userId).child("favorites").child("videoCards")

            // Log untuk memastikan referensi
            Log.d("FavoriteViewModel", "Reference: ${favoriteRef.toString()}")

            // Menghapus video card berdasarkan name
            favoriteRef.orderByChild("name").equalTo(videoCardName).get().addOnSuccessListener { snapshot ->
                Log.d("FavoriteViewModel", "Snapshot children count: ${snapshot.childrenCount}")

                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        Log.d("FavoriteViewModel", "Deleting video card with name: ${it.child("name").value}")
                        it.ref.removeValue().addOnSuccessListener {
                            Log.d("FavoriteViewModel", "Video card deleted successfully")
                            // Update StateFlow setelah menghapus item dari Firebase
                            _videoCards.value = _videoCards.value.filterNot { videoCard ->
                                videoCard["name"] == videoCardName
                            }
                        }.addOnFailureListener { error ->
                            Log.e("FavoriteViewModel", "Failed to delete video card: ${error.message}")
                        }
                    }
                } else {
                    Log.d("FavoriteViewModel", "No matching video card found to delete.")
                }
            }.addOnFailureListener { error ->
                Log.e("FavoriteViewModel", "Failed to query video cards: ${error.message}")
            }
        }
    }

    fun deleteComponent(componentType: String, componentName: String) {
        val database = getDatabaseReference()
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        Log.d("FavoriteViewModel", "Deleting $componentType with name: $componentName")

        if (userId != null) {
            val favoriteRef = database.child("users").child(userId).child("favorites").child(componentType)

            // Log untuk memastikan referensi benar
            Log.d("FavoriteViewModel", "Reference: ${favoriteRef.toString()}")

            // Menghapus komponen berdasarkan name
            favoriteRef.orderByChild("name").equalTo(componentName).get().addOnSuccessListener { snapshot ->
                Log.d("FavoriteViewModel", "Snapshot children count: ${snapshot.childrenCount}")

                if (snapshot.exists()) {
                    snapshot.children.forEach {
                        Log.d("FavoriteViewModel", "Deleting $componentType with name: ${it.child("name").value}")
                        it.ref.removeValue().addOnSuccessListener {
                            Log.d("FavoriteViewModel", "$componentType deleted successfully")

                            // Update StateFlow sesuai jenis komponen
                            when (componentType) {
                                "processors" -> {
                                    _processors.value = _processors.value.filterNot { processor ->
                                        processor["name"] == componentName
                                    }
                                }
                                "videoCards" -> {
                                    _videoCards.value = _videoCards.value.filterNot { videoCard ->
                                        videoCard["name"] == componentName
                                    }
                                }
                                "motherboards" -> {
                                    _motherboards.value = _motherboards.value.filterNot { motherboard ->
                                        motherboard["name"] == componentName
                                    }
                                }
                                "memory" -> {
                                    _memory.value = _memory.value.filterNot { memory ->
                                        memory["name"] == componentName
                                    }
                                }
                                "powerSupplies" -> {
                                    _powerSupplies.value = _powerSupplies.value.filterNot { powerSupply ->
                                        powerSupply["name"] == componentName
                                    }
                                }
                                "cpuCoolers" -> {
                                    _cpuCoolers.value = _cpuCoolers.value.filterNot { cpuCooler ->
                                        cpuCooler["name"] == componentName
                                    }
                                }
                                "casings" -> {
                                    _casings.value = _casings.value.filterNot { casing ->
                                        casing["name"] == componentName
                                    }
                                }
                                "headphones" -> {
                                    _headphones.value = _headphones.value.filterNot { headphone ->
                                        headphone["name"] == componentName
                                    }
                                }
                                "internalHardDrives" -> {
                                    _internalHardDrives.value = _internalHardDrives.value.filterNot { hardDrive ->
                                        hardDrive["name"] == componentName
                                    }
                                }
                                "keyboards" -> {
                                    _keyboards.value = _keyboards.value.filterNot { keyboard ->
                                        keyboard["name"] == componentName
                                    }
                                }
                                "mice" -> {
                                    _mice.value = _mice.value.filterNot { mouse ->
                                        mouse["name"] == componentName
                                    }
                                }
                                else -> Log.w("FavoriteViewModel", "Unknown component type: $componentType")
                            }
                        }.addOnFailureListener { error ->
                            Log.e("FavoriteViewModel", "Failed to delete $componentType: ${error.message}")
                        }
                    }
                } else {
                    Log.d("FavoriteViewModel", "No matching $componentType found to delete.")
                }
            }.addOnFailureListener { error ->
                Log.e("FavoriteViewModel", "Failed to query $componentType: ${error.message}")
            }
        }
    }


}

