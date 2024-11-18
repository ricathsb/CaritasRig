package com.superbgoal.caritasrig.data.model

import com.superbgoal.caritasrig.data.model.buildmanager.Build

data class User(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val dateOfBirth: String = "",
    val profileImageUrl: String? = null
)

