package org.example

import org.example.ui.WeatherApplication
import org.example.ui.WeatherApplicationImpl

fun main() {
    val weatherApplication: WeatherApplication = WeatherApplicationImpl.INSTANCE
    while (true) {
        val userCommand = readlnOrNull() ?: continue
        weatherApplication.executeCommand(userCommand)
    }
}
