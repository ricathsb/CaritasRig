package com.superbgoal.caritasrig.data.model

data class CpuCooler(
    val name: String,
    val price: Double,
    val rpm: List<Int>, // Change this to List<Int> to handle the array
    val noise_level: List<Double>, // Change this to List<Double> to handle the array
    val color: String,
    val size: Int
)

