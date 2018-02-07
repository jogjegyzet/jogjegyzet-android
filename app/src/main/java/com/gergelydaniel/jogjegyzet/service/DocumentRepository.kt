package com.gergelydaniel.jogjegyzet.service

import android.util.LruCache
import com.gergelydaniel.jogjegyzet.api.ApiClient
import com.gergelydaniel.jogjegyzet.domain.Document
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepository @Inject constructor(private val apiClient: ApiClient) {
    private val cache = LruCache<String, Document>(30)

    fun getDocumentsInCategory(categoryId : String) : Observable<List<Document>> =
            apiClient.getDocumentsInCategory(categoryId)
                    .toObservable()
                    .doOnNext { it.forEach { cache.put(it.id, it) } }

    fun getDocument(id : String) : Observable<Document> {
        var obs = apiClient.getDocument(id).toObservable()
        val cached : Document? = cache.get(id)
        if(cached != null) {
            obs = obs.startWith(cached)
        }
        return obs
    }
}