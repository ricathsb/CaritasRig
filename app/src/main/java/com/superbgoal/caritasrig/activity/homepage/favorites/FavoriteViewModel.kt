package com.superbgoal.caritasrig.activity.homepage.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.GenericTypeIndicator
import com.superbgoal.caritasrig.data.getDatabaseReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FavoriteViewModel : ViewModel() {
    private val _processors = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val processors: StateFlow<List<Map<String, Any>>> = _processors

    private val _videoCards = MutableStateFlow<List<Map<String, Any>>>(emptyList())
    val videoCards: StateFlow<List<Map<String, Any>>> = _videoCards

    fun fetchFavorites() {
        val database = getDatabaseReference()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val favoriteRef = database.child("users").child(userId).child("favorites")

            // Fetch Processors
            favoriteRef.child("processors").get().addOnSuccessListener { snapshot ->
                // Menyimpan data processors dalam Map, menggunakan ID sebagai key
                val fetchedProcessors = snapshot.children.mapNotNull { processorSnapshot ->
                    processorSnapshot.value as? Map<String, Any>? // Ambil data sebagai Map
                }

                // Debug log
                Log.d("fetchFavorites", "Fetched processors: $fetchedProcessors")
                _processors.value = fetchedProcessors  // Menyimpan data dalam _processors
            }.addOnFailureListener {
                Log.e("fetchFavorites", "Failed to fetch processors: ${it.message}")
            }

            // Fetch Video Cards
            favoriteRef.child("videoCards").get().addOnSuccessListener { snapshot ->
                // Menyimpan data video cards dalam Map, menggunakan ID sebagai key
                val fetchedVideoCards = snapshot.children.mapNotNull { videoCardSnapshot ->
                    videoCardSnapshot.value as? Map<String, Any>? // Ambil data sebagai Map
                }

                // Debug log
                Log.d("fetchFavorites", "Fetched video cards: $fetchedVideoCards")
                _videoCards.value = fetchedVideoCards  // Menyimpan data dalam _videoCards
            }.addOnFailureListener {
                Log.e("fetchFavorites", "Failed to fetch video cards: ${it.message}")
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
}

