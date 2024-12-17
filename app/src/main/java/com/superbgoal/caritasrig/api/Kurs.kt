package com.superbgoal.caritasrig.api

import android.content.Context
import com.superbgoal.caritasrig.screen.homepage.homepage.constant
import android.util.Log

object Kurs {
    private const val TAG = "Kurs"
    private var rates: MutableMap<String, Double> = mutableMapOf() // Menyimpan semua kurs
    private var isInitialized = false

    suspend fun initializeRates() {
        if (!isInitialized) {
            val apiKey = constant.apiKeyCurrency
            try {
                // Permintaan ke API untuk beberapa mata uang
                val response = RetrofitClient.api.getRates(apiKey, "USD,IDR,EUR,JPY,INR,CNY")
                Log.d(TAG, "Response rates: ${response.rates}") // Log seluruh rates

                // Simpan semua rates ke dalam map
                rates.putAll(response.rates)

                // Tandai sebagai diinisialisasi
                isInitialized = true
                Log.d(TAG, "Rates berhasil diinisialisasi: $rates")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mendapatkan kurs mata uang: ${e.message}", e)
            }
        } else {
            Log.d(TAG, "Rates sudah diinisialisasi sebelumnya.")
        }
    }

    // Fungsi untuk mengambil kurs berdasarkan currency di Shared Preferences
    fun getRate(context: Context): Double? {
        val sharedPreferences = context.getSharedPreferences("BuildPrefs", Context.MODE_PRIVATE)
        val currencyCode = sharedPreferences.getString("selected_currency", "USD")
        val rate = rates[currencyCode]
        if (rate == null) {
            Log.e(TAG, "Kurs untuk $currencyCode tidak ditemukan. Pastikan initializeRates() sudah dipanggil.")
        } else {
            Log.d(TAG, "Mengambil kurs $currencyCode: $rate")
        }
        return rate
    }
}





