package com.superbgoal.caritasrig.api

import com.superbgoal.caritasrig.screen.homepage.homepage.constant

object Kurs {
    private var rates: MutableMap<String, Double> = mutableMapOf()
    private var isInitialized = false

    suspend fun initializeRates() {
        if (!isInitialized) {
            val apiKey = constant.apiKeyCurrency
            try {
                val response = RetrofitClient.api.getRates(apiKey, "USD")
                rates.putAll(response.rates)
                isInitialized = true
                println("Kurs berhasil diperbarui: $rates")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Gagal mendapatkan kurs mata uang.")
            }
        }
    }

    fun getRate(toCurrency: String): Double? {
        return rates[toCurrency]
    }
}