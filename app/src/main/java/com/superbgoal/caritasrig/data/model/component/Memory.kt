package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Memory(
    val name: String = "",
    val price: Double = 0.0,
    val speed: Int = 0, // List of Double for speed values (e.g., [5, 6600])
    val modules: Int = 0, // List of Int for module sizes (e.g., [2, 16])
    val pricePerGb: Double = 0.0, // Renamed from price_per_gb for camelCase
    val color: String = "",
    val firstWordLatency: Double =0.0, // Updated to Double for correct mapping of decimal values
    val casLatency: Double = 0.0, // CAS latency remains as Int'
    val socket: Int = 0 // Socket remains as Int
):Parcelable
