package com.minhhnn18898.architecture.usecase

abstract class AsyncUseCase<P, R> {
    suspend fun execute(params: P): R {
        return run(params)
    }

    protected abstract suspend fun run(params: P): R
}