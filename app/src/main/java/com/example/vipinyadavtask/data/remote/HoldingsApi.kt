package com.example.vipinyadavtask.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET

const val BASE_URL = "https://35dee773a9ec441e9f38d5fc249406ce.api.mockbin.io/"

@Serializable
data class ApiResponse(
    @SerialName("data") val data: HoldingsWrapper
)

@Serializable
data class HoldingsWrapper(
    @SerialName("userHolding") val userHolding: List<HoldingDto>
)

@Serializable
data class HoldingDto(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)

interface HoldingsApi {
    @GET("/")
    suspend fun getHoldings(): ApiResponse
}


