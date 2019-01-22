package com.gergelydaniel.jogjegyzet.ui.home

import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.CategoryRepository
import com.gergelydaniel.jogjegyzet.service.FavoriteRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class HomePresenter @Inject constructor(private val categoryRepository: CategoryRepository,
                                        private val favoriteRepository: FavoriteRepository) {

    fun getViewModel(): Observable<ViewModel> {
        return Observables.combineLatest(
                categoryRepository.getRootCategories()
                        .map { CategoriesViewModel.Data(it) as CategoriesViewModel }
                        .startWith(CategoriesViewModel.Loading())
                        .onErrorReturn { CategoriesViewModel.Error(it) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()),

                favoriteRepository.getFavorites().toObservable()
        ) { cats, favs -> ViewModel(cats, favs) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}

class ViewModel(val categories: CategoriesViewModel, val favorites: List<Document>)

sealed class CategoriesViewModel {
    class Loading : CategoriesViewModel()
    class Data(val categories: List<Category>) : CategoriesViewModel()
    class Error(val error: Throwable) : CategoriesViewModel()
}