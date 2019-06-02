package com.gergelydaniel.jogjegyzet.ui.search

import com.gergelydaniel.jogjegyzet.api.ApiClient
import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.domain.SearchResult
import com.gergelydaniel.jogjegyzet.service.DocumentData
import com.gergelydaniel.jogjegyzet.service.FavoriteRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchPresenter @Inject constructor(private val apiClient: ApiClient,
                                          private val favoriteRepository: FavoriteRepository) {
    private val retrySubject = PublishSubject.create<Any>()

    fun getViewModel(query: String): Observable<ViewModel> {
        return apiClient.search(query)
                .toObservable()
                .flatMap { results ->

                    if(results.isEmpty()) {
                        Observable.just(ViewModel.Empty())
                    } else {
                        val docIds = results.filter { it is SearchResult.DocumentResult }
                                .map { (it as SearchResult.DocumentResult).document.id }

                        favoriteRepository.containsItems(docIds)
                                .map { favouriteContains ->
                                    val data: List<SearchResultViewModel> = results.map {
                                        when (it) {
                                            is SearchResult.CategoryResult -> SearchResultViewModel.CategoryResult(it.category)
                                            is SearchResult.DocumentResult -> {
                                                val docIndex = docIds.indexOf(it.document.id)
                                                val docInFavs = favouriteContains[docIndex]

                                                SearchResultViewModel.DocumentResult(DocumentData(it.document, docInFavs))
                                            }
                                        }
                                    }

                                    ViewModel.Data(data)
                                }.toObservable()

                        //ViewModel.Data(DocumentData(it, ))
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
    class Data(val data: List<SearchResultViewModel>) : ViewModel()
    class Empty : ViewModel()
    class Error(val error: Throwable) : ViewModel()
}

sealed class SearchResultViewModel {
    class CategoryResult(val category: Category) : SearchResultViewModel()
    class DocumentResult(val document: DocumentData) : SearchResultViewModel()
}