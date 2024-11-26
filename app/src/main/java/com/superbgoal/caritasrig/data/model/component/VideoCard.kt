package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoCard(
    val id : String = "",
    val name: String = "",
    val price: Double = 0.0,
    val chipset: String = "",
    val memory: Double = 0.0, // Memory size in GB
    @SerializedName("core_clock") val coreClock: String = "",
    val boostClock: Int = 0, // Boost clock speed in MHz
    val color: String = "",
    val length: Int = 0, // Length of the card in mm
) : Parcelable
