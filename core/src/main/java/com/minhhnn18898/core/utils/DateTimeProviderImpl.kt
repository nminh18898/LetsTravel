package com.minhhnn18898.core.utils

class DateTimeProviderImpl: DateTimeProvider {
    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }
}