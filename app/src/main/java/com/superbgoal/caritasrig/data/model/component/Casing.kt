package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Casing(
    val id:String = "",
    val name: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val color: String = "",
    val psu: String? = null,
    val sidePanel: String = "",
    val externalVolume: Double = 0.0,
    val internal35Bays: Int = 0
) : Parcelable
