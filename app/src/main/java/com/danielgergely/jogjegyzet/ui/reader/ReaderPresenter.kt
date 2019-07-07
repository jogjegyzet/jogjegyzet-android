package com.danielgergely.jogjegyzet.ui.reader

import com.danielgergely.jogjegyzet.service.DocumentData
import com.danielgergely.jogjegyzet.service.DocumentRepository
import com.danielgergely.jogjegyzet.service.FavoriteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ReaderPresenter @Inject constructor(private val documentRepository: DocumentRepository,
                                          private val favoriteRepository: FavoriteRepository) {
    private val docSubject: BehaviorSubject<DocumentData> = BehaviorSubject.create()
    private val refreshSubject = PublishSubject.create<Any>()

    private fun getDocument(): Single<DocumentData> {
        return docSubject
                .filter { it != null }
                .firstOrError()
    }

    fun getViewModel(docId: String): Observable<DocumentData> {
        return documentRepository.getDocument(docId)
                .subscribeOn(Schedulers.io())
                .repeatWhen { refreshSubject }
                .doOnNext(docSubject::onNext)
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun addToFavorites(): Single<Completable> {
        return getDocument()
                .flatMapCompletable { favoriteRepository.insert(it.document).subscribeOn(Schedulers.io()) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { refreshSubject.onNext(Any()) }
                .toSingle { removeFromFavorites().ignoreElement() }
    }

    fun removeFromFavorites(): Single<Completable> {
        return getDocument()
                .flatMap { favoriteRepository.deleteById(it.document.id).subscribeOn(Schedulers.io()) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { refreshSubject.onNext(Any()) }
                .map { index ->
                    getDocument().flatMapCompletable { document -> favoriteRepository.insert(document.document, index) }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())

                }
    }

}