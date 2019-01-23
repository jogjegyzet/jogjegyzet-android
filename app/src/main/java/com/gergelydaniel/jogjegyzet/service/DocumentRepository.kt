package com.gergelydaniel.jogjegyzet.service

import android.util.Log
import android.util.LruCache
import com.gergelydaniel.jogjegyzet.api.ApiClient
import com.gergelydaniel.jogjegyzet.domain.Document
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(private val apiClient: ApiClient,
                                             private val favoriteRepository: FavoriteRepository) {
    private val cache = LruCache<String, Document>(30)

    fun getDocumentsInCategory(categoryId: String): Observable<List<Document>> =
            apiClient.getDocumentsInCategory(categoryId)
                    .doOnSuccess { it.forEach { cache.put(it.id, it) } }
                    .flatMapObservable {
                        favoriteRepository.updateIfExists(it)
                                .subscribeOn(Schedulers.io())
                                .andThen(Observable.just(it))
                    }


    fun getDocument(id: String): Observable<Document> {
        return Observable.defer {
            val networkObs = apiClient.getDocument(id)
                    .flatMapObservable {
                        favoriteRepository.updateIfExists(it)
                                .subscribeOn(Schedulers.io())
                                .andThen(Observable.just(it))
                    }

            var obs =
                    favoriteRepository.getById(id)
                            .toObservable()
                            .flatMap {
                                networkObs
                                        .startWith(it)
                                        .onErrorResumeNext(Observable.empty<Document>())
                            }
                            .switchIfEmpty(networkObs)


            val cached: Document? = cache.get(id)
            if (cached != null) {
                obs = obs.startWith(cached)
            }
            obs
                    .doOnError { Log.e("getDoc", "ERROROROR", it) }
        }
    }
}