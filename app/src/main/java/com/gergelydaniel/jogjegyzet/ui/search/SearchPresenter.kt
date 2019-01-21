package com.gergelydaniel.jogjegyzet.ui.search

import com.gergelydaniel.jogjegyzet.api.ApiClient
import com.gergelydaniel.jogjegyzet.domain.SearchResult
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchPresenter @Inject constructor(private val apiClient: ApiClient) {
    private val retrySubject = PublishSubject.create<Any>()

    fun getViewModel(query: String): Observable<ViewModel> {
        return apiClient.search(query)
                .toObservable()
                .map {
                    if(it.isEmpty()) {
                        ViewModel.Empty()
                    } else {
                        ViewModel.Data(it)
                    }
                }
                .startWith(ViewModel.Loading())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn { ViewModel.Error(it) }
                .repeatWhen { it.delay(10, TimeUnit.SECONDS).mergeWith(retrySubject) }
    }

    fun retry() {
        retrySubject.onNext(Unit)
    }
}

sealed class ViewModel {
    class Loading : ViewModel()
    class Data(val data: List<SearchResult>) : ViewModel()
    class Empty : ViewModel()
    class Error(val error: Throwable) : ViewModel()
}