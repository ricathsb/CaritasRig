package com.superbgoal.caritasrig.activity.homepage.profileicon

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.data.loadUserData
import com.superbgoal.caritasrig.data.model.User
import com.superbgoal.caritasrig.data.updateUserProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileSettingsViewModel : ViewModel() {

    private val _firstname = MutableStateFlow("")
    val firstname: StateFlow<String> get() = _firstname

    private val _lastname = MutableStateFlow("")
    val lastname: StateFlow<String> get() = _lastname

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> get() = _username

    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth: StateFlow<String> get() = _dateOfBirth

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> get() = _imageUri

    private val _imageUrl = MutableStateFlow("")
    val imageUrl: StateFlow<String> get() = _imageUrl

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadProfileData()
    }

    private fun loadProfileData() {
        if (userId != null) {
            viewModelScope.launch {
                _isLoading.value = true
                loadUserData(
                    userId = userId,
                    onUserDataLoaded = { user ->
                        _firstname.value = user.firstName
                        _lastname.value = user.lastName
                        _username.value = user.username
                        _dateOfBirth.value = user.dateOfBirth
                        _imageUrl.value = user.profileImageUrl ?: ""
                        _imageUri.value = user.profileImageUrl?.let { Uri.parse(it) }
                        _isLoading.value = false
                    },
                    onFailure = { errorMessage ->
                        Log.d("ProfileSettingsViewModel", "Error loading data: $errorMessage")
                        _isLoading.value = false
                    }
                )
            }
        }
    }

    fun updateFirstname(newFirstname: String) {
        _firstname.value = newFirstname
    }

    fun updateLastname(newLastname: String) {
        _lastname.value = newLastname
    }

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun updateDateOfBirth(newDateOfBirth: String) {
        _dateOfBirth.value = newDateOfBirth
    }

    fun updateImageUri(newUri: Uri?) {
        _imageUri.value = newUri
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }


    fun updateProfile(context: Context) {
        if (userId != null) {
            val user = User(
                userId = userId,
                firstName = _firstname.value,
                lastName = _lastname.value,
                username = _username.value,
                dateOfBirth = _dateOfBirth.value,
                profileImageUrl = _imageUrl.value
            )

            _isLoading.value = true
            updateUserProfileData(
                user = user,
                imageUri = _imageUri.value,
                imageUrl = _imageUrl.value,
                context = context
            ) { success ->
                _isLoading.value = false
                if (!success) {
                    Log.d("ProfileSettingsViewModel", "Failed to update profile.")
                }
            }
        }
    }
}
