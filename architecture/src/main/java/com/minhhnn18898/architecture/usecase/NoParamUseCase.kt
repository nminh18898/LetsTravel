package com.minhhnn18898.architecture.usecase

abstract class NoParamUseCase<R> {
    fun execute(): R {
        return run()
    }

    protected abstract fun run(): R
}