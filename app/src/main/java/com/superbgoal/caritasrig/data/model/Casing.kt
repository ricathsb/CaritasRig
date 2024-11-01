package com.superbgoal.caritasrig.data.model

data class Casing(
    val name: String,
    val price: Double,
    val type: String,
    val color: String,
    val psu: String?,
    val sidePanel: String,
    val externalVolume: Double,
    val internal35Bays: Int
)
