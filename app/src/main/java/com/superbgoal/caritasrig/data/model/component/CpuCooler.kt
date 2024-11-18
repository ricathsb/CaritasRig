package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CpuCooler(
    val name: String = "",
    val price: Double = 0.0,
    val rpm: List<Int> = emptyList(), // Change this to List<Int> to handle the array
    val noise_level: List<Double> = emptyList(), // Change this to List<Double> to handle the array
    val color: String = "",
    val size: Int = 0
):Parcelable

