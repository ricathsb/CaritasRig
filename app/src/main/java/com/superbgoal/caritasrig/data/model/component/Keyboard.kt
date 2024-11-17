package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Keyboard(
    val name: String = "",
    val price: Double = 0.0,
    val style: String = "",
    val switches: String? = null,
    val backlit: String? = "",
    val tenkeyless: Boolean = false,
    val connectionType: String = "",
    val color: String = ""
): Parcelable

