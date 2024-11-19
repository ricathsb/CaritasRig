package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Motherboard(
    val name: String = "",
    val price: Double = 0.0,
    val socket: String = "",
    val formFactor: String = "",
    val maxMemory: Int = 0, // Maximum memory in GB
    val memorySlots: Int = 0, // Number of memory slots
    val color: String = "",
    @SerializedName("image_url") val imageUrl: String = ""
):Parcelable

