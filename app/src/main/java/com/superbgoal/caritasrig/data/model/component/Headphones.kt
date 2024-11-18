package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Headphones(
    val name: String = "",
    val price: Double = 0.0,
    val type: String = "",
    val frequencyResponse: List<Int> = emptyList(), // List of Int for frequency response values
    val microphone: Boolean = false,
    val wireless: Boolean = false,
    val enclosureType: String = "",
    val color: String = ""
):Parcelable

