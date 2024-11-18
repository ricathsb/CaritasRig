package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Mouse(
    val name: String ="",
    val price: Double = 0.0,
    val trackingMethod: String = "",
    val connectionType: String = "",
    val maxDpi: Int = 0, // Maximum DPI (dots per inch)
    val handOrientation: String = "",
    val color: String = ""
):Parcelable
