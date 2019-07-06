package com.danielgergely.jogjegyzet.service

import android.util.LruCache
import com.danielgergely.jogjegyzet.api.ApiClient
import com.danielgergely.jogjegyzet.domain.Document
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(private val apiClient: ApiClient,
                                             private val favoriteRepository: FavoriteRepository) {
    private val cache = LruCache<String, DocumentData>(30)

    fun getDocumentsInCategory(categoryId: String): Observable<List<DocumentData>> =
            apiClient.getDocumentsInCategory(categoryId)
                    .flatMap { docs ->
                        favoriteRepository.updateIfExists(docs)
                                .subscribeOn(Schedulers.io())
                                .map { bools -> docs.zip(bools) { doc, b -> DocumentData(doc, b) } }
                    }
                    .doOnSuccess { it.forEach { cache.put(it.document.id, it) } }
                    .toObservable()


    fun getDocument(id: String): Observable<DocumentData> {
        return Observable.defer {
            val networkObs = apiClient.getDocument(id)
                    .flatMap { doc ->
                        favoriteRepository.updateIfExists(doc)
                                .subscribeOn(Schedulers.io())
                                .map { DocumentData(doc, it) }
                    }.toObservable()

            var obs =
                    favoriteRepository.getById(id)
                            .toObservable()
                            .flatMap {
                                networkObs
                                        .startWith(DocumentData(it, true))
                                        .onErrorResumeNext(Observable.empty<DocumentData>())
                            }
                            .switchIfEmpty(networkObs)


            val cached: DocumentData? = cache.get(id)
            if (cached != null) {
                obs = obs.startWith(cached)
            }
            obs
        }
    }
}

class DocumentData(val document: Document, val isInFavorites: Boolean)