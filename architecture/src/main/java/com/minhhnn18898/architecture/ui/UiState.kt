package com.minhhnn18898.architecture.ui

sealed class UiState<out T> {
    data class Success<T>(val data: T): UiState<T>()

    data class Error(val errorCode: Int = 0) : UiState<Nothing>()

    data object Loading: UiState<Nothing>()
}