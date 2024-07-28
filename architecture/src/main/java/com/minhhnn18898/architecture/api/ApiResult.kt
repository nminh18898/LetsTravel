package com.minhhnn18898.architecture.api

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T): ApiResult<T>
    data class Error(val exception: Throwable? = null): ApiResult<Nothing>
}