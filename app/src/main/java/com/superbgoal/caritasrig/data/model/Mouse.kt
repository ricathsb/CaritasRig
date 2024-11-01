package com.superbgoal.caritasrig.data.model

data class Mouse(
    val name: String,
    val price: Double,
    val trackingMethod: String,
    val connectionType: String,
    val maxDpi: Int, // Maximum DPI (dots per inch)
    val handOrientation: String,
    val color: String
)

