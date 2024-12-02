package com.superbgoal.caritasrig.ComposableScreen.auth.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    // State flows for UI states
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> get() = _password

    private val _isPasswordVisible = MutableStateFlow(false)
    val isPasswordVisible: StateFlow<Boolean> get() = _isPasswordVisible

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _offsetY = MutableStateFlow(800f)
    val offsetY: StateFlow<Float> get() = _offsetY

    private val _isArrowUp = MutableStateFlow(true)
    val isArrowUp: StateFlow<Boolean> get() = _isArrowUp

    fun updateEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _isPasswordVisible.value = !_isPasswordVisible.value
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun updateOffsetY(newOffset: Float) {
        _offsetY.value = newOffset.coerceIn(0f, 800f)
    }

    fun toggleArrowDirection() {
        _isArrowUp.value = !_isArrowUp.value
    }
}
