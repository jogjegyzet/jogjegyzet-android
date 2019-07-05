package com.gergelydaniel.jogjegyzet.ui.home

import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.CategoryRepository
import com.gergelydaniel.jogjegyzet.service.FavoriteRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomePresenter @Inject constructor(private val categoryRepository: CategoryRepository,
                                        private val favoriteRepository: FavoriteRepository) {

    private val retrySubject = PublishSubject.create<Any>()

    fun getViewModel(): Observable<ViewModel> {
        return Observables.combineLatest(
                categoryRepository.getRootCategories()
                        .map { CategoriesViewModel.Data(it.sortedBy { it.name }) as CategoriesViewModel }
                        .startWith(CategoriesViewModel.Loading())
                        .flatMap { Observable.concat(Observable.just(it), Observable.never<CategoriesViewModel>()) }
                        .onErrorReturn { CategoriesViewModel.Error(it) }
                        .repeatWhen { it.delay(10, TimeUnit.SECONDS).mergeWith(retrySubject) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()),

                favoriteRepository.getFavorites()
        ) { cats, favs -> ViewModel(cats, favs) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun retry() {
        retrySubject.onNext(Unit)
    }
}

class ViewModel(val categories: CategoriesViewModel, val favorites: List<Document>)

sealed class CategoriesViewModel {
    class Loading : CategoriesViewModel()
    class Data(val categories: List<Category>) : CategoriesViewModel()
    class Error(val error: Throwable) : CategoriesViewModel()
}