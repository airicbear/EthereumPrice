package com.github.airicbear.ethereumprice.network

import com.github.airicbear.ethereumprice.model.EtherPriceResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL =
    "https://api.etherscan.io/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface EtherApiService {
    @GET("api?module=stats&action=ethprice")
    fun getEtherPrice(
        @Query("apikey") apiKey: String
    ): Call<EtherPriceResult>
}

object EtherApi {
    val retrofitService: EtherApiService by lazy {
        retrofit.create(EtherApiService::class.java)
    }
}