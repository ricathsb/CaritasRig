package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Motherboard(
    val name: String = "",
    val price: Double = 0.0,
    val socket: String = "",
    val formFactor: String = "",
    val maxMemory: Int =0, // Maximum memory in GB
    val memorySlots: Int = 0, // Number of memory slots
    val color: String = "",
    val imageUrl: String = ""
):Parcelable

