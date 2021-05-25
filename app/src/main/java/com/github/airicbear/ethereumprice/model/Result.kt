package com.github.airicbear.ethereumprice.model

import com.squareup.moshi.Json

data class Result(
    @Json(name = "ethbtc") val bitcoinValue: String,
    @Json(name = "ethbtc_timestamp") val bitcoinValueTimestamp: String,
    @Json(name = "ethusd") val usdValue: String,
    @Json(name = "ethusd_timestamp") val usdValueTimestamp: String
)