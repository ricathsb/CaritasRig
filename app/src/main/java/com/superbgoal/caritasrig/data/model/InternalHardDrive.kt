package com.superbgoal.caritasrig.data.model

data class InternalHardDrive(
    val name: String,
    val price: Double,
    val capacity: Int, // Capacity in GB
    val pricePerGb: Double,
    val type: String,
    val cache: Int, // Cache size in MB
    val formFactor: String,
    val interfacee: String
)
