package com.minhhnn18898.architecture

abstract class NoParamUseCase<R> {
    fun execute(): R? {
        return run()
    }

    protected abstract fun run(): R?
}