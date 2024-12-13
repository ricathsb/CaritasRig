package com.superbgoal.caritasrig.api

import com.superbgoal.caritasrig.screen.homepage.homepage.constant
import kotlinx.coroutines.*

@OptIn(DelicateCoroutinesApi::class)
    fun fetchCurrencyRates() {
        val apiKey = constant.apiKeyCurrency
        val symbols = "IDR,EUR"

        GlobalScope.launch {
            try {
                val response = RetrofitClient.api.getRates(apiKey, symbols)
                response.rates.forEach { (currency, rate) ->
                    println("1 USD ke $currency = $rate")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
