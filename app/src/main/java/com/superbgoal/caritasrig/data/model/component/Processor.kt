package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Processor(
    val id : String = "",
    val name: String = "",
    val price: Double = 0.0,
    val core_count: Int = 0,
    val core_clock: Double = 0.0,
    val boost_clock: Double = 0.0,
    val tdp: Int = 0,
    val graphics: String = "",
    val smt: Boolean = false,
    val single_core_score: Int = 0,
    val multi_core_score: Int = 0,
    val image_url: String = ""
) :Parcelable
