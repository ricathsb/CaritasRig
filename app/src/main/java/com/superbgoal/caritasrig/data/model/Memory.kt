package com.superbgoal.caritasrig.data.model

data class Memory(
    val name: String,
    val price: Double,
    val speed: List<Double>, // List of Double for speed values (e.g., [5, 6600])
    val modules: List<Int>, // List of Int for module sizes (e.g., [2, 16])
    val pricePerGb: Double, // Renamed from price_per_gb for camelCase
    val color: String,
    val firstWordLatency: Double, // Updated to Double for correct mapping of decimal values
    val casLatency: Int // CAS latency remains as Int
)
