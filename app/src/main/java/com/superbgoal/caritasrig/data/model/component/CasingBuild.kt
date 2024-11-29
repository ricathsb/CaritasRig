package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CasingBuild(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL") private val rawImageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Type") val type: String = "",
    @SerializedName("Color") val color: String = "",
    @SerializedName("Power Supply") val powerSupply: String = "",
    @SerializedName("Side Panel") val sidePanel: String = "",
    @SerializedName("Power Supply Shroud") val powerSupplyShroud: String = "",
    @SerializedName("Front Panel USB") val frontPanelUsb: String = "",
    @SerializedName("Motherboard Form Factor") val motherboardFormFactor: String = "",
    @SerializedName("Maximum Video Card Length") val maxVideoCardLength: String = "",
    @SerializedName("Drive Bays") val driveBays: String = "",
    @SerializedName("Expansion Slots") val expansionSlots: String = "",
    @SerializedName("Dimensions") val dimensions: String = "",
    @SerializedName("Volume") val volume: String = ""
) : Parcelable {
    val imageUrl: String
        get() = "https:${rawImageUrl.replace("https:", "")}"
}
