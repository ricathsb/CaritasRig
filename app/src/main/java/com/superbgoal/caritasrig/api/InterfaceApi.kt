package com.superbgoal.caritasrig.api

import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyFreaksApi {
    @GET("latest")
    suspend fun getRates(
        @Query("apikey") apiKey: String,
        @Query("symbols") symbols: String
    ): CurrencyResponse
}

data class CurrencyResponse(
    val base: String,
    val date: String,
    val rates: Map<String, Double>
)
