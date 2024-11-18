package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PowerSupply(
    val name: String ="",
    val price: Double = 0.0,
    val type: String = "",
    val efficiency: String = "", // Efficiency rating (e.g., gold)
    val wattage: Int = 0, // Wattage of the power supply
    val modular: String = "", // Type of modularity (e.g., Full)
    val color: String = ""
):Parcelable
