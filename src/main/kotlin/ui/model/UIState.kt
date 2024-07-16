package org.example.ui.model

sealed class UIState<T> {
    class Loading<T> : UIState<T>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error<T>(val error: Throwable) : UIState<T>()
}
