package com.danielgergely.jogjegyzet.ui.reader

import com.danielgergely.jogjegyzet.service.DocumentData
import com.danielgergely.jogjegyzet.service.DocumentRepository
import com.danielgergely.jogjegyzet.service.FavoriteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ReaderPresenter @Inject constructor(private val documentRepository: DocumentRepository,
                                          private val favoriteRepository: FavoriteRepository) {
    private val docSubject: BehaviorSubject<DocumentData> = BehaviorSubject.create()
    private val refreshSubject = PublishSubject.create<Any>()

    fun getViewModel(docId: String): Observable<DocumentData> {
        return documentRepository.getDocument(docId)
                .subscribeOn(Schedulers.io())
                .repeatWhen { refreshSubject }
                .doOnNext(docSubject::onNext)
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun addToFavorites(): Completable {
        return docSubject
                .filter { it != null }
                .take(1)
                .flatMapCompletable { favoriteRepository.insert(it.document).subscribeOn(Schedulers.io()) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { refreshSubject.onNext(Any()) }
    }

    fun removeFromFavorites(): Completable {
        return docSubject
                .filter { it != null }
                .take(1)
                .flatMapCompletable { favoriteRepository.deleteById(it.document.id).subscribeOn(Schedulers.io()) }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { refreshSubject.onNext(Any()) }
    }

    fun undoAddToFavourites(): Completable = removeFromFavorites()

    fun undoRemoveFromFavourites(): Completable {
        return Completable.complete()
    }
}