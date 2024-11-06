package com.superbgoal.caritasrig.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Processor(
    val name: String,
    val price: Double,
    val core_count: Int,
    val core_clock: Double,
    val boost_clock: Double,
    val tdp: Int,
    val graphics: String,
    val smt: Boolean
) : Parcelable
