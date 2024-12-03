package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProcessorTrial(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL")  val imageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Series") val series: String = "",
    @SerializedName("Microarchitecture") val microarchitecture: String = "",
    @SerializedName("Core Family") val coreFamily: String = "",
    @SerializedName("Socket") val socket: String = "",
    @SerializedName("Core Count") val coreCount: Int = 0,
    @SerializedName("Performance Core Clock") val performanceCoreClock: Double = 0.0,
    @SerializedName("Performance Core Boost Clock") val performanceCoreBoostClock: Double = 0.0,
    @SerializedName("Efficiency Core Clock") val efficiencyCoreClock: String? = null,
    @SerializedName("Efficiency Core Boost Clock") val efficiencyCoreBoostClock: String? = null,
    @SerializedName("L2 Cache") val l2Cache: String = "",
    @SerializedName("L3 Cache") val l3Cache: String = "",
    @SerializedName("TDP") val tdp: String = "",
    @SerializedName("Integrated Graphics") val integratedGraphics: String = "",
    @SerializedName("Maximum Supported Memory") val maxSupportedMemory: String = "",
    @SerializedName("ECC Support") val eccSupport: String = "",
    @SerializedName("Includes Cooler") val includesCooler: String = "",
    @SerializedName("Packaging") val packaging: String = "",
    @SerializedName("Lithography") val lithography: String = "",
    @SerializedName("Includes CPU Cooler") val includesCpuCooler: String = "",
    @SerializedName("Simultaneous Multithreading") val smt: Boolean = false
) : Parcelable




