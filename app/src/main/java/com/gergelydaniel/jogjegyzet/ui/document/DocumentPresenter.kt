package com.gergelydaniel.jogjegyzet.ui.document

import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.DocumentRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class DocumentPresenter @Inject constructor(private val documentRepository: DocumentRepository) {

    fun getViewModel(docId: String): Observable<ViewModel> {
        return documentRepository.getDocument(docId)
                .map { ViewModel.Data(it) as ViewModel }
                .startWith(ViewModel.Loading())
                .onErrorReturn { ViewModel.Error() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}

sealed class ViewModel {
    class Loading : ViewModel()
    class Data(val document: Document) : ViewModel()
    class Error : ViewModel()
}