package com.superbgoal.caritasrig.data.model.component

data class GpuBenchmark(
    val gpuName: String,
    val G3Dmark: Int,
    val G2Dmark: Int,
    val price: Double,
    val gpuValue: Double,
    val TDP: Double,
    val powerPerformance: Double,
    val testDate: Int,
    val category: String
)
