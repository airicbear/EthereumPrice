package com.github.airicbear.ethereumprice.model

import com.squareup.moshi.Json

data class EtherPriceResult(
    @Json(name = "status") val status: String,
    @Json(name = "message") val message: String,
    @Json(name = "result") val result: Result
)