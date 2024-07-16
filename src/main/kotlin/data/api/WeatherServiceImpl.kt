package org.example.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.gson.gson
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.example.data.api.serializer.WeatherInfoAdapter
import org.example.data.model.WeatherInfo

class WeatherServiceImpl(
    private val client: HttpClient,
    private val ioDispatcher: CoroutineDispatcher,
) : WeatherService {

    constructor(apiKey: String) : this(
        HttpClient(CIO) {
            install(Logging) {
                level = LogLevel.BODY
                logger = Logger.DEFAULT
            }
            install(ContentNegotiation) {
                gson {
                    setLenient()
                    setPrettyPrinting()
                    registerTypeAdapter(WeatherInfo::class.java, WeatherInfoAdapter())
                }
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                url(BASE_URL)
                header(QueryParams.KEY, apiKey)
            }
        },
        Dispatchers.IO,
    )

    override suspend fun getWeatherByCity(cityName: String) = withContext(ioDispatcher) {
        client.get {
            url {
                parameters.append(QueryParams.QUERY, cityName)
            }
        }.bodyResult<WeatherInfo>()
    }

    override suspend fun getWeatherByLatLong(lat: Float, long: Float) = withContext(ioDispatcher) {
        client.get {
            url {
                parameters.append(QueryParams.QUERY, "$lat,$long")
            }
        }.bodyResult<WeatherInfo>()
    }

    private suspend inline fun <reified T> HttpResponse.bodyResult() = withContext(ioDispatcher) {
        try {
            NetworkResult.Success(body<T>())
        } catch (e: NoTransformationFoundException) {
            NetworkResult.Error(e)
        }
    }

    private object QueryParams {
        const val KEY = "key"
        const val QUERY = "q"
    }

    private companion object {
        const val BASE_URL = "https://api.weatherapi.com/v1/current.json"
    }
}