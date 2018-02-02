package com.gergelydaniel.jogjegyzet.ui.home

import com.gergelydaniel.jogjegyzet.SomeDataStuff
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomePresenter @Inject constructor(private val dep: SomeDataStuff) {

    private val subject = BehaviorSubject.createDefault(1)

    fun data(): Observable<String> = Observable.interval(1, TimeUnit.SECONDS)
            .flatMap { subject.take(1) }
            .map {
                subject.onNext(it+1)
                it
            }
            .withLatestFrom(Observable.fromCallable { dep.getData() }, BiFunction { num:Int, d : String -> "" + d + num})
            .observeOn(AndroidSchedulers.mainThread())

}