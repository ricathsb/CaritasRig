package com.superbgoal.caritasrig.data.model

data class Headphones(
    val name: String,
    val price: Double,
    val type: String,
    val frequencyResponse: List<Int>, // List of Int for frequency response values
    val microphone: Boolean,
    val wireless: Boolean,
    val enclosureType: String,
    val color: String
)

