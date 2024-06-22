package com.minhhnn18898.architecture

abstract class UseCase<P, R> {
    fun execute(params: P): R? {
        return run(params)
    }

    protected abstract fun run(params: P): R?
}