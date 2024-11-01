package com.superbgoal.caritasrig.data.model

data class Motherboard(
    val name: String,
    val price: Double,
    val socket: String,
    val formFactor: String,
    val maxMemory: Int, // Maximum memory in GB
    val memorySlots: Int, // Number of memory slots
    val color: String
)

