package org.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(
    val code: Int,
    val text: String,
)
