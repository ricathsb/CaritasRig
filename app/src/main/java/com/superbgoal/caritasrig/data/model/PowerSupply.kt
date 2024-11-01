package com.superbgoal.caritasrig.data.model

data class PowerSupply(
    val name: String,
    val price: Double,
    val type: String,
    val efficiency: String, // Efficiency rating (e.g., gold)
    val wattage: Int, // Wattage of the power supply
    val modular: String, // Type of modularity (e.g., Full)
    val color: String
)
