package com.gergelydaniel.jogjegyzet.ui.document

import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.DocumentRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class DocumentPresenter @Inject constructor(private val documentRepository: DocumentRepository) {
    private lateinit var docSubject: BehaviorSubject<Document>

    fun getViewModel(initial: Document): Observable<ViewModel> {
        docSubject = BehaviorSubject.createDefault(initial)

        return docSubject.observeOn(AndroidSchedulers.mainThread())
                .map { ViewModel.Data(it) as ViewModel }

    }

    fun getViewModel(docId: String): Observable<ViewModel> {
        docSubject = BehaviorSubject.create()

        return documentRepository.getDocument(docId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(docSubject::onNext)
                .map { ViewModel.Data(it) as ViewModel }
                .onErrorReturn { ViewModel.Error() }
                .startWith(ViewModel.Loading())
    }

    val title: Observable<String> get() =
            docSubject.map { it.name }

}

sealed class ViewModel {
    class Loading : ViewModel()
    class Data(val document: Document) : ViewModel()
    class Error : ViewModel()
}