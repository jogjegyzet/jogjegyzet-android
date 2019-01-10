package com.gergelydaniel.jogjegyzet.ui.document

import com.gergelydaniel.jogjegyzet.domain.Comment
import com.gergelydaniel.jogjegyzet.domain.Document
import com.gergelydaniel.jogjegyzet.service.CommentRepository
import com.gergelydaniel.jogjegyzet.service.DocumentRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class DocumentPresenter @Inject constructor(
        private val documentRepository: DocumentRepository,
        private val commentRepository: CommentRepository) {
    private lateinit var docSubject: BehaviorSubject<Document>

    fun getViewModel(initial: Document): Observable<ViewModel> {
        docSubject = BehaviorSubject.createDefault(initial)

        return getViewModel(initial.id, initial)
    }

    fun getViewModel(docId: String): Observable<ViewModel> {
        return getViewModel(docId, null)
    }

    private fun getViewModel(id: String, initial: Document?) : Observable<ViewModel> {
        docSubject = BehaviorSubject.create()

        val initialVm = if (initial == null)
            ViewModel.Loading()
        else
            ViewModel.Data(initial, CommentsViewModel.Loading())

        val obs = Observables.combineLatest(
                documentRepository.getDocument(id)
                        .subscribeOn(Schedulers.io())
                        .doOnNext(docSubject::onNext),

                commentRepository.getCommentsForDocument(id)
                        .subscribeOn(Schedulers.io())
                        .map { CommentsViewModel.Data(it) as CommentsViewModel }
                        .startWith(CommentsViewModel.Loading())

        ) { doc, comments -> ViewModel.Data(doc, comments) as ViewModel }
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(initialVm)
                .onErrorReturn { ViewModel.Error() }

        return obs
    }

    val title: Observable<String> get() =
            docSubject.map { it.name }

}

sealed class ViewModel {
    class Loading : ViewModel()
    class Data(val document: Document, val comments: CommentsViewModel) : ViewModel()
    class Error : ViewModel()
}

sealed class CommentsViewModel {
    class Loading: CommentsViewModel()
    class Data(val comments: List<Comment>) : CommentsViewModel()
}