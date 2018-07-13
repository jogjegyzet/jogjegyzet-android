package com.gergelydaniel.jogjegyzet.ui.category

import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.CategoryRepository
import com.gergelydaniel.jogjegyzet.service.DocumentRepository
import com.gergelydaniel.jogjegyzet.service.DocumentRepository_Factory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class CategoryPresenter @Inject constructor(private val categoryRepository: CategoryRepository,
                                            private val documentRepository: DocumentRepository) {
    fun getViewModel(catId: String?): Observable<ViewModel> {
        val catObs: Observable<List<Category>>
        val docObs: Observable<List<Document>>

        if (catId == null) {
            catObs = categoryRepository.getRootCategories()
            docObs = Observable.just(listOf())
        } else {
            catObs = categoryRepository.getSubCategories(catId)
            docObs = documentRepository.getDocumentsInCategory(catId)
        }


        return Observables.combineLatest(
                catObs, docObs
        ) { cats, docs ->
            ViewModel.Data(cats.sortedBy { it.name }, docs.sortedBy { it.name }) as ViewModel
        }
                .startWith(ViewModel.Loading())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}

sealed class ViewModel {
    class Loading : ViewModel()
    class Data(val categories: List<Category>, val documents: List<Document>) : ViewModel()
}