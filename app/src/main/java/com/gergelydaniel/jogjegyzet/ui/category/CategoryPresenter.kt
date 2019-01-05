package com.gergelydaniel.jogjegyzet.ui.category

import android.content.Context
import com.gergelydaniel.jogjegyzet.R
import com.gergelydaniel.jogjegyzet.domain.Category
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.CategoryRepository
import com.gergelydaniel.jogjegyzet.service.DocumentRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class CategoryPresenter @Inject constructor(private val categoryRepository: CategoryRepository,
                                            private val documentRepository: DocumentRepository,
                                            private val context: Context) {
    private val titleSubject: BehaviorSubject<String> = BehaviorSubject.create()

    val title: Observable<String> get() = titleSubject

    fun getViewModel(catId: String?): Observable<ViewModel> {
        val catObs: Observable<List<Category>>
        val docObs: Observable<List<Document>>

        if (catId == null) {
            catObs = categoryRepository.getRootCategories()
            docObs = Observable.just(listOf())

            titleSubject.onNext(context.getString(R.string.app_name))
        } else {
            catObs = categoryRepository.getSubCategories(catId)
            docObs = documentRepository.getDocumentsInCategory(catId)

            categoryRepository.getCategory(catId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {titleSubject.onNext(it.name)}
        }


        return Observables.combineLatest(
                catObs, docObs
        ) { cats, docs ->
            if (cats.size + docs.size > 0) {
                ViewModel.NonEmpty(cats.sortedBy { it.name }, docs.sortedBy { it.name })
            } else {
                ViewModel.Empty()
            }
        }
                .startWith(ViewModel.Loading())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}

sealed class ViewModel {
    class Loading : ViewModel()
    class NonEmpty(val categories: List<Category>, val documents: List<Document>) : ViewModel()
    class Empty : ViewModel()
}