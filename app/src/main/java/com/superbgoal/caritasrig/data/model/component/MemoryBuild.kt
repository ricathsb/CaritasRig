package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MemoryBuild(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL") private val rawImageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Speed") val speed: String = "",
    @SerializedName("Form Factor") val formFactor: String = "",
    @SerializedName("Modules") val modules: String = "",
    @SerializedName("Price / GB") val pricePerGb: String = "",
    @SerializedName("Color") val color: String = "",
    @SerializedName("First Word Latency") val firstWordLatency: String = "",
    @SerializedName("CAS Latency") val casLatency: Int = 0,
    @SerializedName("Voltage") val voltage: String = "",
    @SerializedName("Timing") val timing: String = "",
    @SerializedName("ECC / Registered") val eccRegistered: String = "",
    @SerializedName("Heat Spreader") val heatSpreader: String = "",
    @SerializedName("Specs Number") val specsNumber: Int = 0,
    @SerializedName("Arsitektur") val arsitektur: String = "",
    val quantity: Int = 1,
    val totalPrice: Double = 0.0
) : Parcelable {
    val imageUrl: String
        get() = "https:${rawImageUrl.replace("https:", "")}"
}
