package com.gergelydaniel.jogjegyzet.ui.home

import com.gergelydaniel.jogjegyzet.SomeDataStuff
import io.reactivex.Observable
import javax.inject.Inject

class HomePresenter @Inject constructor(private val dep: SomeDataStuff) {

    fun data(): Observable<String> = Observable.fromCallable { dep.getData() }

}