package com.superbgoal.caritasrig.activity.auth.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.superbgoal.caritasrig.activity.auth.login.LoginActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.format.DateTimeFormatter

class RegisterViewModel : ViewModel() {

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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Update functions for each state value
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

    fun signOut(context: Context) {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
        (context as Activity).finish()
    }

}
