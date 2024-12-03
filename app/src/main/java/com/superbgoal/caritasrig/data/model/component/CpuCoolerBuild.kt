package com.superbgoal.caritasrig.data.model.component

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CpuCoolerBuild(
    @SerializedName("Name") val name: String = "",
    @SerializedName("Image URL")  val imageUrl: String = "",
    @SerializedName("Product URL") val productUrl: String = "",
    @SerializedName("Price") val price: Double = 0.0,
    @SerializedName("Manufacturer") val manufacturer: String = "",
    @SerializedName("Model") val model: String = "",
    @SerializedName("Part #") val partNumber: String = "",
    @SerializedName("Fan RPM") val fanRpm: String = "",
    @SerializedName("Noise Level") val noiseLevel: String = "",
    @SerializedName("Color") val color: String = "",
    @SerializedName("Height") val height: String = "",
    @SerializedName("CPU Socket") val cpuSocket: String = "",
    @SerializedName("Water Cooled") val waterCooled: String = "",
    @SerializedName("Fanless") val fanless: String = "",
    @SerializedName("Specs Number") val specsNumber: String = ""
) : Parcelable {


    fun getMinNoiseLevel(): Double {
        return if ("-" in noiseLevel) {
            noiseLevel.split(" - ")[0].trim().toDoubleOrNull() ?: 0.0
        } else {
            noiseLevel.toDoubleOrNull() ?: 0.0
        }
    }

    fun getMaxNoiseLevel(): Double {
        return if ("-" in noiseLevel) {
            noiseLevel.split(" - ")[1].trim().toDoubleOrNull() ?: 0.0
        } else {
            noiseLevel.toDoubleOrNull() ?: 0.0
        }
    }
}
