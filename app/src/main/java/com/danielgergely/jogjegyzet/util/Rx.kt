package com.danielgergely.jogjegyzet.util

import io.reactivex.Maybe

fun <T> maybeFromNullable(data: T?): Maybe<T> = if (data == null)
    Maybe.empty()
else
    Maybe.just(data)