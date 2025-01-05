package com.minhhnn18898.core.utils

fun Long.safeDiv(other: Int): Long {
    if(other == 0) {
        return this
    }

    return this.div(other)
}