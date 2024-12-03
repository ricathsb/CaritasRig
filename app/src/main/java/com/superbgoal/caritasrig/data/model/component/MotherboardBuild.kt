package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class MotherboardBuild(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL")  val imageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Socket/CPU") val socketCpu: String = "",
    @SerializedName("Form Factor") val formFactor: String = "",
    @SerializedName("Chipset") val chipset: String = "",
    @SerializedName("Memory Max") val memoryMax: String = "",
    @SerializedName("Memory Type") val memoryType: String = "",
    @SerializedName("Memory Slots") val memorySlots: Int = 0,
    @SerializedName("Memory Speed") val memorySpeed: String = "",
    @SerializedName("Color") val color: String = "",
    @SerializedName("PCIe x16 Slots") val pcieX16Slots: Int = 0,
    @SerializedName("PCIe x8 Slots") val pcieX8Slots: Int = 0,
    @SerializedName("PCIe x4 Slots") val pcieX4Slots: Int = 0,
    @SerializedName("PCIe x1 Slots") val pcieX1Slots: Int = 0,
    @SerializedName("PCI Slots") val pciSlots: Int = 0,
    @SerializedName("M.2 Slots") val m2Slots: String = "",
    @SerializedName("Mini-PCIe Slots") val miniPcieSlots: Int = 0,
    @SerializedName("Half Mini-PCIe Slots") val halfMiniPcieSlots: Int = 0,
    @SerializedName("Mini-PCIe / mSATA Slots") val miniPcieMsataSlots: Int = 0,
    @SerializedName("mSATA Slots") val msataSlots: Int = 0,
    @SerializedName("SATA 6.0 Gb/s") val sata6Gbps: Int = 0,
    @SerializedName("Onboard Ethernet") val onboardEthernet: String = "",
    @SerializedName("Onboard Video") val onboardVideo: String = "",
    @SerializedName("USB 2.0 Headers") val usb20Headers: Int = 0,
    @SerializedName("USB 2.0 Headers (Single Port)") val usb20SingleHeaders: Int = 0,
    @SerializedName("USB 3.2 Gen 1 Headers") val usb32Gen1Headers: Int = 0,
    @SerializedName("USB 3.2 Gen 2 Headers") val usb32Gen2Headers: Int = 0,
    @SerializedName("USB 3.2 Gen 2x2 Headers") val usb32Gen2x2Headers: Int = 0,
    @SerializedName("Supports ECC") val supportsEcc: String = "",
    @SerializedName("Wireless Networking") val wirelessNetworking: String = "",
    @SerializedName("RAID Support") val raidSupport: String = ""
) : Parcelable {

}
