package org.example.data.model

data class WeatherInfo(
    val location: String,
    val requestTime: String,
    val condition: WeatherCondition,
)
