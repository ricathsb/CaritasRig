package com.superbgoal.caritasrig.data.model

data class User(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val dateOfBirth: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
) {
}

