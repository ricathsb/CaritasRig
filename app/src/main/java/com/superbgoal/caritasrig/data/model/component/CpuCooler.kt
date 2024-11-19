package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CpuCooler(
    val name: String = "",
    val price: Double = 0.0,
    val rpm: Double = 0.0,
    val noise_level: Double = 0.0,
    val color: String = "",
    val size: Int = 0
):Parcelable

