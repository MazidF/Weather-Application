package org.example.data.api

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error<out T>(val exception: Exception) : NetworkResult<T>()
}
