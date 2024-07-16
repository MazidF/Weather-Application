package org.example.data.storage

import data.storage.HistoryStorage
import java.io.File
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.example.data.model.WeatherInfo

class HistoryStorageImpl(
    private val targetFile: File,
    private val ioDispatcher: CoroutineDispatcher,
): HistoryStorage {
    private val lock = ReentrantReadWriteLock(true)

    private lateinit var weatherInfos: List<WeatherInfo>

    override suspend fun saveSearch(weatherInfo: WeatherInfo) = lock.write {
        loadWeatherInfosInNeeded()

        weatherInfos += weatherInfo

        withContext(ioDispatcher) {
            val jsonText = Json.encodeToString(weatherInfos)
            targetFile.writeText(jsonText)
        }
    }

    override suspend fun getSearchHistory(query: String?) = lock.read {
        loadWeatherInfosInNeeded()
        if (query.isNullOrBlank()) {
            return@read weatherInfos
        }
        weatherInfos.filter { it.location == query }
    }

    private suspend fun createTheTargetFileIfNeeded() = withContext(ioDispatcher) {
        if (targetFile.exists()) return@withContext
        targetFile.parentFile?.mkdirs()
        targetFile.createNewFile()
        targetFile.writeText("[]")
    }

    private suspend fun loadWeatherInfosInNeeded() = withContext(ioDispatcher) {
        createTheTargetFileIfNeeded()

        if (this@HistoryStorageImpl::weatherInfos.isInitialized) {
            return@withContext weatherInfos
        }

        val fileContent = targetFile.readText()
        Json.decodeFromString<List<WeatherInfo>>(fileContent).also {
            weatherInfos = it.toMutableList()
        }
    }
}
