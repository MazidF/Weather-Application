package org.example.ui

import data.storage.HistoryStorage
import java.io.File
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.example.data.api.NetworkResult
import org.example.data.api.WeatherService
import org.example.data.api.WeatherServiceImpl
import org.example.data.storage.HistoryStorageImpl
import org.example.ui.model.UIState

class WeatherApplicationImpl private constructor(
    private val weatherService: WeatherService,
    private val historyStorage: HistoryStorage,
    private val ioDispatcher: CoroutineDispatcher,
) : WeatherApplication {
    override fun executeCommand(command: String) {
        when(val supportedCommand = SupportedCommand.getCommand(command)) {
            SupportedCommand.GET_WEATHER -> getWeather(supportedCommand.extractArgs(command))
            SupportedCommand.GET_HISTORY -> showHistory(supportedCommand.extractArgs(command))
            null -> {
                println("Unknown command: $command")
                println("Available commands are:")
                println("\t\tgetWeather\t <city name>, <lat,long>")
                println("\t\thistory\t\t <optional city name>")
                printSeparator()
            }
        }
    }

    private fun getWeather(args: List<String>) = runBlocking {
        if (args.isEmpty()) {
            println("City name should be entered after the ${SupportedCommand.GET_WEATHER.commandBody}.")
            return@runBlocking
        }

        val cityName = args.first()
        safeApiCall {
            weatherService.getWeatherByCity(cityName)
        }.collectLatest { state ->
            when (state) {
                is UIState.Loading -> {
                    print("Loading ...")
                    while (isActive) {
                        delay(100)
                        print('.')
                    }
                }
                is UIState.Error -> {
                    println()
                    println("Failed to fetch weather for $cityName because of ${state.error.message}.")
                    printSeparator()
                }
                is UIState.Success -> {
                    val weatherInfo = state.data

                    println()
                    println("${weatherInfo.requestTime}: $cityName current weather is ${weatherInfo.condition.text}.")
                    printSeparator()

                    historyStorage.saveSearch(weatherInfo)
                }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private inline fun <T> safeApiCall(
        crossinline block: suspend () -> NetworkResult<T>,
    ) = flow {
        when (val result = block()) {
            is NetworkResult.Error -> emit(UIState.Error(result.error))
            is NetworkResult.Success -> emit(UIState.Success(result.data))
        }
    }.onStart {
        emit(UIState.Loading())
    }.catch { cause ->
        emit(UIState.Error(cause))
    }.debounce(200)
        .flowOn(ioDispatcher)

    private fun showHistory(args: List<String>) = runBlocking {
        val query = args.firstOrNull()
        val history = historyStorage.getSearchHistory(query).joinToString("\n")

        if (history.isBlank()) {
            println("No history found.")
            printSeparator()
            return@runBlocking
        }

        println("History${if(query.isNullOrBlank()) "" else " for $query"}: \n$history")
        printSeparator()
    }

    private fun printSeparator() = println("----------------------------------------------")

    private enum class SupportedCommand(val commandBody: String) {
        GET_WEATHER("getWeather"),
        GET_HISTORY("history");

        fun extractArgs(command: String): List<String> {
            return command.removePrefix(commandBody).trim().split(" ")
        }

        companion object {
            fun getCommand(command: String) = entries.firstOrNull { entry ->
                command.startsWith(entry.commandBody)
            }
        }
    }

    companion object {
        val INSTANCE by lazy {
            WeatherApplicationImpl(
                ioDispatcher = Dispatchers.IO,
                weatherService = WeatherServiceImpl("39fb50e076ee4946bf484953242806"),
                historyStorage = HistoryStorageImpl(File("../history.json"), Dispatchers.IO),
            )
        }
    }
}
