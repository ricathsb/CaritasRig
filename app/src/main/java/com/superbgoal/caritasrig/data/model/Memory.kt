package com.superbgoal.caritasrig.data.model

data class Memory(
    val name: String,
    val price: Double,
    val speed: List<Int>, // List of Int for speed values (e.g., [4, 3200])
    val modules: List<Int>, // List of Int for module sizes (e.g., [2, 8])
    val pricePerGb: Double,
    val color: String,
    val firstWordLatency: Int, // First word latency in nanoseconds
    val casLatency: Int // CAS latency
)
