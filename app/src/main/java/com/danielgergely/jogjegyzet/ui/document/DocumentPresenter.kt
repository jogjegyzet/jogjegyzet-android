package com.danielgergely.jogjegyzet.ui.document

import com.danielgergely.jogjegyzet.domain.Comment
import com.danielgergely.jogjegyzet.domain.User
import com.danielgergely.jogjegyzet.service.*
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DocumentPresenter @Inject constructor(
        private val documentRepository: DocumentRepository,
        private val commentRepository: CommentRepository,
        private val userRepository: UserRepository,
        private val favoriteRepository: FavoriteRepository) {

    private lateinit var docSubject: BehaviorSubject<DocumentData>
    private val refreshSubject = PublishSubject.create<Any>()

    fun getViewModel(initial: DocumentData): Observable<ViewModel> {
        docSubject = BehaviorSubject.createDefault(initial)

        return getViewModel(initial.document.id, initial)
    }

    fun getViewModel(docId: String): Observable<ViewModel> {
        return getViewModel(docId, null)
    }

    private fun getViewModel(id: String, initial: DocumentData?) : Observable<ViewModel> {
        docSubject = BehaviorSubject.create()

        val initialVm = if (initial == null)
            ViewModel.Loading()
        else
            ViewModel.Data(initial, CommentsViewModel.Loading())

        val obs = Observables.combineLatest(
                documentRepository.getDocument(id)
                        .subscribeOn(Schedulers.io())
                        .repeatWhen { refreshSubject }
                        .doOnNext(docSubject::onNext),

                commentRepository.getCommentsForDocument(id)
                        .repeatWhen { refreshSubject }
                        .subscribeOn(Schedulers.io())
                        .flatMap { comments ->
                            val observables = comments.map { comment -> comment.user }
                                    .filter { userId -> userId != null }
                                    .distinct()
                                    .map { userId -> userRepository.getUser(userId!!).firstOrError() }

                            Single.merge(observables)
                                    .collect({ mutableListOf<User>() }, { container, value -> container.add(value) })
                                    .toObservable()
                                    .map { users -> comments.map { c -> CommentViewModel(c, users.find { it.id == c.user }) } }
                        }
                        .map { CommentsViewModel.Data(it) as CommentsViewModel }
                        .startWith(CommentsViewModel.Loading())
                        .onErrorReturn { CommentsViewModel.Error(it) }

        ) { doc, comments -> ViewModel.Data(doc, comments) as ViewModel }
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(initialVm)
                .onErrorReturn { ViewModel.Error() }
                .repeatWhen { it.delay(10, TimeUnit.SECONDS).mergeWith(refreshSubject) }

        return obs
    }

    val title: Observable<String> get() =
            docSubject.map { it.document.name }

}

sealed class ViewModel {
    class Loading : ViewModel()
    class Data(val document: DocumentData, val comments: CommentsViewModel) : ViewModel()
    class Error : ViewModel()
}

sealed class CommentsViewModel {
    class Loading: CommentsViewModel()
    class Data(val comments: List<CommentViewModel>) : CommentsViewModel()
    class Error(val error: Throwable): CommentsViewModel()
}

class CommentViewModel(val comment: Comment, val user: User?)
