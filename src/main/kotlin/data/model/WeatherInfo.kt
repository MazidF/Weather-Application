package org.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherInfo(
    val location: String,
    val requestTime: String,
    val condition: WeatherCondition,
)
