package com.minhhnn18898.architecture.usecase

abstract class NoParamAsyncUseCase<R> {
    suspend fun execute(): R {
        return run()
    }

    protected abstract suspend fun run(): R
}