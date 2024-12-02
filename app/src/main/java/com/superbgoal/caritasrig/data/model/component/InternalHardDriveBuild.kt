package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class InternalHardDriveBuild(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL")  val imageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Capacity") val capacity: String = "",
    @SerializedName("Price / GB") val pricePerGB: String = "",
    @SerializedName("Type") val type: String = "",
    @SerializedName("Cache") val cache: String = "",
    @SerializedName("Form Factor") val formFactor: String = "",
    @SerializedName("Interface") val interfaceType: String = "",
    @SerializedName("NVME") val nvme: String = "",
    @SerializedName("Arsitektur") val arsitektur: String = "",
) : Parcelable {

}
