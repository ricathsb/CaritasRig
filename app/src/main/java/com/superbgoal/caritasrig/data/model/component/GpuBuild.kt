package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GpuBuild(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL") private val rawImageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Chipset") val chipset: String = "",
    @SerializedName("Memory") val memory: Float = 0.0f,
    @SerializedName("Memory Type") val memoryType: String = "",
    @SerializedName("Core Clock") val coreClock: String = "",
    @SerializedName("Boost Clock") val boostClock: String = "",
    @SerializedName("Effective Memory Clock") val effectiveMemoryClock: String = "",
    @SerializedName("Interface") val interfaceType: String = "",
    @SerializedName("Color") val color: String = "",
    @SerializedName("Frame Sync") val frameSync: String = "",
    @SerializedName("Length") val length: String = "",
    @SerializedName("TDP") val tdp: Double = 0.0,
    @SerializedName("Case Expansion Slot Width") val caseExpansionSlotWidth: Int = 0,
    @SerializedName("Total Slot Width") val totalSlotWidth: Int = 0,
    @SerializedName("Cooling") val cooling: String = "",
    @SerializedName("External Power") val externalPower: String = "",
    @SerializedName("HDMI Outputs") val hdmiOutputs: String = "",
    @SerializedName("DisplayPort Outputs") val displayPortOutputs: String = "",
    @SerializedName("DVI-D Dual Link Outputs") val dviDOutputs: String = "N/A",
    @SerializedName("HDMI 2.1a Outputs") val hdmi2Outputs: String = "N/A",
    @SerializedName("DisplayPort 1.4 Outputs") val displayPort14Outputs: String = "N/A",
    @SerializedName("DisplayPort 1.4a Outputs") val displayPort14aOutputs: String = "N/A",
    @SerializedName("DisplayPort 2.1 Outputs") val displayPort21Outputs: String = "N/A",
    @SerializedName("SLI/CrossFire") val sliCrossFire: String = "N/A"
) : Parcelable {
    val imageUrl: String
        get() = "https:${rawImageUrl.replace("https:", "")}"
}
