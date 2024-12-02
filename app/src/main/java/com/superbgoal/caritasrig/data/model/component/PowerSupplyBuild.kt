package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PowerSupplyBuild(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL") private val rawImageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Model") val model: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Type") val type: String = "",
    @SerializedName("Efficiency Rating") val efficiencyRating: String = "",
    @SerializedName("Wattage") val wattage: String = "",
    @SerializedName("Length") val length: String = "",
    @SerializedName("Modular") val modular: String = "",
    @SerializedName("Color") val color: String = "",
    @SerializedName("Fanless") val fanless: String = "",
    @SerializedName("ATX 4-Pin Connectors") val atx4PinConnectors: Int = 0,
    @SerializedName("EPS 8-Pin Connectors") val eps8PinConnectors: Int = 0,
    @SerializedName("PCIe 12+4-Pin 12VHPWR Connectors") val pcie12VhpwrConnectors: Int = 0,
    @SerializedName("PCIe 12-Pin Connectors") val pcie12PinConnectors: Int = 0,
    @SerializedName("PCIe 8-Pin Connectors") val pcie8PinConnectors: Int = 0,
    @SerializedName("PCIe 6+2-Pin Connectors") val pcie6Plus2PinConnectors: Int = 0,
    @SerializedName("PCIe 6-Pin Connectors") val pcie6PinConnectors: Int = 0,
    @SerializedName("SATA Connectors") val sataConnectors: Int = 0,
    @SerializedName("Molex 4-Pin Connectors") val molex4PinConnectors: Int = 0,
    @SerializedName("Specs Number") val specsNumber: Int = 0
) : Parcelable {
    val imageUrl: String
        get() = "https:${rawImageUrl.replace("https:", "")}"
}
