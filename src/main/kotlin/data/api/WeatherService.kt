package org.example.data.api

import org.example.data.model.WeatherInfo

interface WeatherService {
    suspend fun getWeatherByCity(cityName: String): NetworkResult<WeatherInfo>
    suspend fun getWeatherByLatLong(lat: Float, long: Float): NetworkResult<WeatherInfo>
    // TODO: Implements getWeatherByIP(ip: String) for more points
}
