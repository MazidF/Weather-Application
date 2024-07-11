package org.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(
    val condition: String,
    val code: Int,
)
