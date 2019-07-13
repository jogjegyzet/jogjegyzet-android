package com.danielgergely.jogjegyzet.ui.reader

import com.danielgergely.jogjegyzet.domain.NoInternetException
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
import java.io.InputStream
import java.net.URL
import java.net.UnknownHostException
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

    private fun mapError(error: Throwable) = when(error) {
        is UnknownHostException -> NoInternetException()
        else -> error
    }

    fun getViewModel(docId: String): Observable<ViewModel> {
        return documentRepository.getDocument(docId)
                .subscribeOn(Schedulers.io())
                .repeatWhen { refreshSubject }
                .doOnNext(docSubject::onNext)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { docData ->
                    Observable.fromCallable { URL(docData.document.fileUrl).openStream() }
                            .subscribeOn(Schedulers.io())
                            .map { stream -> ViewModel.Data(docData, stream) as ViewModel }
                }.startWith(ViewModel.Loading)
                .onErrorReturn { ViewModel.Error(mapError(it)) }
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

sealed class ViewModel {
    object Loading : ViewModel()
    class Error(val error: Throwable): ViewModel()
    class Data(val document: DocumentData, val stream: InputStream) : ViewModel()

}