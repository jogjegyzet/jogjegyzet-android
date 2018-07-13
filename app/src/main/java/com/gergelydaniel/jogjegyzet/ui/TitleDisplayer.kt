package com.gergelydaniel.jogjegyzet.ui

import io.reactivex.Observable

interface TitleProvider {
    val title: Observable<String>
}