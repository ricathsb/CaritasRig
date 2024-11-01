package com.superbgoal.caritasrig.data.model

data class Keyboard(
    val name: String,
    val price: Double,
    val style: String,
    val switches: String?, // switches is nullable
    val backlit: String,
    val tenkeyless: Boolean,
    val connectionType: String,
    val color: String
)

