package com.superbgoal.caritasrig.data.model

data class CpuCooler(
    val name: String,
    val price: Double,
    val rpm: Int,
    val noiseLevel: Double,
    val color: String,
    val size: String? // size is nullable
)

