package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoCard(
    val name: String = "",
    val price: Double = 0.0,
    val chipset: String = "",
    val memory: Double = 0.0, // Memory size in GB
    val coreClock: Int = 0, // Core clock speed in MHz
    val boostClock: Int = 0, // Boost clock speed in MHz
    val color: String = "",
    val length: Int = 0, // Length of the card in mm
) : Parcelable
