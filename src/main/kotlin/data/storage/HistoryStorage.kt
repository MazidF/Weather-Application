package data.storage

import org.example.data.model.WeatherInfo

interface HistoryStorage {
    suspend fun saveSearch(weatherInfo: WeatherInfo)
    suspend fun getSearchHistory(): List<WeatherInfo>
    // TODO: Implements getSearchHistory(query: String) for more points
}
