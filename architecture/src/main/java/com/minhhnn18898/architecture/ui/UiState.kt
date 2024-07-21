package com.minhhnn18898.architecture.ui

sealed interface UiState<out T, out R> {
    data class Success<T>(val data: T): UiState<T, Nothing>
    data class Error<R>(val error: R): UiState<Nothing, R>
    data object Loading: UiState<Nothing, Nothing>

    data object UndefinedError
}