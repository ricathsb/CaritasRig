package com.superbgoal.caritasrig.data.model

data class VideoCard(
    val name: String,
    val price: Double,
    val chipset: String,
    val memory: Double, // Memory size in GB
    val coreClock: Int, // Core clock speed in MHz
    val boostClock: Int, // Boost clock speed in MHz
    val color: String,
    val length: Int // Length of the card in mm
)
