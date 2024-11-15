package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InternalHardDrive(
    val name: String = "",
    val price: Double = 0.0,
    val capacity: Double = 0.0, // Capacity in GB
    val pricePerGb: Double = 0.0,
    val type: String = "",
    val cache: Int = 0, // Cache size in MB
    val formFactor: String = "",
    val interfacee: String = ""
) :Parcelable
