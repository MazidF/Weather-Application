package data.storage

import org.example.data.model.WeatherInfo

interface HistoryStorage {
    suspend fun saveSearch(weatherInfo: WeatherInfo)
    suspend fun getSearchHistory(query: String?): List<WeatherInfo>
}
