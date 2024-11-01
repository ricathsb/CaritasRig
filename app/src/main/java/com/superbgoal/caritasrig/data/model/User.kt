package com.superbgoal.caritasrig.data.model

data class User(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val username: String,
    val dateOfBirth: String,
    val email: String // Tetap di sini untuk keperluan aplikasi, tetapi tidak akan disimpan di database
)
